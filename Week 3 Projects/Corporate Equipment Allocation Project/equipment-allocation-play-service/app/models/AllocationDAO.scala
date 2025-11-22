package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationDAO @Inject()(
                               dbConfigProvider: DatabaseConfigProvider
                             )(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val allocations = TableQuery[AllocationTable]

  def list(): Future[Seq[Allocation]] =
    db.run(allocations.result)

  def find(id: Long): Future[Option[Allocation]] =
    db.run(allocations.filter(_.id === id).result.headOption)

  def create(a: Allocation): Future[Long] =
    db.run((allocations returning allocations.map(_.id)) += a)

  def listByEmployee(empId: Long): Future[Seq[Allocation]] =
    db.run(allocations.filter(_.employeeId === empId).result)

  def markReturned(id: Long, returnDate: java.time.LocalDateTime, condition: Option[String]): Future[Int] =
    db.run {
      allocations
        .filter(_.id === id)
        .map(a => (a.actualReturnDate, a.returnCondition, a.status))
        .update((Some(returnDate), condition, "RETURNED"))
    }

  def findOverdue(now: LocalDateTime): Future[Seq[Allocation]] = {
    val query = allocations.filter(a =>
      a.expectedReturnDate < now &&
        a.actualReturnDate.isEmpty
    )

    db.run(query.result)
  }

}
