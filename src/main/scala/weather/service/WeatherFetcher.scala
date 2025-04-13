package weather.service

import io.circe.*
import io.circe.parser.*

import sttp.client4.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import weather.model.FullResponse
import weather.model.JsonCodecs.*


object WeatherFetcher {
  private val baseUrl = "https://api.openweathermap.org"
  private val apiKey = sys.env.getOrElse("API_KEY", throw new IllegalStateException("API_KEY environment variable not set"))
  private val backend = DefaultFutureBackend()

  def getWeatherByCity(city: String): Future[Either[String, FullResponse]] = {
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
}