package com.shoppingcart.repository

import cats.effect.IO
import com.shoppingcart.domains.Items
import com.shoppingcart.models.{BrandName, CreateItem, Item, ItemId, UpdateItem}
import doobie.Transactor
import doobie.implicits.*

final class LiveItems(xa: Transactor[IO]) extends Items[IO] {
  override def findAll: IO[List[Item]] =
    DoobieItems.findAll.transact(xa)

  override def findBy(brand: BrandName): IO[List[Item]] =
    DoobieItems.findBy(brand).transact(xa)

  override def findById(itemId: ItemId): IO[Option[Item]] =
    DoobieItems.findById(itemId).transact(xa)

  override def create(item: CreateItem): IO[ItemId] =
    DoobieItems.create(item).transact(xa)

  override def update(item: UpdateItem): IO[Unit] =
    DoobieItems.update(item).transact(xa)
}
