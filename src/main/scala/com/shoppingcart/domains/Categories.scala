package com.shoppingcart.domains

import com.shoppingcart.models.{Category, CategoryId, CategoryName}

trait Categories[F[_]] {
  def findAll: F[List[Category]]
  def create(name: CategoryName): F[CategoryId]
}
