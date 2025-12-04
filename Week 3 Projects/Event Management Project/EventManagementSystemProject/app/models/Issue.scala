package models

import java.time.LocalDateTime
import play.api.libs.json.{OFormat, Json}

case class Issue(
                  id: Long = 0L,
                  taskId: Long,
                  teamId: Long,
                  description: String,
                  reportedAt: LocalDateTime = LocalDateTime.now()
                )

object Issue {
  implicit val format: OFormat[Issue] = Json.format[Issue]
}
