package repository

import models.Issue
import play.api.db.slick.DatabaseConfigProvider
import tables.IssueTable


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class IssueRepository @Inject()(
                                 dbConfigProvider: DatabaseConfigProvider
                               )(implicit ec: ExecutionContext)
  extends BaseRepository(dbConfigProvider) {

  import profile.api._
  private val issues = IssueTable.query

  def create(issue: Issue): Future[Long] =
    db.run(issues returning issues.map(_.id) += issue)

  def getByTask(taskId: Long): Future[Seq[Issue]] =
    db.run(issues.filter(_.taskId === taskId).result)

  def getAll(): Future[Seq[Issue]] =
    db.run(issues.result)
}
