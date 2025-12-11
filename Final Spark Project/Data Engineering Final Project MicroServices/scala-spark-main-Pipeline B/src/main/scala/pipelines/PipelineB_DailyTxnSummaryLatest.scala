package pipelines

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object PipelineB_DailyTxnSummaryLatest {

  def main(args: Array[String]): Unit = {

    // ----------------------------
    // Spark Session
    // ----------------------------
    val spark = SparkSession.builder()
      .appName("PipelineB_DailyTxnSummary")
      .master("local[*]")

      // REQUIRED for writing to S3
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.access.key", "abc")
      .config("spark.hadoop.fs.s3a.secret.key", "abc")

      // Stockholm region
      .config("spark.hadoop.fs.s3a.endpoint", "s3.eu-north-1.amazonaws.com")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")
      .getOrCreate()

    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    println("Pipeline B — Daily Transaction Summary started.\n")

    // ----------------------------
    // Compute YESTERDAY DATE
    // ----------------------------
    val yesterday = java.time.LocalDate.now.minusDays(1).toString
    println(s"[PipelineB] Processing transactions for date = $yesterday")

    // ----------------------------
    // MySQL JDBC Setup
    // ----------------------------
    val jdbcUrl = "jdbc:mysql://database-1.cev02em2c3cb.us-east-1.rds.amazonaws.com:3306/ecommerce"

    val connectionProps = new java.util.Properties()
    connectionProps.setProperty("user", "admin")
    connectionProps.setProperty("password", "Jaisadguru")
    connectionProps.setProperty("driver", "com.mysql.cj.jdbc.Driver")

    // ----------------------------
    // Read from MySQL
    // ----------------------------
    val transactions = spark.read.jdbc(jdbcUrl, "transactions", connectionProps)
    val products = spark.read.jdbc(jdbcUrl, "products", connectionProps)

    // ----------------------------
    // Add date & filter for YESTERDAY
    // ----------------------------
    val txnsWithDate = transactions
      .withColumn("date", to_date(col("txn_timestamp")))
      .filter(col("date").isNotNull)
      //.filter(col("date") === lit(yesterday))

    println(s"[PipelineB] Loaded ${txnsWithDate.count()} txns for $yesterday")

    // ----------------------------
    // Data Quality Filtering
    // ----------------------------
    val validTxns = txnsWithDate.filter(col("product_id").isNotNull)
    val invalidTxns = txnsWithDate.filter(col("product_id").isNull)

    if (invalidTxns.count() > 0) {
      println(s"[PipelineB] Invalid records found (null product_id): ${invalidTxns.count()}")
      invalidTxns.show(20, false)
    }

    // ----------------------------
    // Join With Products
    // ----------------------------
    val productsRenamed = products
      .withColumnRenamed("category", "product_category")

    val joined = validTxns.join(productsRenamed, "product_id")

    // ----------------------------
    // Calculate MODE (top_category)
    // ----------------------------
    val categoryCount = joined
      .groupBy("date", "customer_id", "product_category")
      .agg(count("*").as("cnt"))

    val w = Window.partitionBy("date", "customer_id").orderBy(col("cnt").desc)

    val categoryMode = categoryCount
      .withColumn("rank", row_number().over(w))
      .filter(col("rank") === 1)
      .select(
        col("date"),
        col("customer_id"),
        col("product_category").as("top_category")
      )

    // ----------------------------
    // Daily Aggregation
    // ----------------------------
    val dailyAgg = joined
      .groupBy("date", "customer_id")
      .agg(
        sum("amount").as("total_amount"),
        sum("qty").as("total_items"),
        countDistinct("product_id").as("distinct_products")
      )
      .join(categoryMode, Seq("date", "customer_id"))

    // Preview sample
    println("[PipelineB] Sample aggregated summary:")
    dailyAgg.show(10, false)
    val sampleAgg = dailyAgg.limit(50)
    // ----------------------------
    // Write final output to S3
    // ----------------------------
    val outputPath = "s3a://ritik-s3/lake/txn_summary/"

    println(s"[PipelineB] Writing results to S3 → $outputPath")

    sampleAgg.write
      .mode("append")
      .partitionBy("date")
      .option("compression", "snappy")
      .parquet(outputPath)

    println("\nPipeline B (Daily Transaction Summary) completed successfully!")
    spark.stop()
  }
}
