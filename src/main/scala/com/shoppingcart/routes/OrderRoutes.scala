package com.shoppingcart.routes

import cats.Monad
import cats.syntax.all.*
import com.shoppingcart.domains.Orders
import com.shoppingcart.models.JsonCodecs.given
import com.shoppingcart.models.{OrderId, User}
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}

import java.util.UUID

final case class OrderRoutes[F[_]: Monad](
                                           orders: Orders[F]
                                         ) extends Http4sDsl[F] {

  private val httpRoutes: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] {
      case GET -> Root as user =>
        Ok(orders.findBy(user.id))

      case GET -> Root / OrderIdVar(orderId) as user =>
        orders.get(user.id, orderId).flatMap {
          case Some(order) => Ok(order)
          case None        => NotFound()
        }
    }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] =
    Router("/orders" -> authMiddleware(httpRoutes))
}

object OrderIdVar {
  def unapply(value: String): Option[OrderId] =
    Either.catchNonFatal(OrderId(UUID.fromString(value))).toOption
}
