package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models._
import JsonFormats._

@Singleton
class EmployeeController @Inject()(
                                    cc: ControllerComponents,
                                    employeeDAO: EmployeeDAO
                                  )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def list = Action.async {
    employeeDAO.list().map(employees => Ok(Json.toJson(employees)))
  }

  def find(id: Long) = Action.async {
    employeeDAO.find(id).map {
      case Some(e) => Ok(Json.toJson(e))
      case None    => NotFound(Json.obj("error" -> "Employee not found"))
    }
  }

  def create = Action.async(parse.json) { request =>
    request.body.validate[Employee].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> errors.toString))),
      employee =>
        employeeDAO.create(employee).map(newId =>
          Created(Json.obj("id" -> newId))
        )
    )
  }
}
