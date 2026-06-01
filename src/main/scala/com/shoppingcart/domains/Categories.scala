package com.shoppingcart.domains

import com.shoppingcart.models.{Category, CategoryId}

trait Categories[F[_]] {
  def getAll: F[List[Category]]
  def create(category: CategoryId): F[Unit]
}
