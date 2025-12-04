package consumer

import akka.actor.{Actor, ActorRef}
import play.api.libs.json._
import models._
import akka.event.Logging
import notification.SendEmail

class MessageProcessorActor(emailActor: ActorRef) extends Actor {

  val log = Logging(context.system, this)

  override def receive: Receive = {

    case ("task-assigned", jsonString: String) =>
      val msg = Json.parse(jsonString).as[TaskAssignedMessage]
      log.info(s"[TASK ASSIGNED] $msg")

      emailActor ! SendEmail(
        to = "team@example.com",
        subject = s"New Task Assigned: ${msg.taskName}",
        body =
          s"""
             |Task Assigned
             |-------------
             |Task ID: ${msg.taskId}
             |Event ID: ${msg.eventId}
             |Team ID: ${msg.teamId}
             |Task Name: ${msg.taskName}
             |Deadline: ${msg.deadline}
           """.stripMargin
      )

    case ("task-status", jsonString: String) =>
      val msg = Json.parse(jsonString).as[TaskStatusUpdatedMessage]
      log.info(s"[TASK STATUS UPDATED] $msg")

      emailActor ! SendEmail(
        to = "manager@gmail.com",
        subject = s"Task ${msg.taskId} Status Updated",
        body = s"Task ${msg.taskId} is now '${msg.status}'."
      )

    case ("issue-reported", jsonString: String) =>
      val msg = Json.parse(jsonString).as[IssueReportedMessage]
      log.warning(s"[ISSUE REPORTED] $msg")

      emailActor ! SendEmail(
        to = "manager@gmail.com",
        subject = s"Issue Reported for Task: ${msg.taskId}",
        body =
          s"""
             |Issue Reported!
             |---------------
             |Issue ID: ${msg.issueId}
             |Task ID: ${msg.taskId}
             |Team ID: ${msg.teamId}
             |Description: ${msg.description}
           """.stripMargin
      )
  }
}
