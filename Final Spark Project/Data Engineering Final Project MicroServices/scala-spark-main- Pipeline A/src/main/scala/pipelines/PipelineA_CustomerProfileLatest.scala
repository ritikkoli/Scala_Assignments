package pipelines

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object PipelineA_CustomerProfileLatest {

  def main(args: Array[String]): Unit = {

    // ----------------------------
    // Configurable paths & flags
    // ----------------------------
    val invalidOutputPath = sys.props.getOrElse("pipelineA.invalid.path", "/tmp/pipelineA/invalid/")
    val cassandraKeyspace = sys.props.getOrElse("pipelineA.cassandra.keyspace", "ecommerce_ks")
    val cassandraTable = sys.props.getOrElse("pipelineA.cassandra.table", "customer_profile")
    val jdbcUrl = sys.props.getOrElse("pipelineA.jdbc.url", "jdbc:mysql://path:3306/ecommerce")
    val jdbcUser = sys.props.getOrElse("pipelineA.jdbc.user", "admin")
    val jdbcPass = sys.props.getOrElse("pipelineA.jdbc.password", "abc")

    // ----------------------------
    // Spark Session
    // ----------------------------
    val spark = SparkSession.builder()
      .appName("PipelineA_CustomerProfile")
      .master("local[*]")

      // Cassandra connection configs (example)
      .config("spark.cassandra.connection.host", sys.props.getOrElse("pipelineA.cassandra.host", "cassandra.us-east-1.amazonaws.com"))
      .config("spark.cassandra.connection.port", sys.props.getOrElse("pipelineA.cassandra.port", "9142"))
      .config("spark.cassandra.connection.ssl.enabled", sys.props.getOrElse("pipelineA.cassandra.ssl", "true"))
      .config("spark.cassandra.auth.username", sys.props.getOrElse("pipelineA.cassandra.user", "abc"))
      .config("spark.cassandra.auth.password", sys.props.getOrElse("pipelineA.cassandra.password", "abc"))
      // output consistency (optional)
      .config("spark.cassandra.output.consistency.level", "LOCAL_QUORUM")
      .getOrCreate()

    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    // ----------------------------
    // Compute last-5-years cut-off dynamically
    // ----------------------------
    val cutoffDate = LocalDate.now().minusYears(5)
    val cutoffString = cutoffDate.format(DateTimeFormatter.ISO_LOCAL_DATE) // yyyy-MM-dd

    println(s"[PipelineA] Using cutoff date for transactions: $cutoffString")

    // ----------------------------
    // JDBC connection properties
    // ----------------------------
    val connectionProps = new java.util.Properties()
    connectionProps.setProperty("user", jdbcUser)
    connectionProps.setProperty("password", jdbcPass)
    connectionProps.setProperty("driver", "com.mysql.cj.jdbc.Driver")

    // ----------------------------
    // Read source tables
    // ----------------------------
    val customersRaw = spark.read
      .jdbc(jdbcUrl, "customers", connectionProps)
      .withColumnRenamed("id", "customer_id") // defensive rename if needed

    val productsRaw = spark.read
      .jdbc(jdbcUrl, "products", connectionProps)
      .withColumnRenamed("name", "product_name")
      .withColumnRenamed("category", "product_category")

    // Transactions: apply last-5-years filter, dedupe, cast amount
    val transactionsRaw = spark.read
      .jdbc(jdbcUrl, "transactions", connectionProps)
      .withColumnRenamed("id", "txn_id") // defensive rename if needed
      // Ensure timestamp column is present and castable
      .withColumn("txn_timestamp", to_timestamp(col("txn_timestamp")))
      .filter(col("txn_timestamp").isNotNull)
      .filter(col("txn_timestamp") >= lit(cutoffString))
      .dropDuplicates("txn_id")
      // Cast amount to decimal for safe aggregations
      .withColumn("amount", col("amount").cast(DecimalType(18, 2)))
      // ensure qty numeric if present
      .withColumn("qty", when(col("qty").isNull, lit(0)).otherwise(col("qty").cast(IntegerType)))

    // ----------------------------
    // DATA QUALITY CHECKS + LOGGING
    // ----------------------------

    // Identify missing customer_id
    val missingCustomer = transactionsRaw.filter(col("customer_id").isNull)
    val missingProduct = transactionsRaw.filter(col("product_id").isNull)
    val negativeAmount = transactionsRaw.filter(col("amount").isNotNull && col("amount") < 0)

    val invalidUnion = missingCustomer.union(missingProduct).union(negativeAmount).dropDuplicates("txn_id")

    val invalidCount = invalidUnion.count()
    println(s"[PipelineA] Invalid records found: $invalidCount")
    if (invalidCount > 0) {
      println("[PipelineA] Writing invalid records for inspection to: " + invalidOutputPath)
      // Persist invalid records for later inspection (parquet)
      invalidUnion
        .withColumn("validation_error", when(col("customer_id").isNull, lit("missing_customer_id"))
          .when(col("product_id").isNull, lit("missing_product_id"))
          .when(col("amount") < 0, lit("negative_amount"))
          .otherwise(lit("other")))
        .write.mode("overwrite").parquet(invalidOutputPath)
    } else {
      println("[PipelineA] No invalid records detected.")
    }

    // Filter invalid rows out of the main transactions stream
    val transactions = transactionsRaw
      .except(invalidUnion) // remove invalid rows
      .filter(col("customer_id").isNotNull && col("product_id").isNotNull)

    // ----------------------------
    // Join datasets
    // ----------------------------
    // Join transactions -> customers -> products
    val txCust = transactions.join(customersRaw, Seq("customer_id"), "inner")
    val txCustProd = txCust.join(productsRaw, Seq("product_id"), "inner")

    // ----------------------------
    // Aggregations per customer
    // ----------------------------
    // Basic aggregations
    val customerAgg = txCustProd
      .groupBy(
        col("customer_id"),
        col("name").as("customer_name"),
        col("email"),
        col("gender")
      )
      .agg(
        sum(col("amount")).as("total_spend"),
        count(lit(1)).as("total_transactions"),
        min(col("txn_timestamp")).as("first_purchase"),
        max(col("txn_timestamp")).as("last_purchase")
      )
      .withColumn("avg_order_value",
        when(col("total_transactions") === 0, lit(0.00))
          .otherwise(round(col("total_spend") / col("total_transactions"), 2))
      )

    // ----------------------------
    // Favorite category (MODE) computation
    // ----------------------------
    // Count purchases per (customer_id, product_category)
    val custCategoryCounts = txCustProd
      .groupBy("customer_id", "product_category")
      .agg(count(lit(1)).as("cnt"))

    // Rank categories per customer by count desc, pick top-1 (tie broken arbitrarily by product_category)
    val w = Window.partitionBy("customer_id").orderBy(col("cnt").desc, col("product_category").asc)
    val favoriteCategory = custCategoryCounts
      .withColumn("rank", row_number().over(w))
      .filter(col("rank") === 1)
      .select(col("customer_id"), col("product_category").as("favorite_category"))

    // ----------------------------
    // Combine aggregations + favorite category
    // ----------------------------
    val finalProfile = customerAgg
      .join(favoriteCategory, Seq("customer_id"), "left")
      // Add ingestion timestamp and other metadata
      .withColumn("ingestion_timestamp", current_timestamp())
      // Defensive typing / null handling
      .withColumn("favorite_category", coalesce(col("favorite_category"), lit("UNKNOWN")))
      // Ensure numeric types are well-typed
      .withColumn("total_spend", col("total_spend").cast(DecimalType(18,2)))
      .withColumn("total_transactions", col("total_transactions").cast(IntegerType))

    // ----------------------------
    // Optional: Validate final rows count and sample
    // ----------------------------
    val profileCount = finalProfile.count()
    println(s"[PipelineA] Final profile rows to write: $profileCount")
    finalProfile.show(5, truncate = false)

    // ----------------------------
    // Write to Cassandra (upsert semantics)
    // ----------------------------
    println(s"[PipelineA] Writing to Cassandra keyspace=$cassandraKeyspace table=$cassandraTable")
    finalProfile.write
      .format("org.apache.spark.sql.cassandra")
      .option("keyspace", cassandraKeyspace)
      .option("table", cassandraTable)
      .mode("append") // Cassandra upsert: insert/overwrite by primary key
      .save()

    println("[PipelineA] Pipeline A completed successfully!")

    spark.stop()
  }
}
