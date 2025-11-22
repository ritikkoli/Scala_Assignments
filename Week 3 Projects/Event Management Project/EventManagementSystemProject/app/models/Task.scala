package models

import java.time.LocalDateTime
import play.api.libs.json.{Json, OFormat}

case class Task(
                 id: Long = 0L,
                 eventId: Long,
                 teamId: Long,
                 taskName: String,
                 instructions: String,
                 deadline: LocalDateTime,
                 status: String = "Pending",  // Pending, InProgress, Completed, Delayed
                 createdAt: LocalDateTime = LocalDateTime.now()
               )

object Task {
  implicit val format: OFormat[Task] = Json.format[Task]
}
