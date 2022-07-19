ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "scala-microservices-realworld-example-app-users_service-service"
  )

val AkkaVersion = "2.6.19"
val AkkaHttpVersion = "10.2.9"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",
  "com.github.jwt-scala" %% "jwt-core" % "9.0.5",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.slf4j" % "slf4j-nop" % "1.7.36",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.3.6"
)

ThisBuild / assemblyMergeStrategy := {
  case PathList(ps @ _*) if ps.last endsWith "StaticMDCBinder.class" =>
    MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "StaticMarkerBinder.class" =>
    MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "StaticLoggerBinder.class" =>
    MergeStrategy.first
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
