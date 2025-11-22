package tables

import models.Team
import slick.jdbc.MySQLProfile.api._

class TeamTable(tag: Tag) extends Table[Team](tag, "teams") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def contactEmail = column[Option[String]]("contact_email")
  def contactPhone = column[Option[String]]("contact_phone")

  def * =
    (id, name, contactEmail, contactPhone)
      .<>((Team.apply _).tupled, Team.unapply)
}

object TeamTable {
  val query = TableQuery[TeamTable]
}
