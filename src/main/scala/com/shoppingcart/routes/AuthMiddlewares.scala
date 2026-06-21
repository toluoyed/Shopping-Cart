package com.shoppingcart.routes

import cats.Monad
import cats.data.Kleisli
import cats.syntax.all.*
import com.shoppingcart.domains.Auth
import com.shoppingcart.models.{JwtToken, User}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthScheme, AuthedRoutes, Credentials, HttpRoutes, Request}
import org.http4s.server.middleware.AutoSlash

object AuthMiddlewares {

  def users[F[_]: Monad](auth: Auth[F]): AuthMiddleware[F, User] = {
    val dsl = Http4sDsl[F]
    import dsl.*

    val authenticate: Kleisli[F, Request[F], Either[String, User]] =
      Kleisli { request =>
        bearerToken(request).fold("Missing bearer token".asLeft[User].pure[F]) { token =>
          auth.findUser(token).map(_.toRight("Invalid bearer token"))
        }
      }

    val onFailure: AuthedRoutes[String, F] =
      AuthedRoutes.of[String, F] { case _ as message =>
        Forbidden(message)
      }

    AuthMiddleware(authenticate, onFailure)
  }

  private def bearerToken[F[_]](request: Request[F]): Option[JwtToken] =
    request.headers.get[Authorization].collect {
      case Authorization(Credentials.Token(AuthScheme.Bearer, token)) => JwtToken(token)
    }
}

object HttpRouteMiddlewares {
  def common[F[_]: Monad]: HttpRoutes[F] => HttpRoutes[F] =
    routes => AutoSlash(routes)
}
