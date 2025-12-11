package controllers

import repository.CustomerRepository
import play.api.mvc._
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CustomerController @Inject() (
                                     cc: ControllerComponents,
                                     repo: CustomerRepository
                                   )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getCustomer(id: Int) = Action.async {
    repo.getCustomer(id).map {
      case Some(profile) => Ok(play.api.libs.json.Json.toJson(profile))
      case None          => NotFound(s"No customer found with id = $id")
    }
  }
}
