package models

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

case class Employee(
                     id: Long = 0L,
                     name: String,
                     email: String,
                     department: Option[String],
                     createdAt: LocalDateTime = LocalDateTime.now()
                   )

class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {


  import java.time.LocalDateTime
  import java.time.format.DateTimeFormatter

  private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, String](
      ldt => ldt.format(dateTimeFormat),              // Scala → DB
      str => LocalDateTime.parse(str, dateTimeFormat) // DB → Scala
    )

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def department = column[Option[String]]("department")
  def createdAt = column[LocalDateTime]("created_at")

  def * = (id, name, email, department, createdAt).mapTo[Employee]
}
