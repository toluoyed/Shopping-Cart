package com.shoppingcart.models

import cats.{Eq, Show}
import io.circe.{Decoder, Encoder}
import java.util.UUID
import squants.market.Money

case class ItemId(value: UUID)
final case class ItemName(value: String)
final case class ItemDescription(value: String)

object ItemName {

  given Eq[ItemName] = Eq.by(_.value)

  given Show[ItemName] =
    Show.show(_.value)

  given Encoder[ItemName] =
    Encoder[String].contramap(_.value)

  given Decoder[ItemName] =
    Decoder[String].map(ItemName.apply)
}

object ItemDescription {

  given Eq[ItemDescription] = Eq.by(_.value)

  given Show[ItemDescription] =
    Show.show(_.value)

  given Encoder[ItemDescription] =
    Encoder[String].contramap(_.value)

  given Decoder[ItemDescription] =
    Decoder[String].map(ItemDescription.apply)
}

case class Item(
                 uuid: ItemId,
                 name: ItemName,
                 description: ItemDescription,
                 price: Money,
                 brand: Brand,
                 category: Category
               )

case class CreateItem(
                       name: ItemName,
                       description: ItemDescription,
                       price: Money,
                       brandId: BrandId,
                       categoryId: CategoryId
                     )

case class UpdateItem(
                       id: ItemId,
                       price: Money
                     )
