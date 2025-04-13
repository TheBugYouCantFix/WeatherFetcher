ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

val sttpVersion = "4.0.2"
val circeVersion = "0.14.12"
val zioVersion = "2.1.17"

name := "WeatherFetcher"
libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client4" %% "core" % sttpVersion,
  "com.softwaremill.sttp.client4" %% "circe" % sttpVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-cli" % "0.7.1"
)
