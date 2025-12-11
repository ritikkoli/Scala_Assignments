package models

import play.api.libs.json.{Json, OFormat}

//case class BehaviourEvent(
//                           customer_id: Int,
//                           product_id: Option[Int],
//                           event_type: String,
//                           event_timestamp: String,
//                           ingestion_timestamp: String
//                         )

case class BehaviourEvent(
                           event_id: Option[String],
                           customer_id: Option[Int],
                           event_type: Option[String],
                           product_id: Option[Int],
                           event_timestamp: Option[String],   // already string in schema
                           is_valid: Option[Boolean],         // optional
                         //  is_unknown_customer :Option[Boolean],
                           event_time: Option[String],        // decode INT96 → string
                           ingestion_timestamp: Option[String] // decode INT96 → string
                         )


object BehaviourEvent {
  implicit val format: OFormat[BehaviourEvent] = Json.format[BehaviourEvent]
}
