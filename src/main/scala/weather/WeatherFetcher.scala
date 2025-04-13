import sttp.client4.*
import io.circe.*
import io.circe.parser.*

import sttp.client4.*

import zio.cli.HelpDoc.Span.text
import zio.cli.*
import zio.{Task, ZIO, ZIOAppArgs}

import scala.util.{Either, Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// The classes below represent the overall structure of the json response
case class Weather(
  description: String,
  icon: String
)

case class Wind(
               speed: Double
               )

case class Main(
               temp: Double,
               feels_like: Double
               )

case class FullResponse(
  weather: List[Weather],
  wind: Wind,
  main: Main,
  name: String // city name
) {

  def show(): Unit = println(
    s""" ${name.capitalize} ${WeatherIcons(weather.head.icon)}
       | ${main.temp.round}°C ${weather.head.description}
       | Feels like: ${main.feels_like.round}°C
       | Wind speed: ${wind.speed.round} m/s
       |""".stripMargin
  )
}

implicit val weatherDecoder: Decoder[Weather] = deriveDecoder
implicit val windDecoder: Decoder[Wind] = deriveDecoder
implicit val mainDecoder: Decoder[Main] = deriveDecoder
implicit val fullResponseDecoder: Decoder[FullResponse] = deriveDecoder

private object WeatherFetcher extends ZIOCliDefault {
  private val baseUrl = "https://api.openweathermap.org"
  private val apiKey = sys.env.getOrElse("API_KEY", throw new IllegalStateException("API_KEY environment variable not set"))
  private val backend = DefaultFutureBackend()

  private def getWeatherByCity(city: String): Future[Either[String, FullResponse]] = {
    val uri = uri"$baseUrl/data/2.5/weather?q=$city&appid=$apiKey&units=metric"

    basicRequest
      .get(uri)
      .send(backend)
      .map {
        case Response(Right(s), _, _, _, _, _) =>
          decode[FullResponse](s) match
            case Right(s) => Right(s)
            case Left(err) => Left(err.toString)

        case Response(Left(err), _, _, _, _, _)
        => Left(err)
      }
  }


  def showWeather(city: String): Task[Unit] = {
    ZIO
      .fromFuture(_ => getWeatherByCity(city))
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
  ) {
    city => showWeather(city)
  }

}