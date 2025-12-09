package Assignment4

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object CachingExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("CachingExample")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------------
    // 1. Create a large DataFrame (simulate sales dataset)
    // ------------------------------------------------------
    val salesDF = Seq(
      ("C1", "P1", 2, 100.0),
      ("C2", "P2", 1, 50.0),
      ("C1", "P3", 3, 200.0),
      ("C3", "P2", 4, 200.0),
      ("C2", "P1", 2, 100.0)
    ).toDF("customerId", "productId", "quantity", "amount")

    // ------------------------------------------------------
    // Without caching – measure execution time
    // ------------------------------------------------------
    val t1 = System.currentTimeMillis()

    val totalAmountNoCache = salesDF
      .groupBy("customerId")
      .agg(sum("amount").as("totalAmount"))
      .collect()

    val totalQtyNoCache = salesDF
      .groupBy("productId")
      .agg(sum("quantity").as("totalQuantity"))
      .collect()

    val t2 = System.currentTimeMillis()
    println(s"Execution time WITHOUT cache: ${t2 - t1} ms")

    // ------------------------------------------------------
    // 2. Cache the dataset
    // ------------------------------------------------------
    salesDF.cache()  // or salesDF.persist(StorageLevel.MEMORY_ONLY)

    // Trigger caching (Spark caches only AFTER an action)
    salesDF.count()

    // ------------------------------------------------------
    // With caching – measure execution time
    // ------------------------------------------------------
    val t3 = System.currentTimeMillis()

    val totalAmountCached = salesDF
      .groupBy("customerId")
      .agg(sum("amount").as("totalAmount"))
      .collect()

    val totalQtyCached = salesDF
      .groupBy("productId")
      .agg(sum("quantity").as("totalQuantity"))
      .collect()

    val t4 = System.currentTimeMillis()
    println(s"Execution time WITH cache: ${t4 - t3} ms")

    // ------------------------------------------------------
    // Print results
    // ------------------------------------------------------
    println("\nTotal Amount Per Customer:")
    totalAmountCached.foreach(println)

    println("\nTotal Quantity Per Product:")
    totalQtyCached.foreach(println)

    spark.stop()
  }
}

