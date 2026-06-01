package com.shoppingcart.models

import java.util.UUID

case class UserName(value: String)
case class Password(value: String)
case class EncryptedPassword(value: String)
case class UserId(value: UUID)
case class JwtToken(value: String)

case class User(
               id: UserId,
               name: UserName
               )

case class UserWithPassword(
                             id: UserId,
                             name: UserName,
                             password: EncryptedPassword
                           )
