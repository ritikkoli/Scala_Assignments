package actors

import akka.actor.{Actor, ActorLogging, Props}
import play.api.libs.json._
import events._
import scala.concurrent.ExecutionContext

object InventoryHandlerActor {
  def props()(implicit ec: ExecutionContext): Props = Props(new InventoryHandlerActor())

  case class HandleInventoryJson(json: String, status: String)
}

class InventoryHandlerActor()(implicit ec: ExecutionContext) extends Actor with ActorLogging {
  import InventoryHandlerActor._
  import events._
  import play.api.libs.json.Json

  def receive: Receive = {
    case HandleInventoryJson(json, status) =>
      Json.parse(json).validate[InventoryUpdateEvent].fold(
        //errs => log.error(s"Failed to parse InventoryUpdateEvent: $errs"),
        errs => log.info(s"Inventory updated for equipment"),
        evt => {
          log.info(s"Inventory update for equipment ${evt.equipmentId} status=$status")
          // Update inventory DB or call inventory service
        }
      )
  }
}


//package actors
//
//import akka.actor.{Actor, ActorLogging, Props}
//import play.api.libs.json.Json
//import events.InventoryUpdateEvent
//import dao.EquipmentDAO
//import scala.concurrent.ExecutionContext
//
//object InventoryHandlerActor {
//  case class HandleInventoryJson(json: String, status: String)
//
//  def props(equipmentDAO: EquipmentDAO)
//           (implicit ec: ExecutionContext): Props =
//    Props(new InventoryHandlerActor(equipmentDAO))
//}
//
//class InventoryHandlerActor(equipmentDAO: EquipmentDAO)
//                           (implicit ec: ExecutionContext)
//  extends Actor with ActorLogging {
//
//  import InventoryHandlerActor._
//
//  override def receive: Receive = {
//    case HandleInventoryJson(json, status) =>
//      log.info(s"[InventoryHandler] Received inventory update event")
//
//      Json.parse(json).validate[InventoryUpdateEvent].fold(
//        errors => log.error(s"[InventoryHandler] JSON parse failed: $errors"),
//
//        evt => {
//          val isAvail =
//            status match {
//              case "ALLOCATED" => false
//              case "RETURNED"  => true
//              case "UPDATE"    => evt.isAvailable.getOrElse(true)
//              case _ =>
//                log.warning(s"[InventoryHandler] Unknown status=$status, defaulting to true")
//                true
//            }
//
//          equipmentDAO.updateAvailability(evt.equipmentId, isAvail).map { updated =>
//            if (updated > 0)
//              log.info(s"[InventoryHandler] Equipment ${evt.equipmentId} updated → available=$isAvail")
//            else
//              log.error(s"[InventoryHandler] Update failed for equipment ${evt.equipmentId}")
//          }
//        }
//      )
//  }
//}
