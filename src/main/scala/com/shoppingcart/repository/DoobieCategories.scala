package com.shoppingcart.repository

import com.shoppingcart.domains.Categories
import com.shoppingcart.models.{Category, CategoryId, CategoryName}
import cats.syntax.functor.*
import doobie.ConnectionIO
import doobie.implicits.*

import java.util.UUID

object DoobieCategories extends Categories[ConnectionIO] {
  import DoobieMappings.given

  override def findAll: ConnectionIO[List[Category]] =
    sql"""
      SELECT id, name
      FROM categories
      ORDER BY name
    """.query[Category].to[List]

  override def create(name: CategoryName): ConnectionIO[CategoryId] = {
    val id = CategoryId(UUID.randomUUID())

    sql"""
      INSERT INTO categories (id, name)
      VALUES ($id, $name)
    """.update.run.as(id)
  }
}
