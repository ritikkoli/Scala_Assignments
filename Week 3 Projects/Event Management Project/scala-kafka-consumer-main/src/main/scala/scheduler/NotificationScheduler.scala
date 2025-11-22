package scheduler

import akka.actor.{ActorSystem, Cancellable}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class NotificationScheduler(system: ActorSystem)(implicit ec: ExecutionContext) {

  def start(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(10.seconds, 30.minutes) { () =>
      println("[SCHEDULER] Running periodic notification checks...")
      // In real system: call DB → fetch pending tasks → notify teams
    }
  }
}
