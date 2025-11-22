package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models._
import JsonFormats._
import services.KafkaProducerService
import events.MaintenanceReportedEvent

@Singleton
class MaintenanceController @Inject()(
                                       cc: ControllerComponents,
                                       maintenanceDAO: MaintenanceLogDAO,
                                       kafkaProducerService: KafkaProducerService
                                     )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def list = Action.async {
    maintenanceDAO.list().map(list => Ok(Json.toJson(list)))
  }

  def find(id: Long) = Action.async {
    maintenanceDAO.find(id).map {
      case Some(m) => Ok(Json.toJson(m))
      case None    => NotFound(Json.obj("error" -> "Maintenance record not found"))
    }
  }

//  def report = Action.async(parse.json) { request =>
//    request.body.validate[MaintenanceLog].fold(
//      errors => Future.successful(BadRequest(Json.obj("error" -> errors.toString))),
//      logg =>
//        for {
//          newId <- maintenanceDAO.create(logg)
//
//          _ <- kafkaProducerService.publishMaintenanceReported(
//            MaintenanceReportedEvent(
//              maintenanceId = newId,
//              equipmentId = logg.equipmentId,
//              description = logg.description, // <-- FIXED
//              maintenanceDate = logg.maintenanceDate.toLocalDateTime, // <-- FIX HERE
//              performedBy = logg.performedBy,
//              severity = logg.severity // <-- FIXED
//            )
//          )
//        } yield Created(Json.obj("id" -> newId))
//    )
//  }

  def report = Action.async(parse.json) { request =>
    request.body.validate[MaintenanceLog].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> errors.toString))),
      logg =>
        for {
          newId <- maintenanceDAO.create(logg)

          _ <- kafkaProducerService.publishMaintenanceReported(
            MaintenanceReportedEvent(
              maintenanceId = newId,
              equipmentId = logg.equipmentId,

              // Option[String] → String
              description = logg.description.getOrElse(""),

              // LocalDateTime (mapped field)
              maintenanceDate = logg.reportedDate,

              // Option[Long] → String
              performedBy = logg.reportedBy.map(_.toString).getOrElse("UNKNOWN"),

              // Option[String] → String
              severity = logg.severity.getOrElse("LOW")
            )
          )
        } yield Created(Json.obj("id" -> newId))
    )
  }


}
