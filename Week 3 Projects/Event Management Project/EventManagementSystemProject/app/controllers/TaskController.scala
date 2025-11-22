package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import services.TaskService
import models.Task
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaskController @Inject()(
                                cc: ControllerComponents,
                                taskService: TaskService
                              )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /** POST /tasks */
  def createTask = Action.async(parse.json) { request =>
    request.body.validate[Task].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      task =>
        taskService.createTask(task).map {
          case Right(id) => Created(Json.obj("message" -> "Task created", "id" -> id))
          case Left(msg) => BadRequest(Json.obj("error" -> msg))
        }
    )
  }

  /** PATCH /tasks/:id/status */
  def updateTaskStatus(id: Long) = Action.async(parse.json) { request =>
    (request.body \ "status").asOpt[String] match {
      case Some(status) =>
        taskService.updateTaskStatus(id, status).map {
          case true  => Ok(Json.obj("message" -> "Task status updated"))
          case false => NotFound(Json.obj("error" -> "Task not found"))
        }

      case None => Future.successful(BadRequest(Json.obj("error" -> "Missing status")))
    }
  }

  /** GET /tasks/:id */
  def getTask(id: Long) = Action.async {
    taskService.getTask(id).map {
      case Some(task) => Ok(Json.toJson(task))
      case None       => NotFound(Json.obj("error" -> "Task not found"))
    }
  }

  /** GET /teams/:teamId/tasks */
  def getTasksByTeam(teamId: Long) = Action.async {
    taskService.tasksByTeam(teamId).map(tasks => Ok(Json.toJson(tasks)))
  }
}
