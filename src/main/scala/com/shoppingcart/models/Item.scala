package com.shoppingcart.models

import java.util.UUID
import squants.market.Money

case class ItemId(value: UUID)
case class ItemName(value: String)
case class ItemDescription(value: String)

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
