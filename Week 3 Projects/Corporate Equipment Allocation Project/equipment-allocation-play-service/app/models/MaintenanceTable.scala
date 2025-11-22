package models

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

case class MaintenanceLog(
                           id: Long = 0L,
                           equipmentId: Long,
                           allocationId: Long,
                           reportedDate: LocalDateTime = LocalDateTime.now(),
                           reportedBy: Option[Long],
                           issueType: String,
                           description: Option[String],
                           severity: Option[String],
                           status: String = "OPEN",
                           resolvedDate: Option[LocalDateTime],
                           notes: Option[String]
                         )

class MaintenanceLogTable(tag: Tag) extends Table[MaintenanceLog](tag, "maintenance_log") {
  import java.time.LocalDateTime
  import java.time.format.DateTimeFormatter

  private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, String](
      ldt => ldt.format(dateTimeFormat),              // Scala → DB
      str => LocalDateTime.parse(str, dateTimeFormat) // DB → Scala
    )


  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def equipmentId = column[Long]("equipment_id")
  def allocationId = column[Long]("allocation_id")
  def reportedDate = column[LocalDateTime]("reported_date")
  def reportedBy = column[Option[Long]]("reported_by")
  def issueType = column[String]("issue_type")
  def description = column[Option[String]]("description")
  def severity = column[Option[String]]("severity")
  def status = column[String]("status")
  def resolvedDate = column[Option[LocalDateTime]]("resolved_date")
  def notes = column[Option[String]]("notes")

  def * = (
    id,
    equipmentId,
    allocationId,
    reportedDate,
    reportedBy,
    issueType,
    description,
    severity,
    status,
    resolvedDate,
    notes
  ).mapTo[MaintenanceLog]
}
