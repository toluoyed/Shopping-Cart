package com.shoppingcart.domains

import com.shoppingcart.models.{BrandName, CreateItem, Item, ItemId}

trait Items[F[_]] {

  def findAll: F[List[Item]]
  def findBy(brand: BrandName): F[List[Item]]
  def findByItemId(itemId: ItemId): F[List[Item]]
  def create(item: CreateItem) : F[Item]
  def update(itemId: ItemId, item: Item): F[Item]

}