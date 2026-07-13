package com.shoppingcart.repository

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

final case class DatabaseConfig(
    url: String,
    user: String,
    password: String
)

object DatabaseConfig {
  def load: IO[DatabaseConfig] =
    IO {
      DatabaseConfig(
        url = sys.env.getOrElse("SHOPPING_CART_DB_URL", "jdbc:postgresql://localhost:5432/shopping_cart"),
        user = sys.env.getOrElse("SHOPPING_CART_DB_USER", "postgres"),
        password = sys.env.getOrElse("SHOPPING_CART_DB_PASSWORD", "postgres")
      )
    }
}

object Database {
  def transactor(config: DatabaseConfig): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      config.url,
      config.user,
      config.password,
      ExecutionContext.global,
      None
    )
}
