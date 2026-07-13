package com.shoppingcart.repository

import com.shoppingcart.domains.Items
import com.shoppingcart.models.*
import cats.syntax.functor.*
import doobie.ConnectionIO
import doobie.Fragment
import doobie.implicits.*
import squants.market.{Currency, Money, defaultMoneyContext}

import java.util.UUID

object DoobieItems extends Items[ConnectionIO] {
  import DoobieMappings.given

  override def findAll: ConnectionIO[List[Item]] =
    selectItems(fr"ORDER BY i.name")

  override def findBy(brand: BrandName): ConnectionIO[List[Item]] =
    selectItems(fr"WHERE b.name = $brand ORDER BY i.name")

  override def findById(itemId: ItemId): ConnectionIO[Option[Item]] =
    selectItems(fr"WHERE i.id = $itemId").map(_.headOption)

  override def create(item: CreateItem): ConnectionIO[ItemId] = {
    val id = ItemId(UUID.randomUUID())
    val amount = item.price.amount
    val currency = item.price.currency.code

    sql"""
      INSERT INTO items (
        id,
        name,
        description,
        price_amount,
        price_currency,
        brand_id,
        category_id
      )
      VALUES (
        $id,
        ${item.name},
        ${item.description},
        $amount,
        $currency,
        ${item.brandId},
        ${item.categoryId}
      )
    """.update.run.as(id)
  }

  override def update(item: UpdateItem): ConnectionIO[Unit] = {
    val amount = item.price.amount
    val currency = item.price.currency.code

    sql"""
      UPDATE items
      SET price_amount = $amount,
          price_currency = $currency
      WHERE id = ${item.id}
    """.update.run.void
  }

  private def selectItems(whereAndOrder: Fragment): ConnectionIO[List[Item]] =
    (sql"""
      SELECT
        i.id,
        i.name,
        i.description,
        i.price_amount,
        i.price_currency,
        b.id,
        b.name,
        c.id,
        c.name
      FROM items i
      INNER JOIN brands b ON b.id = i.brand_id
      INNER JOIN categories c ON c.id = i.category_id
    """ ++ whereAndOrder).query[
      (
          ItemId,
          ItemName,
          ItemDescription,
          BigDecimal,
          String,
          BrandId,
          BrandName,
          CategoryId,
          CategoryName
      )
    ].to[List]
      .map(_.map { case (itemId, name, description, amount, currencyCode, brandId, brandName, categoryId, categoryName) =>
        Item(
          itemId,
          name,
          description,
          Money(amount, Currency(currencyCode)(defaultMoneyContext).get),
          Brand(brandId, brandName),
          Category(categoryId, categoryName)
        )
      })
}
