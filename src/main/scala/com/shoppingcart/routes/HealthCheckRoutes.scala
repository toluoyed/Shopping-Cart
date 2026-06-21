package com.shoppingcart.routes

import cats.Monad
import com.shoppingcart.domains.HealthCheck
import com.shoppingcart.models.JsonCodecs.given
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class HealthCheckRoutes[F[_]: Monad](healthCheck: HealthCheck[F]) extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F]{
      case GET -> Root => Ok(healthCheck.status)
    }

  val routes: HttpRoutes[F] = Router("/healthCheck" -> httpRoutes)

}
