package models

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

case class Notification(
                         id: Long = 0L,
                         eventType: String,
                         recipient: String,
                         recipientType: String,
                         notificationChannel: String,
                         payload: String,
                         status: String = "PENDING",
                         retryCount: Int = 0,
                         createdAt: LocalDateTime = LocalDateTime.now(),
                         sentAt: Option[LocalDateTime],
                         failureReason: Option[String],
                         relatedAllocationId: Option[Long],
                         relatedEquipmentId: Option[Long],
                         relatedMaintenanceId: Option[Long]
                       )

class NotificationTable(tag: Tag) extends Table[Notification](tag, "notification") {
  import java.time.LocalDateTime
  import java.time.format.DateTimeFormatter

  private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, String](
      ldt => ldt.format(dateTimeFormat),              // Scala → DB
      str => LocalDateTime.parse(str, dateTimeFormat) // DB → Scala
    )


  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def eventType = column[String]("event_type")
  def recipient = column[String]("recipient")
  def recipientType = column[String]("recipient_type")
  def notificationChannel = column[String]("notification_channel")
  def payload = column[String]("payload")
  def status = column[String]("status")
  def retryCount = column[Int]("retry_count")
  def createdAt = column[LocalDateTime]("created_at")
  def sentAt = column[Option[LocalDateTime]]("sent_at")
  def failureReason = column[Option[String]]("failure_reason")
  def relatedAllocationId = column[Option[Long]]("related_allocation_id")
  def relatedEquipmentId = column[Option[Long]]("related_equipment_id")
  def relatedMaintenanceId = column[Option[Long]]("related_maintenance_id")

  def * = (
    id,
    eventType,
    recipient,
    recipientType,
    notificationChannel,
    payload,
    status,
    retryCount,
    createdAt,
    sentAt,
    failureReason,
    relatedAllocationId,
    relatedEquipmentId,
    relatedMaintenanceId
  ).mapTo[Notification]
}
