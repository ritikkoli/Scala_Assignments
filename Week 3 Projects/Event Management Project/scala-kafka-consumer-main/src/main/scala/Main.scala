//import akka.actor.{ActorSystem, Props}
//import akka.stream.ActorMaterializer
//import com.typesafe.config.ConfigFactory
//import consumer.{KafkaConsumerService, MessageProcessorActor}
//import notification.{EmailService, EmailSenderActor}
//import scheduler.NotificationScheduler
//
//object Main {
//  def main(args: Array[String]): Unit = {
//
//    implicit val system = ActorSystem("event-management-consumer")
//    implicit val mat = ActorMaterializer()
//    implicit val ec = system.dispatcher
//
//    val config = ConfigFactory.load()
//
//    // Email Service Initialization
//    val emailConf = config.getConfig("email")
//
//    val emailService = new EmailService(
//      smtpHost = emailConf.getString("smtp.host"),
//      smtpPort = emailConf.getInt("smtp.port"),
//      username = emailConf.getString("smtp.username"),
//      password = emailConf.getString("smtp.password"),
//      from     = emailConf.getString("from")
//    )
//
//    val emailActor = system.actorOf(Props(new EmailSenderActor(emailService)), "emailSender")
//
//    val processor = system.actorOf(Props(new MessageProcessorActor(emailActor)), "messageProcessor")
//
//    val consumerService = new KafkaConsumerService(processor, config.getConfig("kafka-consumer"))
//    consumerService.start()
//
//    val scheduler = new NotificationScheduler(system)
//    scheduler.start()
//
//    println("Microservice (Kafka Consumer + Scheduler + Email) is running...")
//  }
//}


import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory

import consumer.{KafkaConsumerService, MessageProcessorActor}
import notification.{EmailService, EmailSenderActor}
import scheduler.NotificationScheduler

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("event-management-consumer")
    implicit val mat: Materializer   = Materializer(system)
    implicit val ec                  = system.dispatcher

    val config = ConfigFactory.load()

    // ----------------------------------------
    // 1. Initialize Email Service
    // ----------------------------------------
    val emailConf = config.getConfig("email")

    val emailService = new EmailService(
      smtpHost = emailConf.getString("smtp.host"),
      smtpPort = emailConf.getInt("smtp.port"),
      username = emailConf.getString("smtp.username"),
      password = emailConf.getString("smtp.password"),
      from     = emailConf.getString("from")
    )

    val emailActor = system.actorOf(
      Props(new EmailSenderActor(emailService)),
      "emailSender"
    )

    // ----------------------------------------
    // 2. Message Processor Actor
    // ----------------------------------------
    val processor = system.actorOf(
      Props(new MessageProcessorActor(emailActor)),
      "messageProcessor"
    )

    // ----------------------------------------
    // 3. Kafka Consumer
    // ----------------------------------------
    val consumerConfig = config.getConfig("kafka-consumer")
    val consumerService = new KafkaConsumerService(processor, consumerConfig)
    consumerService.start()

    // ----------------------------------------
    // 4. Periodic Scheduler
    // ----------------------------------------
    val scheduler = new NotificationScheduler(system)
    scheduler.start()

    // ----------------------------------------
    println("Microservice (Kafka Consumer + Scheduler + Email) is running...")
  }
}
