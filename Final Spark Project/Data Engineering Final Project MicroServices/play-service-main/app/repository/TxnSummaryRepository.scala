package repository

import models.DailyTxnSummary
import utils.S3ParquetReader
import javax.inject.{Inject, Singleton}

@Singleton
class TxnSummaryRepository @Inject() () {

  private val basePath =
    "s3a://ritik-s3/lake/txn_summary"

  def getDailySummary(date: String, customerId: Int): Seq[DailyTxnSummary] = {

    val parquetPath = s"$basePath/date=$date"
    //val parquetFolder = s"$basePath/date=$date/"
    //val records = S3ParquetReader.readParquet(parquetFolder)

   //val records = S3ParquetReader.readParquet(parquetPath)
    val records = S3ParquetReader.readParquetFolder(s"$basePath/date=$date/")

    records.flatMap { r =>
      try {
        val cid = r.get("customer_id").toString.toInt
        if (cid == customerId)
          Some(
            DailyTxnSummary(
              date,
              cid,
              BigDecimal(r.get("total_amount").toString),
              r.get("total_items").toString.toInt,
              r.get("distinct_products").toString.toInt,
              r.get("top_category").toString
            )
          )
        else None
      } catch {
        case _: Throwable => None
      }
    }
  }
}
