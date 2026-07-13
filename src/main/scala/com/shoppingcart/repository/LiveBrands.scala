package com.shoppingcart.repository

import cats.effect.IO
import com.shoppingcart.domains.Brands
import com.shoppingcart.models.{Brand, BrandId, BrandName}
import doobie.Transactor
import doobie.implicits.*

final class LiveBrands(xa: Transactor[IO]) extends Brands[IO] {
  override def findAll: IO[List[Brand]] =
    DoobieBrands.findAll.transact(xa)

  override def create(name: BrandName): IO[BrandId] =
    DoobieBrands.create(name).transact(xa)
}
