package com.shoppingcart.repository

import cats.effect.Async
import cats.syntax.all.*
import com.shoppingcart.domains.Brands
import com.shoppingcart.effects.GenUUID
import com.shoppingcart.models.{Brand, BrandId, BrandName}
import doobie.Transactor
import doobie.implicits.*

object DoobieBrands {
  import DoobieMappings.given

  def make[F[_]: Async: GenUUID](xa: Transactor[F]): Brands[F] =
    new Brands[F] {
      override def findAll: F[List[Brand]] =
        BrandSql.findAll.to[List].transact(xa)

      override def create(name: BrandName): F[BrandId] =
        for {
          id <- GenUUID[F].make.map(BrandId.apply)
          affectedRows <- BrandSql.insert(id, name).run.transact(xa)
          _ <-
            if affectedRows == 1 then Async[F].unit
            else
              Async[F].raiseError(
                new IllegalStateException(s"Expected one inserted brand, but inserted $affectedRows")
              )
        } yield id
    }

  private object BrandSql {
    val findAll =
      sql"""
        SELECT id, name
        FROM brands
        ORDER BY name
      """.query[Brand]

    def insert(id: BrandId, name: BrandName) =
      sql"""
        INSERT INTO brands (id, name)
        VALUES ($id, $name)
      """.update
  }
}
