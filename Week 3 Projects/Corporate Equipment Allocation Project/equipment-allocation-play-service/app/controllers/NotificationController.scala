package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models._
import JsonFormats._

@Singleton
class NotificationController @Inject()(
                                        cc: ControllerComponents,
                                        notificationDAO: NotificationDAO
                                      )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def list = Action.async {
    notificationDAO.list().map(n => Ok(Json.toJson(n)))
  }

  def pending = Action.async {
    notificationDAO.pending().map(n => Ok(Json.toJson(n)))
  }

  def create = Action.async(parse.json) { request =>
    request.body.validate[Notification].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> errors.toString))),
      notif =>
        notificationDAO.create(notif).map(newId =>
          Created(Json.obj("id" -> newId))
        )
    )
  }

  def markSent(id: Long) = Action.async {
    notificationDAO.markSent(id).map(_ => Ok(Json.obj("status" -> "SENT")))
  }

  def markFailed(id: Long) = Action.async(parse.json) { request =>
    val reason = (request.body \ "failureReason").as[String]
    notificationDAO.markFailed(id, reason).map(_ => Ok(Json.obj("status" -> "FAILED")))
  }
}
