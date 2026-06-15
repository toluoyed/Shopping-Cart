package com.shoppingcart.domains

import com.shoppingcart.models.{Brand, BrandId, BrandName}

trait Brands[F[_]] {
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[BrandId]
}
