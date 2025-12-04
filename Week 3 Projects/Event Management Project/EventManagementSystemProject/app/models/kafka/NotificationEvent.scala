package models.kafka

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

case class NotificationEvent(
                              messageId: String,
                              eventId: Long,
                              taskId: Option[Long],
                              teamId: Option[Long],
                              eventType: String, // TASK_ASSIGNED, TASK_UPDATED, ISSUE_REPORTED, REMINDER, EVENT_DAY
                              payload: String,
                              timestamp: LocalDateTime = LocalDateTime.now()
                            )

object NotificationEvent {
  implicit val format: OFormat[NotificationEvent] = Json.format[NotificationEvent]
}
