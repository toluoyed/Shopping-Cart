package com.shoppingcart.repository

import cats.effect.IO
import com.shoppingcart.domains.Brands
import com.shoppingcart.models.{Brand, BrandId, BrandName}
import doobie.Transactor

final class LiveBrands(xa: Transactor[IO]) extends Brands[IO] {
  private val brands: Brands[IO] =
    DoobieBrands.make[IO](xa)

  override def findAll: IO[List[Brand]] =
    brands.findAll

  override def create(name: BrandName): IO[BrandId] =
    brands.create(name)
}
