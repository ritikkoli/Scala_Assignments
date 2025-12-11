package pipelines



import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types._
import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.io.DecoderFactory

object PipelineC_StreamingAvroLatest {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("PipelineC_Avro_Full")
      .master("local[*]")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.access.key", "abc")
      .config("spark.hadoop.fs.s3a.secret.key", "abc")
      .config("spark.hadoop.fs.s3a.endpoint", "s3.eu-north-1.amazonaws.com")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")
      .getOrCreate()

    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    // --------------------------------------------------
    // Load AVRO schema
    // --------------------------------------------------
    val schemaString = scala.io.Source.fromInputStream(
      PipelineC_StreamingAvro.getClass.getClassLoader.getResourceAsStream("event.avsc")
    ).mkString
    val avroSchema = new Schema.Parser().parse(schemaString)

    // --------------------------------------------------
    // Load CUSTOMER + PRODUCT master data (broadcast)
    // --------------------------------------------------
    //val jdbcUrl = "jdbc:mysql://database-1.xxxxxx.rds.amazonaws.com:3306/ecommerce"

//    val props = new java.util.Properties()
//    props.put("user", "admin")
//    props.put("password", "xxx")

    val jdbcUrl = "jdbc:mysql://database:3306/ecommerce"

    val props = new java.util.Properties()
    props.setProperty("user", "admin")
    props.setProperty("password", "abc")
    props.put("driver", "com.mysql.cj.jdbc.Driver")

    val customerDf = spark.read.jdbc(jdbcUrl, "customers", props).select("customer_id")
    val productDf  = spark.read.jdbc(jdbcUrl, "products", props).select("product_id")

    val customerSet = customerDf.collect().map(_.getInt(0)).toSet
    val productSet  = productDf.collect().map(_.getInt(0)).toSet

    val broadcastCustomers = spark.sparkContext.broadcast(customerSet)
    val broadcastProducts  = spark.sparkContext.broadcast(productSet)

    // --------------------------------------------------
    // Read Kafka (Avro binary)
    // --------------------------------------------------
    val kafkaDf = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "events_avro")
      .option("startingOffsets", "latest")
      .load()

    val decoderUdf = udf((bytes: Array[Byte]) => {
      val reader = new GenericDatumReader[Object](avroSchema)
      val decoder = DecoderFactory.get.binaryDecoder(bytes, null)
      val rec = reader.read(null, decoder)
      rec.toString
    })

    val jsonDf = kafkaDf
      .withColumn("json", decoderUdf(col("value")))
      .select("json")

    // --------------------------------------------------
    // Parse JSON into DataFrame
    // --------------------------------------------------
    val sparkSchema = StructType(Seq(
      StructField("event_id", StringType),
      StructField("customer_id", IntegerType),
      StructField("event_type", StringType),
      StructField("product_id", IntegerType),
      StructField("event_timestamp", StringType)
    ))

    val parsedDf = jsonDf
      .withColumn("parsed", from_json(col("json"), sparkSchema))
      .select("parsed.*")

    // --------------------------------------------------
    // Full Validation
    // --------------------------------------------------
    val validEventTypes = Seq("view", "wishlist", "cart_add", "purchase")

    val validateCustomerUdf = udf((id: Int) =>
      broadcastCustomers.value.contains(id)
    )

    val validateProductUdf = udf((id: Int) =>
      broadcastProducts.value.contains(id)
    )

    val validatedDf = parsedDf
      // detect unknown customer
      .withColumn("is_unknown_customer", !validateCustomerUdf(col("customer_id")))
      // unknown product → set null
      .withColumn("product_id",
        when(validateProductUdf(col("product_id")), col("product_id")).otherwise(null)
      )
      // must have event_type
      .filter(col("event_type").isNotNull)
      // event_type must be allowed
      .filter(col("event_type").isin(validEventTypes: _*))
      // timestamps
      .withColumn("event_time", to_timestamp(col("event_timestamp")))
      .withColumn("event_date", to_date(col("event_time")))
      .withColumn("ingestion_timestamp", current_timestamp())


    // --------------------------------------------------
    // Split valid + invalid customers (log unknown)
    // --------------------------------------------------
    val validDf = validatedDf.filter($"is_unknown_customer" === false)
    val unknownDf = validatedDf.filter($"is_unknown_customer" === true)

    // --------------------------------------------------
    // Write VALID events → S3
    // --------------------------------------------------
    val validQuery = validDf.writeStream
      .format("parquet")
      .option("path", "s3a://ritik-s3/lake/events/")
      .option("checkpointLocation", "s3a://ritik-s3/checkpoints/events/")
      .partitionBy("event_date")
      .outputMode("append")
      .trigger(Trigger.ProcessingTime("15 minutes"))
      .start()

    // --------------------------------------------------
    // Write UNKNOWN CUSTOMER events → S3 DLQ
    // --------------------------------------------------
    val unknownQuery = unknownDf.writeStream
      .format("json")
      .option("path", "s3a://ritik-s3/dlq/unknown_customers/")
      .option("checkpointLocation", "s3a://ritik-s3/checkpoints/dlq_customers/")
      .outputMode("append")
      .start()

    println("Pipeline C (FULLY SPEC COMPLIANT) is running...")

    validQuery.awaitTermination()
    unknownQuery.awaitTermination()
  }
}

