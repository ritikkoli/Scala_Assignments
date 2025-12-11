package models

import play.api.libs.json.{Json, OFormat}

case class DailyTxnSummary(
                            date: String,
                            customer_id: Int,
                            total_amount: BigDecimal,
                            total_items: Int,
                            distinct_products: Int,
                            top_category: String
                          )

object DailyTxnSummary {
  implicit val format: OFormat[DailyTxnSummary] = Json.format[DailyTxnSummary]
}
