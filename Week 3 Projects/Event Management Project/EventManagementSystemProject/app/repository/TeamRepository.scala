package repository

import models.Team
import play.api.db.slick.DatabaseConfigProvider
import tables.TeamTable


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TeamRepository @Inject()(
                                dbConfigProvider: DatabaseConfigProvider
                              )(implicit ec: ExecutionContext)
  extends BaseRepository(dbConfigProvider) {

  import profile.api._
  private val teams = TeamTable.query

  def create(team: Team): Future[Long] =
    db.run(teams returning teams.map(_.id) += team)

  def getAll(): Future[Seq[Team]] =
    db.run(teams.result)

  def getById(id: Long): Future[Option[Team]] =
    db.run(teams.filter(_.id === id).result.headOption)
}
