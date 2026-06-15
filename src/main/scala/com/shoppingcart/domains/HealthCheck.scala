package com.shoppingcart.domains

import com.shoppingcart.models.AppStatus

trait HealthCheck[F[_]] {
  def status: F[AppStatus]
}
