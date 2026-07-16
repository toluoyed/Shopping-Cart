package com.shoppingcart.repository

import cats.effect.Async
import cats.syntax.all.*
import com.shoppingcart.domains.Items
import com.shoppingcart.effects.GenUUID
import com.shoppingcart.models.*
import doobie.ConnectionIO
import doobie.Fragment
import doobie.Transactor
import doobie.implicits.*
import squants.market.{Currency, Money, defaultMoneyContext}

object DoobieItems {
  import DoobieMappings.given

  def make[F[_]: Async: GenUUID](xa: Transactor[F]): Items[F] =
    new Items[F] {
      override def findAll: F[List[Item]] =
        ItemSql.selectItems(fr"ORDER BY i.name").transact(xa)

      override def findBy(brand: BrandName): F[List[Item]] =
        ItemSql.selectItems(fr"WHERE b.name = $brand ORDER BY i.name").transact(xa)

      override def findById(itemId: ItemId): F[Option[Item]] =
        ItemSql.selectItems(fr"WHERE i.id = $itemId").map(_.headOption).transact(xa)

      override def create(item: CreateItem): F[ItemId] =
        for {
          id <- GenUUID[F].make.map(ItemId.apply)
          affectedRows <- ItemSql.insert(id, item).run.transact(xa)
          _ <-
            if affectedRows == 1 then Async[F].unit
            else
              Async[F].raiseError(
                new IllegalStateException(s"Expected one inserted item, but inserted $affectedRows")
              )
        } yield id

      override def update(item: UpdateItem): F[Unit] =
        for {
          affectedRows <- ItemSql.update(item).run.transact(xa)
          _ <-
            if affectedRows == 1 then Async[F].unit
            else
              Async[F].raiseError(
                new IllegalStateException(s"Expected one updated item, but updated $affectedRows")
              )
        } yield ()
    }

  private object ItemSql {
    def insert(id: ItemId, item: CreateItem) = {
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
      """.update
    }

    def update(item: UpdateItem) = {
      val amount = item.price.amount
      val currency = item.price.currency.code

      sql"""
        UPDATE items
        SET price_amount = $amount,
            price_currency = $currency
        WHERE id = ${item.id}
      """.update
    }

    def selectItems(whereAndOrder: Fragment): ConnectionIO[List[Item]] =
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
}
