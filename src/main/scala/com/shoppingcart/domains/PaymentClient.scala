package com.shoppingcart.domains

import com.shoppingcart.models.{Payment, PaymentId}

trait PaymentClient[F[_]] {
  
  def process(payment: Payment): F[PaymentId]
}