package models

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

case class Equipment(
                      id: Long = 0L,
                      assetTag: Option[String],
                      equipmentType: String,
                      model: Option[String],
                      serialNo: Option[String],
                      conditionStatus: String = "GOOD",
                      isAvailable: Boolean = true,
                      createdAt: LocalDateTime = LocalDateTime.now(),
                      updatedAt: LocalDateTime = LocalDateTime.now()
                    )

class EquipmentTable(tag: Tag) extends Table[Equipment](tag, "equipment") {

  import java.time.LocalDateTime
  import java.time.format.DateTimeFormatter

  private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, String](
      ldt => ldt.format(dateTimeFormat),              // Scala → DB
      str => LocalDateTime.parse(str, dateTimeFormat) // DB → Scala
    )

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def assetTag = column[Option[String]]("asset_tag")
  def equipmentType = column[String]("type")
  def model = column[Option[String]]("model")
  def serialNo = column[Option[String]]("serial_no")
  def conditionStatus = column[String]("condition_status")
  def isAvailable = column[Boolean]("is_available")
  def createdAt = column[LocalDateTime]("created_at")
  def updatedAt = column[LocalDateTime]("updated_at")

  def * = (id, assetTag, equipmentType, model, serialNo, conditionStatus, isAvailable, createdAt, updatedAt)
    .mapTo[Equipment]
}
