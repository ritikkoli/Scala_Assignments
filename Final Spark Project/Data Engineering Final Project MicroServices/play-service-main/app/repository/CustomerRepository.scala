package repository

import models.CustomerProfile
import utils.CassandraClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.FutureConverters._

@Singleton
class CustomerRepository @Inject() (client: CassandraClient)(implicit ec: ExecutionContext) {

  private val SELECT_QUERY =
    """SELECT customer_id, customer_name, email, gender,
      | total_spend, total_transactions, avg_order_value,
      | first_purchase, last_purchase, favorite_category,
      | ingestion_timestamp
      | FROM customer_profile
      | WHERE customer_id = ?""".stripMargin

  def getCustomer(id: Int): Future[Option[CustomerProfile]] = {

    val bound = client.session.prepare(SELECT_QUERY).bind(id: java.lang.Integer)

    client.session.executeAsync(bound)
      .asScala
      .map { rs =>
        val row = rs.one()
        if (row == null) None
        else {
          Some(
            CustomerProfile(
              row.getInt("customer_id"),
              row.getString("customer_name"),
              row.getString("email"),
              row.getString("gender"),
              row.getBigDecimal("total_spend"),
              row.getInt("total_transactions"),
              row.getBigDecimal("avg_order_value"),
              row.getInstant("first_purchase").toString,
              row.getInstant("last_purchase").toString,
              row.getString("favorite_category"),
              row.getInstant("ingestion_timestamp").toString
            )
          )
        }
      }
  }
}
