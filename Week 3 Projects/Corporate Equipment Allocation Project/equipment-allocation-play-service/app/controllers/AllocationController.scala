package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime
import models._
import JsonFormats._
import events.{AllocationCreatedEvent, EquipmentReturnedEvent}
import services.KafkaProducerService


//class AllocationController @Inject()(
//                                      cc: ControllerComponents,
//                                      allocationDAO: AllocationDAO,
//                                      equipmentDAO: EquipmentDAO
//                                    )(implicit ec: ExecutionContext)
//  extends AbstractController(cc) {

@Singleton
  class AllocationController @Inject()(
                                        cc: ControllerComponents,
                                        allocationDAO: AllocationDAO,
                                        equipmentDAO: EquipmentDAO,
                                        kafkaProducerService: KafkaProducerService
                                      )(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def find(id: Long) = Action.async {
    allocationDAO.find(id).map {
      case Some(a) => Ok(Json.toJson(a))
      case None    => NotFound(Json.obj("error" -> "Allocation not found"))
    }
  }

  def listByEmployee(empId: Long) = Action.async {
    allocationDAO.listByEmployee(empId).map(result => Ok(Json.toJson(result)))
  }

//  def allocate = Action.async(parse.json) { request =>
//    request.body.validate[Allocation].fold(
//      errors => Future.successful(BadRequest(Json.obj("error" -> errors.toString))),
//      allocation =>
//        for {
//          id <- allocationDAO.create(allocation)
//          _ <- equipmentDAO.updateAvailability(allocation.equipmentId, available = false)
//        } yield Created(Json.obj("id" -> id))
//    )
//  }


  def allocate = Action.async(parse.json) { request =>
    request.body.validate[Allocation].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> errors.toString))),
      allocation =>
        for {
          id <- allocationDAO.create(allocation)

          _ <- equipmentDAO.updateAvailability(allocation.equipmentId, available = false)

          // Produce Kafka event
          _ <- kafkaProducerService.publishAllocationCreated(
            AllocationCreatedEvent(
              allocationId = id,
              equipmentId = allocation.equipmentId,
              employeeId = allocation.employeeId,
              allocationDate = allocation.allocationDate,
              expectedReturnDate = allocation.expectedReturnDate,
              purpose = allocation.purpose
            )
          )
        } yield Created(Json.obj("id" -> id))
    )
  }


//  def markReturned(id: Long) = Action.async(parse.json) { request =>
//    val returnDate = LocalDateTime.now()
//    val condition  = (request.body \ "returnCondition").asOpt[String]
//
//    allocationDAO.markReturned(id, returnDate, condition).flatMap { updated =>
//      if (updated == 0)
//        Future.successful(NotFound(Json.obj("error" -> "Allocation not found")))
//      else {
//        allocationDAO.find(id).flatMap {
//          case Some(allocation) =>
//            equipmentDAO.updateAvailability(allocation.equipmentId, available = true)
//              .map(_ => Ok(Json.obj("status" -> "RETURNED")))
//          case None => Future.successful(NotFound(Json.obj("error" -> "Invalid allocation")))
//        }
//      }
//    }
//  }

  def markReturned(id: Long) = Action.async(parse.json) { request =>
    val returnDate = LocalDateTime.now()
    val condition: String = (request.body \ "returnCondition").asOpt[String].getOrElse("GOOD")

    allocationDAO.markReturned(id, returnDate, Some(condition)).flatMap { updated =>
      if (updated == 0)
        Future.successful(NotFound(Json.obj("error" -> "Allocation not found")))
      else {

        allocationDAO.find(id).flatMap {
          case Some(allocation) =>

            // 1. Update availability
            val updateFuture = equipmentDAO.updateAvailability(allocation.equipmentId, available = true)

            // 2. Publish Kafka event
            val kafkaFuture = kafkaProducerService.publishEquipmentReturned(
              EquipmentReturnedEvent(
                allocationId = allocation.id,
                equipmentId = allocation.equipmentId,
                employeeId = allocation.employeeId,
                returnDate = returnDate,
                returnCondition = condition
              )
            )

            for {
              _ <- updateFuture
              _ <- kafkaFuture
            } yield Ok(Json.obj("status" -> "RETURNED"))

          case None =>
            Future.successful(NotFound(Json.obj("error" -> "Invalid allocation")))
        }
      }
    }
  }

}
