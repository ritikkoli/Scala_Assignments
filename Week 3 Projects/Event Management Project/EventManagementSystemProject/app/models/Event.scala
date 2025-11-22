package models

import java.time.LocalDateTime
import play.api.libs.json.{Json, OFormat}

case class Event(
                  id: Long = 0L,
                  eventType: String,
                  eventDate: LocalDateTime,
                  guestCount: Int,
                  createdAt: LocalDateTime = LocalDateTime.now()
                )

object Event {
  implicit val format: OFormat[Event] = Json.format[Event]
}
