package com.shoppingcart.models

import squants.market.Money

case class ShoppingCart()

case class Quantity(value: Int)
case class Cart(items: Map[ItemId, Quantity])
case class CartItem(item: Item, quantity: Quantity)
case class CartTotal(items: List[CartItem], total: Money)
