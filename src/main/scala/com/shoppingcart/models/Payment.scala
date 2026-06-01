package com.shoppingcart.models

import java.util.UUID
import squants.market.Money

case class PaymentId(uuid: UUID)
case class Card(value: String)

case class Payment(
                  id: UserId,
                  total: Money,
                  card: Card
                  
                  )
