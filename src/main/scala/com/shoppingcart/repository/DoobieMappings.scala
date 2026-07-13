package com.shoppingcart.repository

import com.shoppingcart.models.*
import doobie.Meta
import doobie.postgres.implicits.*

object DoobieMappings {
  given Meta[BrandId] =
    Meta[java.util.UUID].imap(BrandId.apply)(_.value)

  given Meta[BrandName] =
    Meta[String].imap(BrandName.apply)(_.value)

  given Meta[CategoryId] =
    Meta[java.util.UUID].imap(CategoryId.apply)(_.value)

  given Meta[CategoryName] =
    Meta[String].imap(CategoryName.apply)(_.value)

  given Meta[ItemId] =
    Meta[java.util.UUID].imap(ItemId.apply)(_.value)

  given Meta[ItemName] =
    Meta[String].imap(ItemName.apply)(_.value)

  given Meta[ItemDescription] =
    Meta[String].imap(ItemDescription.apply)(_.value)
}
