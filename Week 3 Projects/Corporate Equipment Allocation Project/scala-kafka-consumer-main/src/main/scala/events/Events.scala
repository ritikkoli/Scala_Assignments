package events

import java.time.LocalDateTime
import play.api.libs.json._

import java.time.format.DateTimeFormatter

// Use a date format matching your DB / producer ("yyyy-MM-dd HH:mm:ss")
private[events] object DateFormats {
  val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  implicit val ldtReads: Reads[LocalDateTime] = Reads.localDateTimeReads("yyyy-MM-dd HH:mm:ss")
  implicit val ldtWrites: Writes[LocalDateTime] = Writes.temporalWrites[LocalDateTime, DateTimeFormatter](dtf)
}



case class AllocationCreatedEvent(
                                   allocationId: Long,
                                   equipmentId: Long,
                                   employeeId: Long,
                                   allocationDate: LocalDateTime,
                                   expectedReturnDate: LocalDateTime,
                                   purpose: Option[String]
                                 )
object AllocationCreatedEvent { implicit val format: OFormat[AllocationCreatedEvent] = Json.format }

case class EquipmentReturnedEvent(
                                   allocationId: Long,
                                   equipmentId: Long,
                                   employeeId: Long,
                                   returnDate: LocalDateTime,
                                   returnCondition: String
                                 )
object EquipmentReturnedEvent { implicit val format: OFormat[EquipmentReturnedEvent] = Json.format }

case class MaintenanceEvent(
                             equipmentId: Long,
                             //allocationId: Long,
                             //problem: String,
                             severity: String
                           )
object MaintenanceEvent { implicit val format: OFormat[MaintenanceEvent] = Json.format }

case class OverdueEvent(
                         allocationId: Long,
                         employeeId: Long,
                         dueDate: LocalDateTime
                       )
object OverdueEvent { implicit val format: OFormat[OverdueEvent] = Json.format }

case class InventoryUpdateEvent(
                                 equipmentId: Long,
                                 status: String,
                                 timestamp: LocalDateTime
                               )
object InventoryUpdateEvent { implicit val format: OFormat[InventoryUpdateEvent] = Json.format }

case class NotificationEvent(
                              id: Long,
                              eventType: String,
                              recipient: String,
                              recipientType: String,
                              payload: String
                            )
object NotificationEvent { implicit val format: OFormat[NotificationEvent] = Json.format }
