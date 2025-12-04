package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeDAO @Inject()(
                             dbConfigProvider: DatabaseConfigProvider
                           )(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val employees = TableQuery[EmployeeTable]

  def list(): Future[Seq[Employee]] =
    db.run(employees.result)

  def find(id: Long): Future[Option[Employee]] =
    db.run(employees.filter(_.id === id).result.headOption)

  def create(e: Employee): Future[Long] =
    db.run((employees returning employees.map(_.id)) += e)
}
