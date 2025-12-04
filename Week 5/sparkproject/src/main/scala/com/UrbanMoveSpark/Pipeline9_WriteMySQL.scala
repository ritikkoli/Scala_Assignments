////package com.UrbanMoveSpark
////
////import org.apache.spark.sql.SparkSession
////
////object Pipeline9_WriteMySQL extends App {
////
////  val spark = SparkSession.builder.master("local[*]").appName("Write MySQL").getOrCreate()
////
////  val df = spark.read.parquet("parquet/final_trips.parquet")
////
////  df.write
////    .format("jdbc")
////    .option("url", "jdbc:mysql://azuremysql8823.mysql.database.azure.com:3306/ritik")
////    .option("dbtable", "trip_summary_latest")
////    .option("user", "mysqladmin")
////    .option("password", "Password@12345")
////    .mode("append")
////    .save()
////
////  spark.stop()
////}
//
//
//package com.UrbanMoveSpark
//
//import org.apache.spark.sql.SparkSession
//
//object Pipeline9_WriteMySQL extends App {
//
//  val spark = SparkSession.builder
//    .master("local[*]")
//    .appName("Write MySQL")
//    .getOrCreate()
//
//  val df = spark.read.parquet("parquet/final_trips.parquet")
//
//  df.limit(10)
//    .write
//    .format("jdbc")
//    .option("url", "jdbc:mysql://azuremysql8823.mysql.database.azure.com:3306/ritik?useSSL=true&requireSSL=true")
//    .option("dbtable", "trip_summary_latest")
//    .option("user", "mysqladmin")
//    .option("password", "Password@12345")
//    .option("driver", "com.mysql.cj.jdbc.Driver")   // 🔥 REQUIRED
//    .mode("append")
//    .save()
//
//  spark.stop()
//}


package com.UrbanMoveSpark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Pipeline9_WriteMySQL extends App {

  val spark = SparkSession.builder
    .master("local[*]")
    .appName("Write MySQL")
    .getOrCreate()

  val df = spark.read.parquet("parquet/final_trips.parquet")

  println("=== DF Loaded from final_trips.parquet ===")
  df.show(5, false)
  df.printSchema()

  // 🔥 DROP leftover technical columns if they exist
  val dfClean = df
    .drop("start_ts")
    .drop("end_ts")

  println("=== CLEAN DF BEFORE MYSQL WRITE ===")
  dfClean.show(5, false)
  dfClean.printSchema()

  dfClean.limit(10)
    .write
    .format("jdbc")
    .option("url", "jdbc:mysql://azuremysql8823.mysql.database.azure.com:3306/ritik?useSSL=true")
    .option("dbtable", "trip_summary_latest")
    .option("user", "mysqladmin")
    .option("password", "Password@12345")
    .option("driver", "com.mysql.cj.jdbc.Driver")
    .mode("append")
    .save()

  println("=== SUCCESSFULLY WRITTEN TO MYSQL ===")

  spark.stop()
}
