package com.shoppingcart.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.shoppingcart.domains.ShoppingCart
import com.shoppingcart.models.JsonCodecs.given
import com.shoppingcart.models.{Cart, ItemId, User}
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}

import java.util.UUID

final case class ShoppingCartRoutes[F[_]: Concurrent](
                                                  shoppingCart: ShoppingCart[F]
                                                ) extends Http4sDsl[F] {

  private val httpRoutes: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] {
      case GET -> Root as user =>
        Ok(shoppingCart.get(user.id))

      case ar @ POST -> Root as user =>
        ar.req.as[Cart].flatMap { cart =>
          cart.items.toList.traverse_ { case (itemId, quantity) =>
            shoppingCart.add(user.id, itemId, quantity)
          } *> Created()
        }

      case ar @ PUT -> Root as user =>
        ar.req.as[Cart].flatMap { cart =>
          shoppingCart.update(user.id, cart) *> Ok()
        }

      case DELETE -> Root / ItemIdVar(itemId) as user =>
        shoppingCart.removeItem(user.id, itemId) *> NoContent()
    }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] =
    Router("/cart" -> authMiddleware(httpRoutes))
}

object ItemIdVar {
  def unapply(value: String): Option[ItemId] =
    Either.catchNonFatal(ItemId(UUID.fromString(value))).toOption
}
