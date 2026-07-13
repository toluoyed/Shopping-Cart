package com.shoppingcart.repository

import com.shoppingcart.domains.Brands
import com.shoppingcart.models.{Brand, BrandId, BrandName}
import cats.syntax.functor.*
import doobie.ConnectionIO
import doobie.implicits.*

import java.util.UUID

object DoobieBrands extends Brands[ConnectionIO] {
  import DoobieMappings.given

  override def findAll: ConnectionIO[List[Brand]] =
    sql"""
      SELECT id, name
      FROM brands
      ORDER BY name
    """.query[Brand].to[List]

  override def create(name: BrandName): ConnectionIO[BrandId] = {
    val id = BrandId(UUID.randomUUID())

    sql"""
      INSERT INTO brands (id, name)
      VALUES ($id, $name)
    """.update.run.as(id)
  }
}
