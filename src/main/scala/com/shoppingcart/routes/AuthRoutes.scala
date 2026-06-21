package com.shoppingcart.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.shoppingcart.domains.Auth
import com.shoppingcart.models.JsonCodecs.given
import com.shoppingcart.models.{JwtToken, Password, User, UserName}
import io.circe.Codec
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.dsl.Http4sDsl

final case class AuthRoutes[F[_]: Concurrent](
    auth: Auth[F]
) extends Http4sDsl[F] {

  private val prefixPath = "/auth"

  private val openRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "login" =>
        req.as[AuthRoutes.AuthRequest].flatMap { user =>
          auth
            .login(user.username, user.password)
            .flatMap(Ok(_))
            .handleErrorWith(_ => Forbidden())
        }

      case req @ POST -> Root / "users" =>
        req.as[AuthRoutes.AuthRequest].flatMap { user =>
          auth
            .newUser(user.username, user.password)
            .flatMap(Created(_))
            .handleErrorWith(e => Conflict(e.getMessage))
        }
    }

  private val authedRoutes: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] {
      case ar @ POST -> Root / "logout" as user =>
        AuthRoutes.bearerToken(ar.req)
          .traverse_(token => auth.logout(token, user.name)) *> NoContent()
    }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] =
    Router(
      prefixPath -> (openRoutes <+> authMiddleware(authedRoutes))
    )
}

object AuthRoutes {
  final case class AuthRequest(username: UserName, password: Password)

  object AuthRequest {
    given Codec.AsObject[AuthRequest] = Codec.AsObject.derived[AuthRequest]
  }

  private def bearerToken[F[_]](request: org.http4s.Request[F]): Option[JwtToken] =
    request.headers.get[org.http4s.headers.Authorization].collect {
      case org.http4s.headers.Authorization(
            org.http4s.Credentials.Token(org.http4s.AuthScheme.Bearer, token)
          ) =>
        JwtToken(token)
    }
}
