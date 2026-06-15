package com.shoppingcart.routes

import cats.Monad
import com.shoppingcart.domains.Orders
import com.shoppingcart.models.{OrderId, UserId}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class OrderRoutes[F[_]: Monad](
                                           orders: Orders[F]
                                         ) extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / UUIDVar(userId) =>
        Ok(orders.findBy(UserId(userId)))

      case GET -> Root / UUIDVar(userId) / UUIDVar(orderId) =>
        Ok(orders.get(UserId(userId), OrderId(orderId)))
    }

  val routes: HttpRoutes[F] =
    Router("/orders" -> httpRoutes)
}
