package com.shoppingcart.models

import cats.{Eq, Show}
import io.circe.{Decoder, Encoder}

import java.util.UUID

final case class UserName(value: String)
final case class Password(value: String)
final case class EncryptedPassword(value: String)
case class UserId(value: UUID)
final case class JwtToken(value: String)

object UserName {

  given Eq[UserName] = Eq.by(_.value)

  given Show[UserName] =
    Show.show(_.value)

  given Encoder[UserName] =
    Encoder[String].contramap(_.value)

  given Decoder[UserName] =
    Decoder[String].map(UserName.apply)
}

object Password {

  given Eq[Password] = Eq.by(_.value)

  given Show[Password] =
    Show.show(_ => "Password(<redacted>)")

  given Encoder[Password] =
    Encoder[String].contramap(_.value)

  given Decoder[Password] =
    Decoder[String].map(Password.apply)
}

object EncryptedPassword {

  given Eq[EncryptedPassword] = Eq.by(_.value)

  given Show[EncryptedPassword] =
    Show.show(_ => "Password(<redacted>)")

  given Encoder[EncryptedPassword] =
    Encoder[String].contramap(_.value)

  given Decoder[EncryptedPassword] =
    Decoder[String].map(EncryptedPassword.apply)
}

object JwtToken {

  given Eq[JwtToken] = Eq.by(_.value)

  given Show[JwtToken] =
    Show.show(_.value)

  given Encoder[JwtToken] =
    Encoder[String].contramap(_.value)

  given Decoder[JwtToken] =
    Decoder[String].map(JwtToken.apply)
}

case class User(
               id: UserId,
               name: UserName
               )

case class UserWithPassword(
                             id: UserId,
                             name: UserName,
                             password: EncryptedPassword
                           )
