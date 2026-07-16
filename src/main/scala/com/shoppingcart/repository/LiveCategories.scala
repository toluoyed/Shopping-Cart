package com.shoppingcart.repository

import cats.effect.IO
import com.shoppingcart.domains.Categories
import com.shoppingcart.models.{Category, CategoryId, CategoryName}
import doobie.Transactor

final class LiveCategories(xa: Transactor[IO]) extends Categories[IO] {
  private val categories: Categories[IO] =
    DoobieCategories.make[IO](xa)

  override def findAll: IO[List[Category]] =
    categories.findAll

  override def create(name: CategoryName): IO[CategoryId] =
    categories.create(name)
}
