package Assignment4

import org.apache.spark.sql.SparkSession

object AccumulatorExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("AccumulatorExample")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext

    // ------------------------------
    // Threshold for high-value transactions
    // ------------------------------
    val threshold = 500.0

    // ------------------------------
    // 1. Create accumulator
    // ------------------------------
    val highValueAcc = sc.longAccumulator("HighValueTransactionCounter")

    // ------------------------------
    // Sample dataset (RDD of amounts)
    // ------------------------------
    val transactionsRDD = sc.parallelize(Seq(
      100.0, 250.0, 900.0, 1200.0, 50.0, 700.0, 40.0
    ))

    // ------------------------------
    // 2. Process dataset in parallel and update accumulator
    // ------------------------------
    val processedRDD = transactionsRDD.map { amount =>
      if (amount > threshold) {
        highValueAcc.add(1)    // <-- update accumulator
      }
      amount * 1.0             // dummy processing
    }

    // Must trigger an ACTION to execute the transformation
    processedRDD.collect()

    // ------------------------------
    // 3. Read accumulator value on driver
    // ------------------------------
    println(s"Number of transactions > $threshold: ${highValueAcc.value}")

    spark.stop()
  }
}

