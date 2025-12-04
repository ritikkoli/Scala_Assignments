package repository

import models.Event
import play.api.db.slick.DatabaseConfigProvider
import tables.EventTable


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventRepository @Inject()(
                                 dbConfigProvider: DatabaseConfigProvider
                               )(implicit ec: ExecutionContext)
  extends BaseRepository(dbConfigProvider) {

  import profile.api._
  private val events = EventTable.query

  def create(event: Event): Future[Long] =
    db.run(events returning events.map(_.id) += event)

  def getAll(): Future[Seq[Event]] =
    db.run(events.result)

  def getById(id: Long): Future[Option[Event]] =
    db.run(events.filter(_.id === id).result.headOption)

  def delete(id: Long): Future[Int] =
    db.run(events.filter(_.id === id).delete)
}
