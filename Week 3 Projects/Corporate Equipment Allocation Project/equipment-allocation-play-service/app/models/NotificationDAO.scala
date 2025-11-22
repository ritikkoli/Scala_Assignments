package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationDAO @Inject()(
                                 dbConfigProvider: DatabaseConfigProvider
                               )(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val notifications = TableQuery[NotificationTable]

  def list(): Future[Seq[Notification]] =
    db.run(notifications.result)

  def find(id: Long): Future[Option[Notification]] =
    db.run(notifications.filter(_.id === id).result.headOption)

  def create(n: Notification): Future[Long] =
    db.run((notifications returning notifications.map(_.id)) += n)

  def pending(): Future[Seq[Notification]] =
    db.run(notifications.filter(_.status === "PENDING").result)

  def markSent(id: Long): Future[Int] =
    db.run {
      notifications
        .filter(_.id === id)
        .map(n => (n.status, n.sentAt))
        .update(("SENT", Some(java.time.LocalDateTime.now())))
    }

  def markFailed(id: Long, reason: String): Future[Int] =
    db.run {
      notifications
        .filter(_.id === id)
        .map(n => (n.status, n.failureReason, n.retryCount))
        .update(("FAILED", Some(reason), 1))
    }
}
