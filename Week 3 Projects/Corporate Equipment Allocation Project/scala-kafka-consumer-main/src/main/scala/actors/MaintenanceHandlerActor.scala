package actors

import akka.actor.{Actor, ActorLogging, Props}
import play.api.libs.json._
import events._
import scala.concurrent.ExecutionContext

object MaintenanceHandlerActor {
  def props()(implicit ec: ExecutionContext): Props = Props(new MaintenanceHandlerActor())

  case class HandleMaintenanceJson(json: String)
}

class MaintenanceHandlerActor()(implicit ec: ExecutionContext) extends Actor with ActorLogging {
  import MaintenanceHandlerActor._
  import events._
  import play.api.libs.json.Json

  def receive: Receive = {
    case HandleMaintenanceJson(json) =>
      Json.parse(json).validate[MaintenanceEvent].fold(
        errs => log.error(s"Failed to parse MaintenanceEvent: $errs"),
        evt  => {
          log.info(s"Scheduling maintenance for equipment ${evt.equipmentId}, severity=${evt.severity}")
          // Here you would call maintenance DB/service — currently just logging
        }
      )
  }
}
