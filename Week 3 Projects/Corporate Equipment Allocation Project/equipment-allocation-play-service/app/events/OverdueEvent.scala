package events

import java.time.LocalDateTime
import play.api.libs.json.{Json, OFormat}

case class OverdueEvent(
                         allocationId: Long,
                         equipmentId: Long,
                         employeeId: Long,
                         expectedReturnDate: LocalDateTime,
                         actualReturnDate: Option[LocalDateTime],
                         allocationDate: LocalDateTime
                       )

object OverdueEvent {
  implicit val format: OFormat[OverdueEvent] = Json.format[OverdueEvent]
}
