package repository

import tables.TaskTable
import models.Task
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Future, ExecutionContext}

class TaskRepository(db: Database)(implicit ec: ExecutionContext) {

  def getTasksApproachingDeadline(): Future[Seq[Task]] = {
    val now = java.time.LocalDateTime.now()
    val nextHour = now.plusHours(1)

    db.run(
      TaskTable.query
        .filter(_.status =!= "Completed")
        .filter(_.deadline.between(now, nextHour))
        .result
    )
  }
}
