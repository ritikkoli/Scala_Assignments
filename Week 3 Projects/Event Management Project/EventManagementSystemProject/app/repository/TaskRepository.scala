package repository

import models.Task
import play.api.db.slick.DatabaseConfigProvider
import tables.TaskTable

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class TaskRepository @Inject()(
                                dbConfigProvider: DatabaseConfigProvider
                              )(implicit ec: ExecutionContext)
  extends BaseRepository(dbConfigProvider) {

  import profile.api._
  private val tasks = TaskTable.query

  def create(task: Task): Future[Long] =
    db.run(tasks returning tasks.map(_.id) += task)

  def updateStatus(id: Long, status: String): Future[Int] =
    db.run(tasks.filter(_.id === id).map(_.status).update(status))

  def getByEvent(eventId: Long): Future[Seq[Task]] =
    db.run(tasks.filter(_.eventId === eventId).result)

  def getByTeam(teamId: Long): Future[Seq[Task]] =
    db.run(tasks.filter(_.teamId === teamId).result)

  def getById(id: Long): Future[Option[Task]] =
    db.run(tasks.filter(_.id === id).result.headOption)

  def delete(id: Long): Future[Int] =
    db.run(tasks.filter(_.id === id).delete)

  def getPendingTasks(): Future[Seq[Task]] = {
    val now = LocalDateTime.now()
    db.run(
      TaskTable.query.filter(_.status =!= "Completed").result
    )
  }

  def getTasksApproachingDeadline(): Future[Seq[Task]] = {
    val now = LocalDateTime.now()
    val nextHour = now.plusHours(1)

    db.run(
      TaskTable.query
        .filter(_.status =!= "Completed")
        .filter(_.deadline.between(now, nextHour))
        .result
    )
  }


}
