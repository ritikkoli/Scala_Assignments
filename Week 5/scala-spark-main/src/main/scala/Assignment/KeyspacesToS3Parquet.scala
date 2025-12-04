


package Assignment

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object KeyspacesToS3Parquet extends App {

  println("🚀 Starting Keyspaces → S3 Parquet job...")

  val spark = SparkSession.builder()
    .appName("KeyspacesToS3Parquet")
    .master("local[*]")

    // ---------------- Cassandra / Keyspaces ----------------
    .config("spark.cassandra.connection.host", "cassandraabc")
    .config("spark.cassandra.connection.port", "1234")
    .config("spark.cassandra.connection.ssl.enabled", "true")
    // Username / password
    .config("spark.cassandra.auth.username", "abc")
    .config("spark.cassandra.auth.password", "abc")
    .config("spark.cassandra.input.consistency.level", "LOCAL_QUORUM")
    .config("spark.cassandra.connection.ssl.trustStore.path", "/Users/xxx/cassandra_truststore.jks")
    .config("spark.cassandra.connection.ssl.trustStore.password", "changeit")

    // ---------------- S3 Configuration (IMPORTANT) ----------------
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .config("spark.hadoop.fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
    .config("spark.hadoop.fs.s3a.access.key", "xxxxx")
    .config("spark.hadoop.fs.s3a.secret.key", "xxxxx")

    // Your bucket is in: Europe (Stockholm) → eu-north-1
    .config("spark.hadoop.fs.s3a.endpoint", "s3.eu-north-1.amazonaws.com")
    .config("spark.hadoop.fs.s3a.path.style.access", "false")

    .getOrCreate()

  println("📡 Reading data from Amazon Keyspaces...")

  val df = spark.read
    .format("org.apache.spark.sql.cassandra")
    .option("keyspace", "abc")
    .option("table", "sales_data")
    .load()

  println("📊 Sample data:")
  df.show(10, truncate = false)

  println("🧮 Selecting required columns...")

  val finalDF = df.select(
    "customer_id",
    "order_id",
    "amount",
    "product_name",
    "quantity"
  )

  println("💾 Writing partitioned Parquet to S3 (eu-north-1)...")

  finalDF.write
    .mode("overwrite")
    .partitionBy("customer_id")
    .parquet("s3a://xxx-s3/sales/parquet/")

  println("🎉 Job completed successfully! Files saved to:")
  println("➡️ s3://xxx-s3/sales/parquet/")

  spark.stop()
}
