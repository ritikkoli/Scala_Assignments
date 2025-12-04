package scheduler

import repository.TaskRepository
import models.Task
import akka.actor.ActorRef
import scala.concurrent.{Future, ExecutionContext}

class TaskSchedulerService(
                            taskRepo: TaskRepository,
                            emailActor: ActorRef
                          )(implicit ec: ExecutionContext) {

  def checkAndNotify(): Future[Unit] = {
    taskRepo.getTasksApproachingDeadline().map { tasks =>
      tasks.foreach { task =>
        val msg =
          s"Reminder: Task '${task.taskName}' for Team ${task.teamId} is approaching deadline (${task.deadline})."

        // Send email (actor will handle retries)
        emailActor ! ("team@example.com", msg)
      }
    }
  }
}
