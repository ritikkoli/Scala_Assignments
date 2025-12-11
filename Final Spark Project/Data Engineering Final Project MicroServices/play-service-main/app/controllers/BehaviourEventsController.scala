package controllers

import javax.inject._
import play.api.mvc._
import repository.BehaviourEventsRepository
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json

@Singleton
class BehaviourEventsController @Inject()(cc: ControllerComponents, repo: BehaviourEventsRepository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getEvents(customerId: Int, limit: Option[Int]) = Action {
    val n = limit.getOrElse(10)
    val events = repo.getLastEvents(customerId, n)
    Ok(Json.toJson(events))
  }
}
