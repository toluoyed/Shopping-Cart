package com.shoppingcart.domains

import com.shoppingcart.models.{EncryptedPassword, UserId, UserName, UserWithPassword}

trait Users[F[_]] {
  
  def find(
            username: UserName
          ): F[Option[UserWithPassword]]
  def create(
              username: UserName,
              password: EncryptedPassword
            ): F[UserId]
}