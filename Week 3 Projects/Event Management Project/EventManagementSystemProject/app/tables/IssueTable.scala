package tables

import models.Issue
import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

class IssueTable(tag: Tag) extends Table[Issue](tag, "issues") {

  import DateTimeMapping._

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def taskId = column[Long]("task_id")
  def teamId = column[Long]("team_id")
  def description = column[String]("description")
  def reportedAt = column[LocalDateTime]("reported_at", O.SqlType("DATETIME"))

  def taskFk = foreignKey("fk_task", taskId, TaskTable.query)(_.id, onDelete = ForeignKeyAction.Cascade)
  def teamFk = foreignKey("fk_team_issue", teamId, TeamTable.query)(_.id, onDelete = ForeignKeyAction.Cascade)

  def * =
    (id, taskId, teamId, description, reportedAt)
      .<>((Issue.apply _).tupled, Issue.unapply)
}

object IssueTable {
  val query = TableQuery[IssueTable]
}
