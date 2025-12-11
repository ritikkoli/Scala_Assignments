package controllers

import javax.inject._
import play.api.mvc._
import repository.TxnSummaryRepository
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json

@Singleton
class TxnSummaryController @Inject()(
                                      cc: ControllerComponents,
                                      repo: TxnSummaryRepository
                                    )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getSummary(date: String, customerId: Int) = Action {

    val results = repo.getDailySummary(date, customerId)

    if (results.isEmpty)
      Ok(s"No summary for date=$date and customer_id=$customerId")
    else
      Ok(Json.toJson(results))
  }
}
