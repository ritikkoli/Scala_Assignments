package Assignment

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object ParquetToJsonAggregation extends App {

  println("🚀 Starting Pipeline 3: Parquet → Aggregation → JSON")

  val spark = SparkSession.builder()
    .appName("ParquetToJsonAggregation")
    .master("local[*]")
    .getOrCreate()

  // ---- ADD S3 CREDENTIALS ----
  spark.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", "xxxxx")
  spark.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", "xxxxx")
  spark.sparkContext.hadoopConfiguration.set("fs.s3a.endpoint", "s3.eu.com")

  // ---------------------------------------------
  // 1️⃣ READ PARQUET FROM S3
  // ---------------------------------------------
  println("📥 Reading Parquet files from S3...")

  val parquetDF = spark.read
    .parquet("s3a://xxxxx-s3/sales/parquet/")

  parquetDF.show(10, false)

  // ---------------------------------------------
  // 2️⃣ COMPUTE AGGREGATION
  // ---------------------------------------------
  println("🧮 Computing totals per product...")

  val aggDF = parquetDF
    .groupBy("product_name")
    .agg(
      sum("quantity").alias("total_quantity"),
      sum("amount").alias("total_revenue")
    )
    .orderBy(desc("total_revenue"))

  aggDF.show(20, false)

  // ---------------------------------------------
  // 3️⃣ WRITE JSON TO S3
  // ---------------------------------------------
  val outputPath = "s3a://xxxxxx-s3/aggregates/products.json"

  println(s"💾 Writing JSON output to: $outputPath")

  aggDF.coalesce(1)
    .write
    .mode("overwrite")
    .json(outputPath)

  println("✅ JSON aggregation job completed successfully!")

  spark.stop()
}
