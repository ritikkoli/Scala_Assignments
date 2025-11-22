package services

import models.{Task, TaskAssignedMessage, TaskStatusUpdatedMessage}
import play.api.Configuration
import play.api.libs.json.Json
import repository.{EventRepository, TaskRepository, TeamRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
//class TaskService @Inject()(
//                             taskRepo: TaskRepository,
//                             eventRepo: EventRepository,
//                             teamRepo: TeamRepository
//                           )(implicit ec: ExecutionContext) {
class TaskService @Inject()(
                             taskRepo: TaskRepository,
                             eventRepo: EventRepository,
                             teamRepo: TeamRepository,
                             kafkaProducer: KafkaProducerService,
                             config: Configuration
                           )(implicit ec: ExecutionContext) {

  /** Create task only if event + team exist */
  def createTask(task: Task): Future[Either[String, Long]] = {

    val eventF = eventRepo.getById(task.eventId)
    val teamF = teamRepo.getById(task.teamId)

    for {
      eventOpt <- eventF
      teamOpt <- teamF
      result <- (eventOpt, teamOpt) match {

        case (None, _) =>
          Future.successful(Left("Event not found."))

        case (_, None) =>
          Future.successful(Left("Team not found."))

//        case _ =>
//          taskRepo.create(task).map(id => Right(id))
        case _ =>
          taskRepo.create(task).map { newId =>
            val message = TaskAssignedMessage(
              taskId = newId,
              eventId = task.eventId,
              teamId = task.teamId,
              taskName = task.taskName,
              deadline = task.deadline.toString
            )

            val topic = config.get[String]("kafka.producer.topic.taskAssigned")

            kafkaProducer.sendMessage(topic, newId.toString, Json.toJson(message))

            Right(newId)
          }

      }
    } yield result
  }

  /** UPDATE STATUS */
//  def updateTaskStatus(taskId: Long, status: String): Future[Boolean] =
//    taskRepo.updateStatus(taskId, status).map(_ > 0)

  def updateTaskStatus(taskId: Long, status: String): Future[Boolean] =
    taskRepo.updateStatus(taskId, status).map { updated =>

      if (updated > 0) {
        val message = TaskStatusUpdatedMessage(taskId, status)
        val topic = config.get[String]("kafka.producer.topic.taskStatus")
        kafkaProducer.sendMessage(topic, taskId.toString, Json.toJson(message))
      }

      updated > 0
    }


  /** FETCH TASK */
  def getTask(taskId: Long): Future[Option[Task]] =
    taskRepo.getById(taskId)

  /** GET ALL TASKS FOR A TEAM */
  def tasksByTeam(teamId: Long): Future[Seq[Task]] =
    taskRepo.getByTeam(teamId)

  /** ⭐️ MISSING METHOD — FIXED HERE ⭐️ */
  def getEventTasks(eventId: Long): Future[Seq[Task]] =
    taskRepo.getByEvent(eventId)
}
