package services

import models.{Issue, IssueReportedMessage}
import play.api.Configuration
import play.api.libs.json.Json
import repository.{IssueRepository, TaskRepository, TeamRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IssueService @Inject()(
                              issueRepo: IssueRepository,
                              taskRepo: TaskRepository,
                              teamRepo: TeamRepository,
                              kafkaProducer: KafkaProducerService,
                              config: Configuration
                            )(implicit ec: ExecutionContext) {

  /** Create an issue only if valid task + team exist */
  def reportIssue(issue: Issue): Future[Either[String, Long]] = {

    val taskF = taskRepo.getById(issue.taskId)
    val teamF = teamRepo.getById(issue.teamId)

    for {
      taskOpt <- taskF
      teamOpt <- teamF
      result <- (taskOpt, teamOpt) match {

        case (None, _) =>
          Future.successful(Left("Task not found."))

        case (_, None) =>
          Future.successful(Left("Team not found."))

//        case _ =>
//          issueRepo.create(issue).map(id => Right(id))

        case _ =>
          issueRepo.create(issue).map { id =>
            val msg = IssueReportedMessage(
              issueId = id,
              taskId = issue.taskId,
              teamId = issue.teamId,
              description = issue.description
            )

            val topic = config.get[String]("kafka.producer.topic.issueReported")
            kafkaProducer.sendMessage(topic, id.toString, Json.toJson(msg))

            Right(id)
          }

      }
    } yield result
  }

  def getIssuesForTask(taskId: Long): Future[Seq[Issue]] =
    issueRepo.getByTask(taskId)

  def getAll(): Future[Seq[Issue]] =
    issueRepo.getAll()
}
