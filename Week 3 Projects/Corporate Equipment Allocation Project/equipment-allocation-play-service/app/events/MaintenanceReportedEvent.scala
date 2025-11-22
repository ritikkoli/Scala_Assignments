package events

import play.api.libs.json._
import java.time.LocalDateTime

case class MaintenanceReportedEvent(
                                     maintenanceId: Long,
                                     equipmentId: Long,
                                     description: String,
                                     maintenanceDate: LocalDateTime,
                                     performedBy: String,
                                     severity: String
                                   )


object MaintenanceReportedEvent {



  // JSON serialization/deserialization
  implicit val maintenanceReportedFormat: OFormat[MaintenanceReportedEvent] =
    Json.format[MaintenanceReportedEvent]
}
