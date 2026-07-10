package com.shoppingcart.models

import cats.syntax.either.*
import io.circe.{Decoder, Encoder, Json, KeyDecoder, KeyEncoder}
import io.circe.generic.semiauto.*
import squants.market.Money

import java.util.UUID

object JsonCodecs {

  given Encoder[UserId] = Encoder[String].contramap(_.value.toString)
  given Decoder[UserId] =
    Decoder[String].emap(s => Either.catchNonFatal(UserId(UUID.fromString(s))).leftMap(_.getMessage))

  given Encoder[BrandId] = Encoder[String].contramap(_.value.toString)
  given Decoder[BrandId] =
    Decoder[String].emap(s => Either.catchNonFatal(BrandId(UUID.fromString(s))).leftMap(_.getMessage))

  given Encoder[CategoryId] = Encoder[String].contramap(_.value.toString)
  given Decoder[CategoryId] =
    Decoder[String].emap(s => Either.catchNonFatal(CategoryId(UUID.fromString(s))).leftMap(_.getMessage))

  given Encoder[ItemId] = Encoder[String].contramap(_.value.toString)
  given Decoder[ItemId] =
    Decoder[String].emap(s => Either.catchNonFatal(ItemId(UUID.fromString(s))).leftMap(_.getMessage))

  given KeyEncoder[ItemId] = (key: ItemId) => key.value.toString
  given KeyDecoder[ItemId] = (key: String) =>
    Either.catchNonFatal(ItemId(UUID.fromString(key))).toOption

  given Encoder[Quantity] = Encoder[Int].contramap(_.value)
  given Decoder[Quantity] = Decoder[Int].emap(value =>
    Either.cond(
      value > 0,
      Quantity(value),
      "Quantity must be greater than zero"
    ))

  given Encoder[OrderId] = Encoder[String].contramap(_.uuid.toString)

  given Encoder[PaymentId] = Encoder[String].contramap(_.uuid.toString)

  given Encoder[Money] =
    Encoder.instance { money =>
      Json.obj(
        "amount" -> Json.fromBigDecimal(money.amount),
        "currency" -> Json.fromString(money.currency.code)
      )
    }

  given Encoder[Status] = Encoder[String].contramap {
    case Status.Okay        => "Okay"
    case Status.Unreachable => "Unreachable"
  }

  given Encoder[User] = deriveEncoder[User]
  given Encoder[Brand] = deriveEncoder[Brand]
  given Encoder[Category] = deriveEncoder[Category]
  given Encoder[Item] = deriveEncoder[Item]
  given Decoder[Cart] = deriveDecoder[Cart]
  given Encoder[Cart] = deriveEncoder[Cart]
  given Encoder[CartItem] = deriveEncoder[CartItem]
  given Encoder[CartTotal] = deriveEncoder[CartTotal]
  given Encoder[Order] = deriveEncoder[Order]
  given Encoder[RedisStatus] = deriveEncoder[RedisStatus]
  given Encoder[PostgresStatus] = deriveEncoder[PostgresStatus]
  given Encoder[AppStatus] = deriveEncoder[AppStatus]
}
