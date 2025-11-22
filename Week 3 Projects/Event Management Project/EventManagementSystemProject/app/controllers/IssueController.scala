package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import services.IssueService
import models.Issue
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IssueController @Inject()(
                                 cc: ControllerComponents,
                                 issueService: IssueService
                               )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /** POST /issues */
  def reportIssue = Action.async(parse.json) { request =>
    request.body.validate[Issue].fold(
      e => Future.successful(BadRequest(JsError.toJson(e))),
      issue =>
        issueService.reportIssue(issue).map {
          case Right(id) => Created(Json.obj("message" -> "Issue reported", "id" -> id))
          case Left(msg) => BadRequest(Json.obj("error" -> msg))
        }
    )
  }

  /** GET /tasks/:taskId/issues */
  def getIssuesForTask(taskId: Long) = Action.async {
    issueService.getIssuesForTask(taskId).map(list => Ok(Json.toJson(list)))
  }

  /** GET /issues */
  def getAllIssues = Action.async {
    issueService.getAll().map(list => Ok(Json.toJson(list)))
  }
}
