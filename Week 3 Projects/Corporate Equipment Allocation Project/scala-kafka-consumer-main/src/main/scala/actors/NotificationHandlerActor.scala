//package actors
//
//import akka.actor.{Actor, ActorLogging, Props}
//import play.api.libs.json._
//import events._
//import services.EmailService
//
//import scala.concurrent.ExecutionContext
//
//object NotificationHandlerActor {
//  def props(emailSvc: EmailService)(implicit ec: ExecutionContext): Props = Props(new NotificationHandlerActor(emailSvc))
//
//  // messages
//  case class HandleAllocationJson(json: String)
//  case class HandleReturnJson(json: String)
//  case class HandleOverdueJson(json: String)
//  case class HandleGenericNotificationJson(json: String)
//}
//
//class NotificationHandlerActor(emailSvc: EmailService)(implicit ec: ExecutionContext) extends Actor with ActorLogging {
//  import NotificationHandlerActor._
//  import events._
//  import play.api.libs.json.Json
//  import scala.util.{Success, Failure}
//
//  def receive: Receive = {
//    case HandleAllocationJson(json) =>
//      Json.parse(json).validate[AllocationCreatedEvent].fold(
//        errs => log.error(s"Failed to parse AllocationCreatedEvent: $errs"),
//        event => {
//          log.info(s"Processing allocation notification for allocationId=${event.allocationId}")
//          // Build email / notification to inventory + employee (mock)
//          val subject = s"Equipment allocated (id=${event.equipmentId})"
//          val body = s"Equipment ${event.equipmentId} allocated to employee ${event.employeeId} until ${event.expectedReturnDate}"
//          emailSvc.sendEmail(s"employee-${event.employeeId}@gmail.com", subject, body).onComplete {
//            case Success(_) => log.info("Allocation notification email sent")
//            case Failure(ex) => log.error("Failed to send allocation email", ex)
//          }
//        }
//      )
//
//    case HandleReturnJson(json) =>
//      Json.parse(json).validate[EquipmentReturnedEvent].fold(
//        errs => log.error(s"Failed to parse EquipmentReturnedEvent: $errs"),
//        event => {
//          log.info(s"Processing return notification for equipmentId=${event.equipmentId}")
//          // if returnCondition indicates damaged -> notify maintenance
//          if (!event.returnCondition.equalsIgnoreCase("GOOD")) {
//            log.info("Flagging maintenance due to bad condition")
//            // we might produce a new maintenance event or call maintenance actor - simplified here
//            // send email to maintenance
//            emailSvc.sendEmail("maintenance-team@example.com", s"Equipment ${event.equipmentId} needs service", s"Condition: ${event.returnCondition}")
//          }
//        }
//      )
//
//    case HandleOverdueJson(json) =>
//      Json.parse(json).validate[OverdueEvent].fold(
//        errs => log.error(s"Failed to parse OverdueEvent: $errs"),
//        event => {
//          val to = s"employee-${event.employeeId}@example.com"
//          val subj = "Overdue equipment reminder"
//          val body = s"Your allocation ${event.allocationId} was due on ${event.dueDate}"
//          emailSvc.sendEmail(to, subj, body)
//        }
//      )
//
//    case HandleGenericNotificationJson(json) =>
//      Json.parse(json).validate[NotificationEvent].fold(
//        errs => log.error(s"Failed to parse NotificationEvent: $errs"),
//        event => {
//          emailSvc.sendEmail(event.recipient, s"[${event.eventType}] notification", event.payload)
//        }
//      )
//  }
//}


package actors

import akka.actor.{Actor, ActorLogging, Props}
import play.api.libs.json._
import events._
import services.EmailService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

object NotificationHandlerActor {

  def props(emailSvc: EmailService)(implicit ec: ExecutionContext): Props =
    Props(new NotificationHandlerActor(emailSvc))

  // Messages routed from RouterActor
  case class HandleAllocationJson(json: String)
  case class HandleReturnJson(json: String)
  case class HandleOverdueJson(json: String)
  case class HandleGenericNotificationJson(json: String)
}

class NotificationHandlerActor(emailSvc: EmailService)(implicit ec: ExecutionContext)
  extends Actor with ActorLogging {

  import NotificationHandlerActor._

  override def receive: Receive = {

    // -------------------------------------------------------------------------
    // ALLOCATION NOTIFICATION
    // -------------------------------------------------------------------------
    case HandleAllocationJson(json) =>
      Json.parse(json).validate[AllocationCreatedEvent].fold(
        errs => log.error(s"[Allocation] Failed to parse event: $errs"),
        event => {
          log.info(s"[Allocation] Sending allocation email for allocationId=${event.allocationId}")

          val to = s"employee-${event.employeeId}@example.com"
          val subject = s"Equipment Allocated (Equipment ID: ${event.equipmentId})"
          val body =
            s"""
               |Hello Employee ${event.employeeId},
               |
               |Equipment ID: ${event.equipmentId} has been allocated to you.
               |Purpose: ${event.purpose.getOrElse("N/A")}
               |Allocation Date: ${event.allocationDate}
               |Expected Return: ${event.expectedReturnDate}
               |
               |Regards,
               |Inventory Team
               |""".stripMargin

          sendEmail(to, subject, body)
        }
      )


    // -------------------------------------------------------------------------
    // RETURN NOTIFICATION
    // -------------------------------------------------------------------------
    case HandleReturnJson(json) =>
      Json.parse(json).validate[EquipmentReturnedEvent].fold(
        errs => log.error(s"[Return] Failed to parse event: $errs"),
        event => {
          log.info(s"[Return] Processing return of equipment ${event.equipmentId}")

          val to = s"employee-${event.employeeId}@example.com"
          val subject = s"Thank you for returning equipment ID: ${event.equipmentId}"

          val body =
            s"""
               |Dear Employee ${event.employeeId},
               |
               |We have received the return for Equipment ID: ${event.equipmentId}.
               |Returned Condition: ${event.returnCondition}
               |Return Date: ${event.returnDate}
               |
               |Thank you for your cooperation!
               |
               |Regards,
               |Inventory Team
               |""".stripMargin

          sendEmail(to, subject, body)

          // If equipment condition is BAD, notify maintenance team
          if (!event.returnCondition.equalsIgnoreCase("GOOD")) {
            val maintenanceTo = "maintenance-team@example.com"

            val mSubject = s"Equipment ${event.equipmentId} requires maintenance"
            val mBody =
              s"""
                 |Equipment ID ${event.equipmentId} was returned in condition: ${event.returnCondition}.
                 |
                 |Please schedule an inspection.
                 |Allocation ID: ${event.allocationId}
                 |Employee ID: ${event.employeeId}
                 |""".stripMargin

            sendEmail(maintenanceTo, mSubject, mBody)
          }
        }
      )


    // -------------------------------------------------------------------------
    // OVERDUE NOTIFICATION
    // -------------------------------------------------------------------------
    case HandleOverdueJson(json) =>
      Json.parse(json).validate[OverdueEvent].fold(
        errs => log.error(s"[Overdue] Failed to parse event: $errs"),
        event => {
          log.info(s"[Overdue] Sending overdue reminder for allocation ${event.allocationId}")

          val to = s"employee-${event.employeeId}@example.com"
          val subject = "Reminder: Equipment Return Overdue"
          val body =
            s"""
               |Dear Employee ${event.employeeId},
               |
               |The equipment for Allocation ID ${event.allocationId} was due on ${event.dueDate}.
               |Please return it as soon as possible.
               |
               |Regards,
               |Inventory Team
               |""".stripMargin

          sendEmail(to, subject, body)
        }
      )


    // -------------------------------------------------------------------------
    // GENERIC NOTIFICATION EVENT
    // -------------------------------------------------------------------------
    case HandleGenericNotificationJson(json) =>
      Json.parse(json).validate[NotificationEvent].fold(
        errs => log.error(s"[Notification] Failed to parse event: $errs"),
        event => {
          log.info(s"[Notification] Sending generic notification to ${event.recipient}")

          val subject = s"[${event.eventType}] Notification"
          val body = event.payload

          sendEmail(event.recipient, subject, body)
        }
      )
  }

  // ---------------------------------------------------------------------------
  // Helper: Send email using SMTP (non-blocking)
  // ---------------------------------------------------------------------------
  private def sendEmail(to: String, subject: String, body: String): Unit = {
    emailSvc.sendEmail(to, subject, body).onComplete {
      case Success(_) =>
        log.info(s"[Email Sent] to=$to subject='$subject'")
      case Failure(ex) =>
        log.error(s"[Email Failed] to=$to subject='$subject' error=${ex.getMessage}", ex)
    }
  }
}
