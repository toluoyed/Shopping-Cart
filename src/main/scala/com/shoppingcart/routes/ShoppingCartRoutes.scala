package com.shoppingcart.routes

import cats.Monad
import com.shoppingcart.domains.ShoppingCart
import com.shoppingcart.models.UserId
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class ShoppingCartRoutes[F[_]: Monad](
                                                  shoppingCart: ShoppingCart[F]
                                                ) extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / UUIDVar(userId) =>
        Ok(shoppingCart.get(UserId(userId)))
    }

  val routes: HttpRoutes[F] =
    Router("/cart" -> httpRoutes)
}
