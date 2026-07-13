package com.shoppingcart.repository

import cats.effect.IO
import com.shoppingcart.domains.Categories
import com.shoppingcart.models.{Category, CategoryId, CategoryName}
import doobie.Transactor
import doobie.implicits.*

final class LiveCategories(xa: Transactor[IO]) extends Categories[IO] {
  override def findAll: IO[List[Category]] =
    DoobieCategories.findAll.transact(xa)

  override def create(name: CategoryName): IO[CategoryId] =
    DoobieCategories.create(name).transact(xa)
}
