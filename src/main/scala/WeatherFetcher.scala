import sttp.client4.*
import io.circe.*
import io.circe.parser.*
import io.circe.generic.semiauto.*

import scala.util.{Success, Failure}
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
    s""" Weather in $name
       | Description: \\u${weather.head.icon} ${weather.head.description}
       | Temperature: ${main.temp}°C
       | Feels like: ${main.feels_like}°C
       | Wind speed: ${wind.speed} m/s
       |""".stripMargin
  )
}

implicit val weatherDecoder: Decoder[Weather] = deriveDecoder
implicit val windDecoder: Decoder[Wind] = deriveDecoder
implicit val mainDecoder: Decoder[Main] = deriveDecoder
implicit val fullResponseDecoder: Decoder[FullResponse] = deriveDecoder

object WeatherFetcher {
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

    request
  }

  private def sendRequest(request: HttpRequest): Future[String ] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(
      res => res.entity.toStrict(2.seconds)
    )
    
    entityFuture.map(entity => entity.data.utf8String)
  }

  def main(args: Array[String]): Unit = {
    val city = "Kazan"
    sendRequest(
     getWeatherByCityRequest(city) 
    ).foreach(println)
  }
}
