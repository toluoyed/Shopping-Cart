package com.shoppingcart.repository

import com.shoppingcart.models.{Brand, BrandId, BrandName}

trait BrandRepository[F[_]] {
  
  def findAllBrands():F[List[Brand]]
  
  def createBrand(name:BrandName): F[BrandId]

}
