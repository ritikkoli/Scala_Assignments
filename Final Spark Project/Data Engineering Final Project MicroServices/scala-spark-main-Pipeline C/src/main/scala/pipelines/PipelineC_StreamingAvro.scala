package pipelines

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.io.DecoderFactory

object PipelineC_StreamingAvro {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("PipelineC_Avro")
      .master("local[*]")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.access.key", "abc")
      .config("spark.hadoop.fs.s3a.secret.key", "abc")
      .config("spark.hadoop.fs.s3a.endpoint", "s3.eu-north-1.amazonaws.com")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    // -----------------------------------------
    // Load AVRO schema
    // Fix ambiguity problem using class loader
    // -----------------------------------------
    val schemaStream =
      PipelineC_StreamingAvro.getClass.getClassLoader.getResourceAsStream("event.avsc")

    val schemaString = scala.io.Source.fromInputStream(schemaStream).mkString
    val avroSchema = new Schema.Parser().parse(schemaString)

    // -----------------------------------------
    // Read from Kafka
    // -----------------------------------------
    val kafkaDf = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "events_avro")
      .option("startingOffsets", "latest")
      .load()

    val valueDf = kafkaDf.select(col("value"))

    // -----------------------------------------
    // UDF: Decode Avro → JSON string
    // -----------------------------------------
    val decodeAvroUdf = udf((bytes: Array[Byte]) => {
      val reader = new GenericDatumReader[Object](avroSchema)
      val decoder = DecoderFactory.get.binaryDecoder(bytes, null)
      val record = reader.read(null, decoder)
      record.toString
    })

    val jsonDf = valueDf
      .withColumn("json_str", decodeAvroUdf(col("value")))
      .select("json_str")

    // -----------------------------------------
    // Parse JSON into columns
    // -----------------------------------------
    val sparkSchema = StructType(Seq(
      StructField("event_id", StringType),
      StructField("customer_id", IntegerType),
      StructField("event_type", StringType),
      StructField("product_id", IntegerType),
      StructField("event_timestamp", StringType)
    ))

    val parsedDf = jsonDf
      .withColumn("parsed", from_json(col("json_str"), sparkSchema))
      .select("parsed.*")

    println("Running pipeline with validation...")

    // -----------------------------------------
    // Data Validation Rules
    // -----------------------------------------

    val validEventType = Seq("view", "add_to_cart", "purchase")

    val validatedDf = parsedDf
      .withColumn("is_valid",
        col("event_id").isNotNull &&
          col("customer_id").isNotNull &&
          col("product_id").isNotNull &&
          col("event_type").isNotNull &&
          col("event_timestamp").isNotNull &&
          col("customer_id") > 0 &&
          col("product_id") > 0 &&
          col("event_type").isin(validEventType: _*) &&
          length(col("event_id")) > 10 &&
          to_timestamp(col("event_timestamp")).isNotNull
      )
      .withColumn("event_time", to_timestamp(col("event_timestamp")))
      .withColumn("event_date", to_date(col("event_time")))
      .withColumn("ingestion_timestamp", current_timestamp())

    // valid rows → S3 lake
    val validDf = validatedDf.filter(col("is_valid") === true)

    // invalid rows → DLQ
    val invalidDf = validatedDf.filter(col("is_valid") === false)

    // -----------------------------------------
    // Write Valid Records → S3
    // -----------------------------------------
    val validQuery = validDf.writeStream
      .format("parquet")
      .option("path", "s3a://ritik-s3/lake/events/")
      .option("checkpointLocation", "s3a://Data Engineering Final Project MicroServices/checkpoints/events/")
      .partitionBy("event_date")
      .outputMode("append")
      .start()

    // -----------------------------------------
    // Write Invalid Records → DLQ
    // -----------------------------------------
    val dlqQuery = invalidDf.writeStream
      .format("json")
      .option("path", "s3a://ritik-s3/dlq/events/")
      .option("checkpointLocation", "s3a://ritik-s3/checkpoints/dlq/")
      .outputMode("append")
      .start()

    println("Pipeline C (Avro → Validated → S3 + DLQ) RUNNING...")

    validQuery.awaitTermination()
    dlqQuery.awaitTermination()
  }
}
