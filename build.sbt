name := """wanderlust"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

scalacOptions := Seq(
  "-unchecked",
  "-feature",
  "-deprecation",
  "-encoding",
  "utf8")

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

/** Needed by Activator */
fork in run := true

/******************** Extra Play Framework devSettings **********************
  *
  * You can configure extra settings for the run command in your build.sbt.
  * These settings won’t be used when you deploy your application.
  *
  * */
//PlayKeys.devSettings := Seq("play.server.http.port" -> "8080")

/**
  * By default (since 2.5.0), Play will generate a router that will declare all the controllers that it routes to
  * as dependencies, allowing the controllers to be dependency injected themselves.
  *
  * When using the injected routes generator, prefixing the action with an @ symbol means a Provider of the controller
  * will be injected, instead of the controller being injected directly. This allows, for example,
  * prototype controllers, as well as an option for breaking cyclic dependencies.
  *
  * Enable the injected routes generator.
  */
routesGenerator := InjectedRoutesGenerator
