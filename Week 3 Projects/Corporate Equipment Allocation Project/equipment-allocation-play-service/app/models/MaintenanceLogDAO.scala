package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MaintenanceLogDAO @Inject()(
                                   dbConfigProvider: DatabaseConfigProvider
                                 )(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val logs = TableQuery[MaintenanceLogTable]

  def list(): Future[Seq[MaintenanceLog]] =
    db.run(logs.result)

  def find(id: Long): Future[Option[MaintenanceLog]] =
    db.run(logs.filter(_.id === id).result.headOption)

  def create(m: MaintenanceLog): Future[Long] =
    db.run((logs returning logs.map(_.id)) += m)

  def resolve(id: Long, date: java.time.LocalDateTime, notes: Option[String]): Future[Int] =
    db.run {
      logs
        .filter(_.id === id)
        .map(m => (m.status, m.resolvedDate, m.notes))
        .update(("RESOLVED", Some(date), notes))
    }
}
