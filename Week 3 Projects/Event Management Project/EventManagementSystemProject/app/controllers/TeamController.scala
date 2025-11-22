package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import services.TeamService
import models.Team
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TeamController @Inject()(
                                cc: ControllerComponents,
                                teamService: TeamService
                              )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /** POST /teams */
  def createTeam = Action.async(parse.json) { request =>
    request.body.validate[Team].fold(
      e => Future.successful(BadRequest(JsError.toJson(e))),
      team =>
        teamService.createTeam(team).map(id =>
          Created(Json.obj("message" -> "Team created", "id" -> id))
        )
    )
  }

  /** GET /teams */
  def getAllTeams = Action.async {
    teamService.getAllTeams().map(teams => Ok(Json.toJson(teams)))
  }

  /** GET /teams/:id */
  def getTeam(id: Long) = Action.async {
    teamService.getTeam(id).map {
      case Some(team) => Ok(Json.toJson(team))
      case None       => NotFound(Json.obj("error" -> "Team not found"))
    }
  }
}
