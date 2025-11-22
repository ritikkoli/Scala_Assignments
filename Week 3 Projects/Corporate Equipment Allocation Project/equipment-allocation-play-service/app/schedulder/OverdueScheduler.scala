package scheduler

import org.apache.pekko.actor.{Actor, Props}
import java.time.LocalDateTime
import scala.concurrent.ExecutionContext

import models.AllocationDAO
import services.KafkaProducerService
import events.OverdueEvent

class OverdueScheduler(
                        allocationDAO: AllocationDAO,
                        producer: KafkaProducerService
                      )(implicit ec: ExecutionContext) extends Actor {

  override def receive: Receive = {
    case "check-overdue" =>
      val now = LocalDateTime.now()

      allocationDAO.findOverdue(now).foreach { overdueAllocations =>
        overdueAllocations.foreach { a =>
          val event = OverdueEvent(
            allocationId = a.id,
            equipmentId = a.equipmentId,
            employeeId = a.employeeId,
            expectedReturnDate = a.expectedReturnDate,
            actualReturnDate = a.actualReturnDate,
            allocationDate = a.allocationDate
          )

          producer.publishOverdueEvent(event)
        }
      }
  }
}

object OverdueScheduler {
  def props(
             allocationDAO: AllocationDAO,
             producer: KafkaProducerService
           )(implicit ec: ExecutionContext): Props =
    Props(new OverdueScheduler(allocationDAO, producer))
}
