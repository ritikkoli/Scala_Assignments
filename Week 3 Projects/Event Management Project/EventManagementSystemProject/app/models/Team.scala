package models

import play.api.libs.json.{Json, OFormat}

case class Team(
                 id: Long = 0L,
                 name: String,  // Catering, Decoration, Logistics, Entertainment
                 contactEmail: Option[String] = None,
                 contactPhone: Option[String] = None
               )

object Team {
  implicit val format: OFormat[Team] = Json.format[Team]
}
