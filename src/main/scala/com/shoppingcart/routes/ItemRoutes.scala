package com.shoppingcart.routes

import cats.Monad
import cats.syntax.either.*
import com.shoppingcart.domains.Items
import com.shoppingcart.models.BrandName
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.Validate
import eu.timepit.refined.refineV
import eu.timepit.refined.types.all.NonEmptyString
import org.http4s.{HttpRoutes, ParseFailure, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

case class BrandParam(value: NonEmptyString) {
  def toDomain: BrandName =
    BrandName(value.value.toLowerCase.capitalize)
}

implicit def refinedParamDecoder[T: QueryParamDecoder, P](
                                                           implicit ev: Validate[T, P]
                                                         ): QueryParamDecoder[T Refined P] =
  QueryParamDecoder[T].emap(
    refineV[P](_).leftMap(message => ParseFailure(message, message))
  )

implicit val brandParamDecoder: QueryParamDecoder[BrandParam] =
  QueryParamDecoder[NonEmptyString].map(BrandParam(_))

final case class ItemRoutes[F[_]: Monad](
                                          items: Items[F]
                                        ) extends Http4sDsl[F] {

  object BrandQueryParam extends OptionalQueryParamDecoderMatcher[BrandParam]("brand")

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root :? BrandQueryParam(brand) =>
        Ok(
          brand match
            case None    => items.findAll
            case Some(b) => items.findBy(b.toDomain)
        )
    }

  val routes: HttpRoutes[F] =
    Router("/items" -> httpRoutes)
}
