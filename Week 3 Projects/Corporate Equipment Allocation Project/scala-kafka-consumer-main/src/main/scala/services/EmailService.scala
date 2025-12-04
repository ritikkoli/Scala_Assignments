//package services
//
//import scala.concurrent.{Future, ExecutionContext}
//import org.slf4j.LoggerFactory
//
//class EmailService()(implicit ec: ExecutionContext) {
//  private val log = LoggerFactory.getLogger(getClass)
//
//  def sendEmail(to: String, subject: String, body: String): Future[Unit] = Future {
//    // Replace with real mailer (SMTP, SES, etc.)
//    log.info(s"[MOCK EMAIL] to=$to subject=$subject body=${body.take(500)}")
//  }
//}


package services

import com.typesafe.config.ConfigFactory
import jakarta.mail._
import jakarta.mail.internet._

import scala.concurrent.{ExecutionContext, Future}

class EmailService(implicit ec: ExecutionContext) {

  private val config = ConfigFactory.load().getConfig("email")

  private val host     = config.getString("smtp.host")
  private val port     = config.getInt("smtp.port")
  private val username = config.getString("smtp.username")
  private val password = config.getString("smtp.password")
  private val from     = config.getString("from")
  private val tls      = config.getBoolean("tls")

  private val props: java.util.Properties = {
    val p = new java.util.Properties()
    p.put("mail.smtp.auth", "true")
    p.put("mail.smtp.starttls.enable", tls.toString)
    p.put("mail.smtp.host", host)
    p.put("mail.smtp.port", port.toString)
    p
  }

  private val session: Session = Session.getInstance(
    props,
    new Authenticator() {
      override protected def getPasswordAuthentication: PasswordAuthentication =
        new PasswordAuthentication(username, password)
    }
  )

  /** Send email using SMTP (Gmail) */
  def sendEmail(to: String, subject: String, body: String): Future[Unit] = Future {
    val msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress(from))
    msg.setRecipients(Message.RecipientType.TO, to)
    msg.setSubject(subject)
    msg.setText(body)

    Transport.send(msg)
  }
}

