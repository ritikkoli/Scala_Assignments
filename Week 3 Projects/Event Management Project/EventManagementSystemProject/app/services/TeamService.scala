package services

import models.Team
import repository.TeamRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TeamService @Inject()(teamRepo: TeamRepository)(implicit ec: ExecutionContext) {

  def createTeam(team: Team): Future[Long] =
    teamRepo.create(team)

  def getAllTeams(): Future[Seq[Team]] =
    teamRepo.getAll()

  def getTeam(id: Long): Future[Option[Team]] =
    teamRepo.getById(id)
}
