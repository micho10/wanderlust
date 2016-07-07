package controllers

import java.io.File
import javax.inject._

import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.libs.json.JsValue
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

  /*
  // Retrieve the incoming session from the HTTP request
  def indexx = Action { implicit request =>
    request.session.get("connected").map { user =>
      Ok("Hello " + user)
    }.getOrElse(
      Unauthorized("Oops, you are not connected")
    )
  }
*/

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

  /* Example of built-in Body Parser
   *
   * Mapping of types supported by the default body parser:
   *    - text/plain                      String                    asText
   *    - application/json                JsValue                   asJson
   *    - application/xml                 scala.xml.NodeSeq         asXml
   *    + text/xml
   *    + application/XXX+xml
   *    - application/form-url-encoded    Map[String, Seq[String]]  asFormUrlEncoded
   *    - multipart/form-data             MultipartFormData         asMultipartFormData
   *    - any other                       RawBuffer                 asRaw
   *
   * The default body parser won't attempt to parse the body if the request method is not defined
   * to have a meaningful body, as defined by the HTTP spec. It only parses the bodies of (requests):
   *    - POST
   *    - PUT
   *    - PATCH
   *  not:
   *    - GET
   *    - HEAD
   *    - DELETE
   *  The "anyContent" body parser can be used for these requests.
   */
  def saveBI = Action { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    // Expecting json body
    jsonBody.map { json =>
      Ok("Got: " + (json \ "name").as[String])
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  /* Example of explicit Body Parser
   *
   * Pass the desired body parser to the Action.apply or Action.async method.
   *
   * The body is no longer an Option because the json body parser will validate that the request has
   * Content-Type of "application/json" and send back a "415 Unsupported Media Type" response if it's not.
   *
   */
  def saveC = Action(parse.json) { implicit request =>
    Ok("Got: " + (request.body \ "name").as[String])
  }

  /* Example of explicit Body Parser
   *
   * This example uses "tolerantJson", which ignores the Content-Type and tries to parse the request as JSON.
   */
  def saveTJ = Action(parse.tolerantJson) { implicit request =>
    Ok("Got: " + (request.body \ "name").as[String])
  }

  /* Example of explicit Body Parser
   *
   * This example stores the request body in a file.
   */
  def saveF = Action(parse.file(to = new File("/tmp/upload"))) { implicit request =>
    Ok("Saved the content to " + request.body)
  }

  /*
   * Combining Body Parsers
   */
  val storeInUserFile = parse.using { implicit request =>
    request.session.get("username").map { user =>
      parse.file(to = new File("/tmp/" + user + ".upload"))
    }.getOrElse {
      sys.error("You don't have the right to upload here")
    }
  }

  def saveFC = Action(storeInUserFile) { implicit request =>
    Ok("Saved the content to " + request.body)
  }

  /*
   * Text based Body Parsers use a "max content length" because they have to load all the content into memory
   * (default 100 KB). It can be overriden specifying this property in application.conf
   *
   * play.http.parser.maxMemoryBuffer = 128K
   *
   * For parsers that buffer content on disk (e.g. multipart/form-data), the maximum content (default 10 MB)
   * is specified using:
   *
   * play.http.parser.maxDiskBuffer = 20MB
   *
   */

  // Override default max length for a given Action (accept only 10KB of data)
  def saveML = Action(parse.text(maxLength = 1024 * 10)) { implicit request =>
    Ok("Got: " + request.body)
  }

  // Wrap any parser with max length
  def saveW = Action(parse.maxLength(1024 * 10, storeInUserFile)) { implicit request =>
    Ok("Saved the content to " + request.body)
  }

}
