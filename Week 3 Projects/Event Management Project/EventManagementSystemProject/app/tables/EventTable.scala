package tables

import models.Event
import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

class EventTable(tag: Tag) extends Table[Event](tag, "events") {

  import DateTimeMapping._

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def eventType = column[String]("event_type")
  def eventDate = column[LocalDateTime]("event_date", O.SqlType("DATETIME"))
  def guestCount = column[Int]("guest_count")
  def createdAt = column[LocalDateTime]("created_at", O.SqlType("DATETIME"))

  def * =
    (id, eventType, eventDate, guestCount, createdAt)
      .<>((Event.apply _).tupled, Event.unapply)
}

object EventTable {
  val query = TableQuery[EventTable]
}
