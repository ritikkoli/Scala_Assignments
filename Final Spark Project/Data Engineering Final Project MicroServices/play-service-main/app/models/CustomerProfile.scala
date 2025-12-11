package models

import play.api.libs.json._

case class CustomerProfile(
                            customer_id: Int,
                            customer_name: String,
                            email: String,
                            gender: String,
                            total_spend: BigDecimal,
                            total_transactions: Int,
                            avg_order_value: BigDecimal,
                            first_purchase: String,
                            last_purchase: String,
                            favorite_category: String,
                            ingestion_timestamp: String
                          )

object CustomerProfile {
  implicit val format: OFormat[CustomerProfile] = Json.format[CustomerProfile]
}
