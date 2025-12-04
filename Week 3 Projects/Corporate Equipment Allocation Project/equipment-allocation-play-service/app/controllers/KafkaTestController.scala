package controllers

import akka.actor.TypedActor.dispatcher

import javax.inject._
import play.api.mvc._
import services.KafkaProducerService
import events.AllocationCreatedEvent

import java.time.LocalDateTime

@Singleton
class KafkaTestController @Inject()(
                                     cc: ControllerComponents,
                                     kafka: KafkaProducerService
                                   ) extends AbstractController(cc) {

  def testEvent = Action.async {
    val event = AllocationCreatedEvent(
      allocationId = 999,
      equipmentId = 5,
      employeeId = 20,
      allocationDate = LocalDateTime.now(),
      expectedReturnDate = LocalDateTime.now().plusDays(2),
      purpose = Some("Testing Kafka")
    )
    kafka.publishAllocationCreated(event).map(_ => Ok("Sent"))
  }
}
