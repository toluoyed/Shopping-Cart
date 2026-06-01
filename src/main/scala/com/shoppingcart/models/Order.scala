package com.shoppingcart.models

import java.util.UUID
import squants.market.Money

case class OrderId(uuid: UUID)

case class Order(
                  id: OrderId,
                  pid: PaymentId,
                  items: Map[ItemId, Quantity],
                  total: Money
                )
