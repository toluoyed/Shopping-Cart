package com.shoppingcart.repository

import com.shoppingcart.models.{Brand, BrandId, BrandName}
import org.typelevel.doobie.ConnectionIO
import org.typelevel.doobie.implicits.toSqlInterpolator

object DoobieBrandRepository extends BrandRepository[ConnectionIO]{

  override def findAllBrands(): ConnectionIO[List[Brand]] = 
    sql"""
          select 
          brand.Id
          brand.name
          from brand
      """.query[Brand]

  override def createBrand(name: BrandName): ConnectionIO[BrandId] = ???
  
}
