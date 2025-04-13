package utils

// AI generated
object WeatherIcons {
  private val icons = Map(
    "01d" -> "☀",  // Clear sky (day)
    "01n" -> "☽",  // Clear sky (night) - using moon instead of emoji
    "02d" -> "⛅",  // Few clouds
    "02n" -> "☁",  // Few clouds (night)
    "03d" -> "☁",  // Scattered clouds
    "04d" -> "☁",  // Broken clouds
    "09d" -> "☔",  // Showers
    "10d" -> "🌧", // Rain
    "11d" -> "⚡",  // Thunderstorm
    "13d" -> "❄",  // Snow
    "50d" -> "�",  // Mist/fog (basic terminal fallback)

    // Night variants can use same icons or different
    "04n" -> "☁",
    "09n" -> "☔",
    "10n" -> "🌧",
    "11n" -> "⚡",
    "13n" -> "❄",
    "50n" -> "�"
  )

  def apply(code: String): String = icons.getOrElse(code, "?")
}