package com.shoppingcart.routes

import cats.Monad
import com.shoppingcart.domains.Brands
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class BrandRoutes[F[_]: Monad](
                                           brands: Brands[F]
                                         ) extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(brands.findAll)
    }

  val routes: HttpRoutes[F] =
    Router("/brands" -> httpRoutes)
}