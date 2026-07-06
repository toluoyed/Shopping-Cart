package com.shoppingcart.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.shoppingcart.domains.{Auth, Brands, Categories, HealthCheck, Items, Orders, ShoppingCart}
import com.shoppingcart.models.User
import org.http4s.HttpRoutes
import org.http4s.server.AuthMiddleware

final case class HttpApi[F[_]: Concurrent](
    auth: Auth[F],
    brands: Brands[F],
    categories: Categories[F],
    healthCheck: HealthCheck[F],
    items: Items[F], 
    orders: Orders[F],
    shoppingCart: ShoppingCart[F]
) {
  private val authMiddleware: AuthMiddleware[F, User] =
    AuthMiddlewares.users(auth)

  private val openRoutes: HttpRoutes[F] =
    BrandRoutes[F](brands).routes <+> 
      CategoryRoutes[F](categories).routes <+>
      HealthCheckRoutes[F](healthCheck).routes <+>
      ItemRoutes[F](items).routes

  private val securedRoutes: HttpRoutes[F] =
    AuthRoutes[F](auth).routes(authMiddleware) <+>
      OrderRoutes[F](orders).routes(authMiddleware) <+>
      ShoppingCartRoutes[F](shoppingCart).routes(authMiddleware)

  val routes: HttpRoutes[F] =
    HttpRouteMiddlewares.common[F].apply(openRoutes <+> securedRoutes)
}
