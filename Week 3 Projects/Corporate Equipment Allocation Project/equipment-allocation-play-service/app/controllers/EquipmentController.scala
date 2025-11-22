package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models._
import JsonFormats._

@Singleton
class EquipmentController @Inject()(
                                     cc: ControllerComponents,
                                     equipmentDAO: EquipmentDAO
                                   )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def list = Action.async {
    equipmentDAO.list().map(e => Ok(Json.toJson(e)))
  }

  def find(id: Long) = Action.async {
    equipmentDAO.find(id).map {
      case Some(eq) => Ok(Json.toJson(eq))
      case None     => NotFound(Json.obj("error" -> "Equipment not found"))
    }
  }

  def create = Action.async(parse.json) { request =>
    request.body.validate[Equipment].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> errors.toString))),
      equipment =>
        equipmentDAO.create(equipment).map(newId =>
          Created(Json.obj("id" -> newId))
        )
    )
  }
}
