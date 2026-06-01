package com.shoppingcart.models

import java.util.UUID

case class CategoryId(value: UUID)
case class CategoryName(value: String)

case class Category(categoryId: CategoryId, categoryName: CategoryName)
