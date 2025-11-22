package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import kafka.KafkaMessage

import play.api.libs.json._
import events._

object RouterActor {
  def props(notificationHandler: ActorRef, maintenanceHandler: ActorRef, inventoryHandler: ActorRef): Props =
    Props(new RouterActor(notificationHandler, maintenanceHandler, inventoryHandler))
}

class RouterActor(notificationHandler: ActorRef, maintenanceHandler: ActorRef, inventoryHandler: ActorRef) extends Actor with ActorLogging {
  import events._
  import play.api.libs.json.Json

  def receive: Receive = {
    case km: KafkaMessage =>
      val topic = km.topic
      val payload = km.value
      log.info(s"Router received topic=$topic payload=${payload.take(200)}")
      // route by topic name
      topic match {
        case "equipment-allocation-events" =>
          notificationHandler ! NotificationHandlerActor.HandleAllocationJson(payload)
          inventoryHandler ! InventoryHandlerActor.HandleInventoryJson(payload, "ALLOCATED")
        case "equipment-return-events" =>
          notificationHandler ! NotificationHandlerActor.HandleReturnJson(payload)
          inventoryHandler ! InventoryHandlerActor.HandleInventoryJson(payload, "RETURNED")
        case "equipment-maintenance-events" =>
          maintenanceHandler ! MaintenanceHandlerActor.HandleMaintenanceJson(payload)
        case "equipment-overdue-events" =>
          notificationHandler ! NotificationHandlerActor.HandleOverdueJson(payload)
        case "inventory-update-events" =>
          inventoryHandler ! InventoryHandlerActor.HandleInventoryJson(payload, "AVAILABLE")
        case "notification-events" =>
          notificationHandler ! NotificationHandlerActor.HandleGenericNotificationJson(payload)
        case other =>
          log.warning(s"Unknown topic: $other")
      }
  }
}
