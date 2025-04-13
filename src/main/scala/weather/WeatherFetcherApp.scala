package weather

import zio.{Task, ZIO, ZIOAppArgs}
import zio.cli.*
import zio.cli.HelpDoc.Span.text
import weather.service.WeatherFetcher


object WeatherFetcherApp extends ZIOCliDefault {
  private def showWeather(city: String): Task[Unit] = {
    ZIO
      .fromFuture(_ => WeatherFetcher.getWeatherByCity(city))
      .map {
        case Right(data) => data.show()
        case Left(error) => println(error)
      }
  }

  val arguments: Args[String] = Args.text("city")
  private val help: HelpDoc = HelpDoc.p("Shows weather for a given city")

  private val weatherCommand: Command[String] =
    Command("weather", args = arguments)
      .withHelp(help)

  val cliApp: CliApp[ZIOAppArgs, Throwable, Unit] = CliApp.make(
    name = "WeatherFetcher",
    version = "1.0.0",
    summary = text("A tool for fetching weather info for a given city"),
    command = weatherCommand
  ) (showWeather)
}
