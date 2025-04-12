import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethod, HttpMethods, HttpRequest, HttpResponse, StatusCodes}

import scala.concurrent.ExecutionContext
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
  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "SprayExample")
  implicit val executionContext: ExecutionContext = system.executionContext
  import system.dispatchers

  private val baseUrl = "https://api.openweathermap.org"
  private val apiKey = sys.env.getOrElse("API_KEY", throw new IllegalStateException("API_KEY environment variable not set"))

  private def getWeatherByCityRequest(city: String): HttpRequest = {
    val uri = s"$baseUrl/data/2.5/weather?q=$city&appid=$apiKey&units=metric"

    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = uri,
    )

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
