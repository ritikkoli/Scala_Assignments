package controllers

import play.api.libs.json._
import models._

object JsonFormats {
  implicit val equipmentFormat = Json.format[Equipment]
  implicit val employeeFormat = Json.format[Employee]
  implicit val allocationFormat = Json.format[Allocation]
  implicit val maintenanceFormat = Json.format[MaintenanceLog]
  implicit val notificationFormat = Json.format[Notification]
}
