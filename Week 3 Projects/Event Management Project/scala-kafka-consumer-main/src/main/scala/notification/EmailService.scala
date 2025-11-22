package notification

import java.util.Properties
import javax.mail._
import javax.mail.internet._

class EmailService(smtpHost: String, smtpPort: Int, username: String, password: String, from: String) {

  private val props = new Properties()
  props.put("mail.smtp.auth", "true")
  props.put("mail.smtp.starttls.enable", "true")
  props.put("mail.smtp.host", smtpHost)
  props.put("mail.smtp.port", smtpPort.toString)

  private val session: Session = Session.getInstance(props,
    new javax.mail.Authenticator() {
      override protected def getPasswordAuthentication: PasswordAuthentication =
        new PasswordAuthentication(username, password)
    })

  /** Sends email synchronously */
  def sendEmail(to: String, subject: String, body: String): Unit = {
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from))
    message.setRecipients(Message.RecipientType.TO, to)
    message.setSubject(subject)
    message.setText(body)

    Transport.send(message)
  }
}
