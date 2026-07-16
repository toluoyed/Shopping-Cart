package com.shoppingcart.repository

import cats.effect.Async
import cats.syntax.all.*
import com.shoppingcart.domains.Categories
import com.shoppingcart.effects.GenUUID
import com.shoppingcart.models.{Category, CategoryId, CategoryName}
import doobie.Transactor
import doobie.implicits.*

object DoobieCategories {
  import DoobieMappings.given

  def make[F[_]: Async: GenUUID](xa: Transactor[F]): Categories[F] =
    new Categories[F] {
      override def findAll: F[List[Category]] =
        CategorySql.findAll.to[List].transact(xa)

      override def create(name: CategoryName): F[CategoryId] =
        for {
          id <- GenUUID[F].make.map(CategoryId.apply)
          affectedRows <- CategorySql.insert(id, name).run.transact(xa)
          _ <-
            if affectedRows == 1 then Async[F].unit
            else
              Async[F].raiseError(
                new IllegalStateException(s"Expected one inserted category, but inserted $affectedRows")
              )
        } yield id
    }

  private object CategorySql {
    val findAll =
      sql"""
        SELECT id, name
        FROM categories
        ORDER BY name
      """.query[Category]

    def insert(id: CategoryId, name: CategoryName) =
      sql"""
        INSERT INTO categories (id, name)
        VALUES ($id, $name)
      """.update
  }
}
