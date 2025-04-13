package weather.model

import io.circe.*
import io.circe.generic.semiauto.*


object JsonCodecs {
  implicit val weatherDecoder: Decoder[Weather] = deriveDecoder
  implicit val windDecoder: Decoder[Wind] = deriveDecoder
  implicit val mainDecoder: Decoder[Main] = deriveDecoder
  implicit val fullResponseDecoder: Decoder[FullResponse] = deriveDecoder
}