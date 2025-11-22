package events
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class EquipmentReturnedEvent(
                                   allocationId: Long,
                                   equipmentId: Long,
                                   employeeId: Long,
                                   returnDate: LocalDateTime,
                                   returnCondition: String
                                 )

//object EquipmentReturnedEvent {
//  //def apply(allocationId: Long, equipmentId: Long, employeeId: Long, returnDate: LocalDateTime, returnCondition: String): Nothing = ???
//
//  implicit val format = Json.format[EquipmentReturnedEvent]
//}

object EquipmentReturnedEvent {



  // JSON serialization/deserialization
  implicit val equipmentReturnedEvent: OFormat[EquipmentReturnedEvent] =
    Json.format[EquipmentReturnedEvent]
}
