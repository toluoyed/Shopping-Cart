package com.shoppingcart.routes

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.shoppingcart.domains.Items
import com.shoppingcart.models.JsonCodecs.given
import com.shoppingcart.models.*
import munit.FunSuite
import org.http4s.*
import org.http4s.implicits.uri
import squants.market.USD

import java.util.UUID

final class ItemRoutesSpec extends FunSuite {

  private val nike =
    Brand(BrandId(UUID.fromString("00000000-0000-0000-0000-000000000001")), BrandName("Nike"))

  private val adidas =
    Brand(BrandId(UUID.fromString("00000000-0000-0000-0000-000000000002")), BrandName("Adidas"))

  private val shoes =
    Category(CategoryId(UUID.fromString("00000000-0000-0000-0000-000000000003")), CategoryName("Shoes"))

  private val airMax =
    Item(
      ItemId(UUID.fromString("00000000-0000-0000-0000-000000000004")),
      ItemName("Air Max"),
      ItemDescription("Running shoes"),
      USD(120),
      nike,
      shoes
    )

  private val samba =
    Item(
      ItemId(UUID.fromString("00000000-0000-0000-0000-000000000005")),
      ItemName("Samba"),
      ItemDescription("Classic trainers"),
      USD(90),
      adidas,
      shoes
    )

  private val items = new Items[IO] {
    private val values = List(airMax, samba)

    override def findAll: IO[List[Item]] =
      IO.pure(values)

    override def findBy(brand: BrandName): IO[List[Item]] =
      IO.pure(values.filter(_.brand.name == brand))

    override def findById(itemId: ItemId): IO[Option[Item]] =
      IO.pure(values.find(_.uuid == itemId))

    override def create(item: CreateItem): IO[ItemId] =
      IO.raiseError(new NotImplementedError("not needed for ItemRoutes tests"))

    override def update(item: UpdateItem): IO[Unit] =
      IO.raiseError(new NotImplementedError("not needed for ItemRoutes tests"))
  }

  private val routes = ItemRoutes[IO](items).routes.orNotFound

  test("GET /items returns all items") {
    val response =
      routes.run(Request[IO](Method.GET, uri"/items")).unsafeRunSync()

    assertEquals(response.status, org.http4s.Status.Ok)

    val body = response.as[String].unsafeRunSync()
    assert(body.contains("Air Max"))
    assert(body.contains("Samba"))
  }

  test("GET /items?brand= filters items by brand") {
    val response =
      routes.run(Request[IO](Method.GET, uri"/items?brand=nike")).unsafeRunSync()

    assertEquals(response.status, org.http4s.Status.Ok)

    val body = response.as[String].unsafeRunSync()
    assert(body.contains("Air Max"))
    assert(!body.contains("Samba"))
  }
}
