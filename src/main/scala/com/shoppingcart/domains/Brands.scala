package com.shoppingcart.domains

import com.shoppingcart.models.{Brand, BrandId}

import java.util.UUID

trait Brands[F[_]] {
  def findAll:F[List[Brand]]
  def create(brand: BrandId) : F[BrandId]
}
