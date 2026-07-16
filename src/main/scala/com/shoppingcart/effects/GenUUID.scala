package com.shoppingcart.effects

import cats.effect.Sync

import java.util.UUID

trait GenUUID[F[_]] {
  def make: F[UUID]
}

object GenUUID {
  def apply[F[_]](using genUUID: GenUUID[F]): GenUUID[F] =
    genUUID

  given derived[F[_]: Sync]: GenUUID[F] =
    new GenUUID[F] {
      override def make: F[UUID] =
        Sync[F].delay(UUID.randomUUID())
    }
}
