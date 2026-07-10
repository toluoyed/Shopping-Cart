package com.shoppingcart.models

import java.util.UUID
import cats.{Eq, Show}
import io.circe.{Decoder, Encoder}

final case class BrandId(value: UUID)
final case class BrandName(value: String)

case class Brand(uuid: BrandId, name: BrandName)

object BrandName {

  given Eq[BrandName] = Eq.by(_.value)

  given Show[BrandName] =
    Show.show(_.value)

  given Encoder[BrandName] =
    Encoder[String].contramap(_.value)

  given Decoder[BrandName] =
    Decoder[String].map(BrandName.apply)
}


