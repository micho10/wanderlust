package controllers

import javax.inject._

import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 *
 * Controllers MUST be defined as classes, to take advantage of
 * Dependency Injection (DI).
 *
 * The Inject tag indicates it's using an injected routes generator.
 *
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  // Retrieve the incoming session from the HTTP request
  def indexx = Action { implicit request =>
    request.session.get("connected").map { user =>
      Ok("Hello " + user)
    }.getOrElse(
      Unauthorized("Oops, you are not connected")
    )
  }

  /** :::::::::::::::: Actions ::::::::::::::::
   *
   * The play.api.mvc.Action companion object offers several
   * helper methods to construct an Action value.
   */

  // Takes as argument an expression block returning a Result
  Action { implicit request =>
    Ok("Hello world!")
/*
    // Change default Content-Type to text/html
    Ok(<h1>Hello world!</h1>).as(HTML)

    // Add (or update) any HTTP headers
    Ok("Hello world!").withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      ETAG -> "xx"
    )

    // Add cookies to the result
    val result = Ok("Hello world!").withCookies(
      Cookie("theme", "blue")
    )

    // Discard a Cookie previously stored
    val result2 = result.discardingCookies(DiscardingCookie("theme"))

    // Reply adding a new session
    Ok("Welcome!").withSession("connected" -> "user@gmail.com")

    // Reply adding contents to the existing session
    Ok("Hello World!").withSession(request.session + ("saidHello" -> "yes"))

    // Remove a value from the current session
    Ok("Theme sent!").withSession(request.session - "theme")

    // Discard the whole session
    Ok("Bye").withNewSession
*/
  }

  // Takes as an argument a function Request => Result
  Action { request =>
    Ok("Got request [" + request + "]")
  }

  // It's often useful to mark the Request parameter as `implicit`
  // so it can be implicitly used by other APIs that need it
  Action { implicit request =>
    Ok("Got request [" + request + "]")
  }

  // Specify an additional `BodyParser` argument. Others methods creating
  // Action values use a default "Any content body parser"
  Action(parse.json) { implicit request =>
    Ok("Got request [" + request + "]")
  }

  def index2 = Action {
    Result(
      header = ResponseHeader(200, Map.empty),
      body = HttpEntity.Strict(ByteString("Hello world!"), Some("text/plain"))
    )
  }

  def index3 = Action {
    Redirect("/user/home")
  }

  def index4 = Action {
    Redirect("/user/home", MOVED_PERMANENTLY)
  }

  // Dummy page
  def index5(name: String) = TODO

  // Example of use of the flash scope
  def index6 = Action { implicit request =>
    Ok {
      request.flash.get("success").getOrElse("Welcome!")
    }
  }

  // Example of use of the flash scope
  def save = Action {
    Redirect("/home").flashing(
      "success" -> "The item has been created"
    )
  }

}
