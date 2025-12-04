package tables

import models.Task
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDateTime

class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {

  import DateTimeMapping._

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def eventId = column[Long]("event_id")
  def teamId = column[Long]("team_id")
  def taskName = column[String]("task_name")
  def instructions = column[String]("instructions")
  def deadline = column[LocalDateTime]("deadline", O.SqlType("DATETIME"))
  def status = column[String]("status")
  def createdAt = column[LocalDateTime]("created_at", O.SqlType("DATETIME"))

  //def eventFk = foreignKey("fk_event", eventId, EventTable.query)(_.id, onDelete = ForeignKeyAction.Cascade)
  //def teamFk = foreignKey("fk_team", teamId, TeamTable.query)(_.id, onDelete = ForeignKeyAction.Cascade)

  def * =
    (id, eventId, teamId, taskName, instructions, deadline, status, createdAt)
      .<>((Task.apply _).tupled, Task.unapply)
}

object TaskTable {
  val query = TableQuery[TaskTable]
}
