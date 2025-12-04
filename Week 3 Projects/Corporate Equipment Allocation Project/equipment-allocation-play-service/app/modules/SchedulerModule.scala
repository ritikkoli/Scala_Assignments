package modules

import org.apache.pekko.actor.ActorSystem
import play.api.inject.{SimpleModule, _}
import javax.inject._

import scheduler.OverdueScheduler
import models.AllocationDAO
import services.KafkaProducerService

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class SchedulerModule extends SimpleModule(bind[SchedulerInitializer].toSelf.eagerly())

@Singleton
class SchedulerInitializer @Inject()(
                                      system: ActorSystem,
                                      allocationDAO: AllocationDAO,
                                      kafkaProducer: KafkaProducerService
                                    )(implicit ec: ExecutionContext) {

  private val overdueActor = system.actorOf(
    OverdueScheduler.props(allocationDAO, kafkaProducer),
    "overdue-scheduler"
  )

  system.scheduler.scheduleWithFixedDelay(
    10.seconds,
    1.hour,
    overdueActor,
    "check-overdue"
  )
}
