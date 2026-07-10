package com.shoppingcart.models

import cats.{Eq, Show}
import io.circe.{Decoder, Encoder}
import java.util.UUID
import squants.market.Money

case class PaymentId(uuid: UUID)
final case class Card(value: String)

object Card {

  given Eq[Card] = Eq.by(_.value)

  given Show[Card] =
    Show.show(_.value)

  given Encoder[Card] =
    Encoder[String].contramap(_.value)

  given Decoder[Card] =
    Decoder[String].map(Card.apply)
}

case class Payment(
                  id: UserId,
                  total: Money,
                  card: Card
                  
                  )
