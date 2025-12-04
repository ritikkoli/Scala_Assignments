package services

import models.{Event, Task}
import repository.{EventRepository, TaskRepository}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventService @Inject()(
                              eventRepo: EventRepository,
                              taskRepo: TaskRepository
                            )(implicit ec: ExecutionContext) {

  def createEvent(event: Event): Future[Long] =
    eventRepo.create(event)

  def getEvent(id: Long): Future[Option[Event]] =
    eventRepo.getById(id)

  def getAllEvents(): Future[Seq[Event]] =
    eventRepo.getAll()

  def deleteEvent(id: Long): Future[Int] =
    eventRepo.delete(id)

  /** List all tasks for an event */
  def getEventTasks(eventId: Long): Future[Seq[Task]] =
    taskRepo.getByEvent(eventId)
}
