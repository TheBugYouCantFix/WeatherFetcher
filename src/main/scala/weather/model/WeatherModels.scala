package weather.model

import weather.utils.WeatherIcons

// The classes below represent the overall structure of the json response
// Only the necessary values are parsed and the rest is left out
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
       | ${main.temp.round}°C, ${weather.head.description}
       | Feels like: ${main.feels_like.round}°C
       | Wind speed: ${wind.speed.round} m/s
       |""".stripMargin
  )
}
