import akka.actor.{ActorSystem, Props}
import actors._
import kafka.KafkaPollerService
import services.EmailService

import scala.concurrent.ExecutionContext

object KafkaConsumerApp extends App {
  implicit val system: ActorSystem = ActorSystem("equipment-consumer-system")
  implicit val ec: ExecutionContext = system.dispatcher
  val emailSvc = new EmailService()(ec)

  val notificationHandler = system.actorOf(NotificationHandlerActor.props(emailSvc), "notification-handler")
  val maintenanceHandler = system.actorOf(MaintenanceHandlerActor.props(), "maintenance-handler")
  val inventoryHandler = system.actorOf(InventoryHandlerActor.props(), "inventory-handler")

  val router = system.actorOf(RouterActor.props(notificationHandler, maintenanceHandler, inventoryHandler), "router")

  val poller = new KafkaPollerService(router)
  poller.start()

  sys.addShutdownHook {
    println("Shutting down Kafka poller and actor system...")
    poller.stop()
    system.terminate()
  }

  println("Kafka consumer microservice started.")
}
