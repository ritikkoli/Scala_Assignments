package controllers

import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject

class EmployeeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.employees())
  }
}
