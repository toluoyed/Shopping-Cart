package com.shoppingcart.repository

import cats.effect.IO
import com.shoppingcart.domains.Items
import com.shoppingcart.models.{BrandName, CreateItem, Item, ItemId, UpdateItem}
import doobie.Transactor

final class LiveItems(xa: Transactor[IO]) extends Items[IO] {
  private val items: Items[IO] =
    DoobieItems.make[IO](xa)

  override def findAll: IO[List[Item]] =
    items.findAll

  override def findBy(brand: BrandName): IO[List[Item]] =
    items.findBy(brand)

  override def findById(itemId: ItemId): IO[Option[Item]] =
    items.findById(itemId)

  override def create(item: CreateItem): IO[ItemId] =
    items.create(item)

  override def update(item: UpdateItem): IO[Unit] =
    items.update(item)
}
