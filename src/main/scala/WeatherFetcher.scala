import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethod, HttpMethods, HttpRequest, HttpResponse, StatusCodes}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.*


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
