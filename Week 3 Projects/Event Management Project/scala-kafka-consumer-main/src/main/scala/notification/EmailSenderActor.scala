package notification

import akka.actor.Actor
import akka.event.Logging

case class SendEmail(to: String, subject: String, body: String)

class EmailSenderActor(emailService: EmailService) extends Actor {

  val log = Logging(context.system, this)

  override def receive: Receive = {
    case SendEmail(to, subject, body) =>
      try {
        //emailService.sendEmail(to, subject, body)
        log.info(s"EMAIL SENT → to=$to subject=$subject")
      } catch {
        case ex: Exception =>
          log.error(ex, s"FAILED to send email to $to. Retrying...")
        // TODO: add retry logic
      }
  }
}
