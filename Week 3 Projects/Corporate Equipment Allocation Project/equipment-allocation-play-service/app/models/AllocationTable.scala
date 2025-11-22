package models

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

case class Allocation(
                       id: Long = 0L,
                       equipmentId: Long,
                       employeeId: Long,
                       allocatedBy: Option[Long],
                       allocationDate: LocalDateTime = LocalDateTime.now(),
                       expectedReturnDate: LocalDateTime,
                       actualReturnDate: Option[LocalDateTime],
                       purpose: Option[String],
                       returnCondition: Option[String],
                       status: String = "ALLOCATED",
                       notes: Option[String]
                     )

class AllocationTable(tag: Tag) extends Table[Allocation](tag, "allocation") {

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
  def employeeId = column[Long]("employee_id")
  def allocatedBy = column[Option[Long]]("allocated_by")
  def allocationDate = column[LocalDateTime]("allocation_date")
  def expectedReturnDate = column[LocalDateTime]("expected_return_date")
  def actualReturnDate = column[Option[LocalDateTime]]("actual_return_date")
  def purpose = column[Option[String]]("purpose")
  def returnCondition = column[Option[String]]("return_condition")
  def status = column[String]("status")
  def notes = column[Option[String]]("notes")

  def * = (
    id,
    equipmentId,
    employeeId,
    allocatedBy,
    allocationDate,
    expectedReturnDate,
    actualReturnDate,
    purpose,
    returnCondition,
    status,
    notes
  ).mapTo[Allocation]
}
