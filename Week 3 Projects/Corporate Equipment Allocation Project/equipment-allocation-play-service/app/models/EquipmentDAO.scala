package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentDAO @Inject()(
                              dbConfigProvider: DatabaseConfigProvider
                            )(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val equipments = TableQuery[EquipmentTable]

  def list(): Future[Seq[Equipment]] =
    db.run(equipments.result)

  def find(id: Long): Future[Option[Equipment]] =
    db.run(equipments.filter(_.id === id).result.headOption)

  def create(e: Equipment): Future[Long] =
    db.run((equipments returning equipments.map(_.id)) += e)

  /** Update availability status of equipment */
  def updateAvailability(id: Long, available: Boolean): Future[Int] =
    db.run(
      equipments
        .filter(_.id === id)
        .map(_.isAvailable)
        .update(available)
    )

  /** Update condition (GOOD, DAMAGED, SERVICING, etc.) */
  def updateCondition(id: Long, condition: String): Future[Int] =
    db.run(
      equipments
        .filter(_.id === id)
        .map(_.conditionStatus)
        .update(condition)
    )
}
