
package repository

import javax.inject.{Inject, Singleton}
import models.BehaviourEvent
import utils.S3ParquetReader
import utils.S3ParquetReader.decodeInt96

@Singleton
class BehaviourEventsRepository @Inject()() {

  private val basePath = "s3a://ritik-s3/lake/events"

  def getLastEvents(customerId: Int, limit: Int): Seq[BehaviourEvent] = {

    val folders = S3ParquetReader.listDateFolders(basePath)
    println("DATE FOLDERS FOUND = " + S3ParquetReader.listDateFolders(basePath))

    val sortedDateFolders =
      folders.sortBy(_.stripPrefix("event_date=").trim).reverse

    val recentFolders = sortedDateFolders.take(3)

    val records =
      recentFolders.flatMap(folder =>
        S3ParquetReader.readParquetFolder(basePath + "/" + folder)

      )
    println("Writing data to readParquetFolder")
    val filtered = records
      .filter(r => r.get("customer_id").toString.toInt == customerId)
      .sortBy(r => r.get("event_timestamp").toString)
      .reverse
      .take(limit)
    println("Writing data to json")



    filtered.map { r =>
      BehaviourEvent(
        event_id = Option(r.get("event_id")).map(_.toString),

        customer_id = Option(r.get("customer_id"))
          .map(_.toString.toInt),

        event_type = Option(r.get("event_type"))
          .map(_.toString),

        product_id = Option(r.get("product_id"))
          .map(_.toString.toInt),

        event_timestamp = Option(r.get("event_timestamp"))
          .map(_.toString),

        // Handle BOTH possible schema fields
        is_valid = Option(r.get("is_valid"))
          .orElse(Option(r.get("is_unknown_customer")))
          .map(_.toString.toBoolean),

        event_time = Option(r.get("event_time"))
          .map(_.asInstanceOf[Array[Byte]])
          .map(decodeInt96),

        ingestion_timestamp = Option(r.get("ingestion_timestamp")) match {
          case Some(bytes: Array[Byte]) => Some(decodeInt96(bytes))
          case Some(other)              => Some(other.toString)
          case None                     => None
        }
      )
    }

  }
}
