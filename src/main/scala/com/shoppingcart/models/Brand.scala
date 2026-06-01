package com.shoppingcart.models

import java.util.UUID

case class BrandId(value: UUID)
case class BrandName(value: String)


case class Brand(brandId: BrandId, brandName: BrandName)
