package events

import java.time.LocalDateTime
import play.api.libs.json._

case class AllocationCreatedEvent(
                                   allocationId: Long,
                                   equipmentId: Long,
                                   employeeId: Long,
                                   allocationDate: LocalDateTime,
                                   expectedReturnDate: LocalDateTime,
                                   purpose: Option[String]
                                 )

object AllocationCreatedEvent {
  implicit val format: OFormat[AllocationCreatedEvent] = Json.format[AllocationCreatedEvent]
}
