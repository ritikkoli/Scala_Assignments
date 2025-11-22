package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import services.{EventService, TaskService}
import models.Event
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventController @Inject()(
                                 cc: ControllerComponents,
                                 eventService: EventService,
                                 taskService: TaskService
                               )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /** POST /events */
  def createEvent = Action.async(parse.json) { request =>
    request.body.validate[Event].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> JsError.toJson(errors)))),
      event =>
        eventService.createEvent(event).map(id =>
          Created(Json.obj("message" -> "Event created", "id" -> id))
        )
    )
  }

  /** GET /events/:id */
  def getEvent(id: Long) = Action.async {
    eventService.getEvent(id).map {
      case Some(event) => Ok(Json.toJson(event))
      case None        => NotFound(Json.obj("error" -> "Event not found"))
    }
  }

  /** GET /events */
  def getAllEvents = Action.async {
    eventService.getAllEvents().map(events => Ok(Json.toJson(events)))
  }

  /** GET /events/:id/tasks */
  def getEventTasks(id: Long) = Action.async {
    taskService.getEventTasks(id).map(tasks => Ok(Json.toJson(tasks)))
  }
}
