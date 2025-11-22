package models

import play.api.libs.json.{Json, OFormat}

case class TaskAssignedMessage(
                                taskId: Long,
                                eventId: Long,
                                teamId: Long,
                                taskName: String,
                                deadline: String
                              )
object TaskAssignedMessage {
  implicit val format: OFormat[TaskAssignedMessage] = Json.format[TaskAssignedMessage]
}

case class TaskStatusUpdatedMessage(
                                     taskId: Long,
                                     status: String
                                   )
object TaskStatusUpdatedMessage {
  implicit val format: OFormat[TaskStatusUpdatedMessage] = Json.format[TaskStatusUpdatedMessage]
}

case class IssueReportedMessage(
                                 issueId: Long,
                                 taskId: Long,
                                 teamId: Long,
                                 description: String
                               )
object IssueReportedMessage {
  implicit val format: OFormat[IssueReportedMessage] = Json.format[IssueReportedMessage]
}
