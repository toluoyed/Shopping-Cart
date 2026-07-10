package com.shoppingcart.models

import cats.{Eq, Show}
import io.circe.{Decoder, Encoder}

import java.util.UUID

case class CategoryId(value: UUID)
final case class CategoryName(value: String)

case class Category(uuid: CategoryId, name: CategoryName)

object CategoryName {

  given Eq[CategoryName] = Eq.by(_.value)

  given Show[CategoryName] =
    Show.show(_.value)

  given Encoder[CategoryName] =
    Encoder[String].contramap(_.value)

  given Decoder[CategoryName] =
    Decoder[String].map(CategoryName.apply)
}
