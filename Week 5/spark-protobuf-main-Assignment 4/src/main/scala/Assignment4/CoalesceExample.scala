package Assignment4

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._

object CoalesceExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("CoalesceExample")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // --------------------------------------------------------------
    // Sample large dataset with many partitions
    // --------------------------------------------------------------
    val logsDF = spark.range(1, 10000000)
      .withColumn("level", when($"id" % 10 === 0, "ERROR").otherwise("INFO"))
      .repartition(50)   // simulate large input

    // Filter -> drastically reduces size
    val filtered = logsDF.filter($"level" === "ERROR")

    println(s"Filtered Partitions Before Coalesce: ${filtered.rdd.getNumPartitions}")

    // --------------------------------------------------------------
    // 1. Reduce number of partitions using coalesce
    // --------------------------------------------------------------
    val reduced = filtered.coalesce(5)

    println(s"Partitions After Coalesce: ${reduced.rdd.getNumPartitions}")

    // --------------------------------------------------------------
    // 3. Measure write performance & file count
    // --------------------------------------------------------------
    val t1 = System.currentTimeMillis()

    // Write output
    reduced.write.mode("overwrite").parquet("output/error_logs")

    val t2 = System.currentTimeMillis()

    println(s"Write time with coalesce: ${t2 - t1} ms")

    spark.stop()
  }
}

