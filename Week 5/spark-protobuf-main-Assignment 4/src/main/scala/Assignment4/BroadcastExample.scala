package Assignment4

import org.apache.spark.sql.SparkSession

object BroadcastExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("BroadcastExample")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext

    // ------------------------------
    // 1. Small dataset → Exchange Rates (Currency → USD rate)
    // ------------------------------
    val exchangeRates = Map(
      "USD" -> 1.0,
      "EUR" -> 1.08,
      "INR" -> 0.012,
      "GBP" -> 1.25
    )

    // Broadcast this small lookup table
    val broadcastRates = sc.broadcast(exchangeRates)

    // ------------------------------
    // 2. Large dataset → Transactions
    // Format: (transactionId, amount, currency)
    // ------------------------------
    val transactionsRDD = sc.parallelize(Seq(
      ("t1", 100.0, "USD"),
      ("t2", 250.0, "EUR"),
      ("t3", 5000.0, "INR"),
      ("t4", 80.0, "GBP"),
      ("t5", 60.0, "EUR")
    ))

    // ------------------------------
    // 3. Convert all transactions to USD using broadcast
    // ------------------------------
    val convertedRDD = transactionsRDD.map { case (id, amount, currency) =>
      val rate = broadcastRates.value(currency)
      val amountInUSD = amount * rate
      (id, amountInUSD, currency)
    }

    // ------------------------------
    // 4. Count transactions per currency
    // ------------------------------
    val countPerCurrency = convertedRDD
      .map { case (_, _, currency) => (currency, 1) }
      .reduceByKey(_ + _)

    // Print outputs
    println("Converted Transactions:")
    convertedRDD.collect().foreach(println)

    println("\nTransactions Per Currency:")
    countPerCurrency.collect().foreach(println)

    spark.stop()
  }
}

