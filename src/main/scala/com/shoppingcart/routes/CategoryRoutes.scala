package com.shoppingcart.routes

import cats.Monad
import com.shoppingcart.domains.Categories
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class CategoryRoutes[F[_]: Monad](
                                              categories: Categories[F]
                                            ) extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(categories.findAll)
    }

  val routes: HttpRoutes[F] =
    Router("/categories" -> httpRoutes)
}
