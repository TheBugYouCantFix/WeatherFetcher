ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

name := "WeatherFetcher"
libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client4" %% "core" % "4.0.2"
)
