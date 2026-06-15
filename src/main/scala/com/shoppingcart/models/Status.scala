package com.shoppingcart.models

case class RedisStatus(value: Status)
case class PostgresStatus(value: Status)

case class AppStatus(
                      redis: RedisStatus,
                      postgres: PostgresStatus
                    )

sealed trait Status

object Status {
  case object Okay extends Status
  case object Unreachable extends Status

  def fromBoolean(value: Boolean): Status =
    if value then Okay else Unreachable

  def toBoolean(status: Status): Boolean =
    status match
      case Okay        => true
      case Unreachable => false
}
