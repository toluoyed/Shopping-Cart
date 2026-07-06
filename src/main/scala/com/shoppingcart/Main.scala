package com.shoppingcart

import cats.data.NonEmptyList
import cats.effect.{IO, IOApp, Ref}
import cats.syntax.all.*
import com.comcast.ip4s.*
import com.shoppingcart.domains.{Auth, Brands, Categories, HealthCheck, Items, Orders, ShoppingCart as ShoppingCartService}
import com.shoppingcart.models.*
import com.shoppingcart.routes.HttpApi
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import squants.market.{Money, USD}

import java.util.UUID

object Main extends IOApp.Simple {

  override def run: IO[Unit] =
    InMemoryServices.create.flatMap { services =>
      val httpApp =
        HttpApi[IO](
          services.auth,
          services.brands,
          services.categories,
          services.healthCheck,
          services.items,
          services.orders,
          services.shoppingCart
        ).routes.orNotFound

      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .useForever
    }
}

private final case class InMemoryServices(
    auth: Auth[IO],
    brands: Brands[IO],
    categories: Categories[IO],
    healthCheck: HealthCheck[IO],
    items: Items[IO],
    orders: Orders[IO],
    shoppingCart: ShoppingCartService[IO]
)

private object InMemoryServices {
  private def id(value: String): UUID =
    UUID.fromString(value)

  private val nike =
    Brand(BrandId(id("00000000-0000-0000-0000-000000000001")), BrandName("Nike"))

  private val adidas =
    Brand(BrandId(id("00000000-0000-0000-0000-000000000002")), BrandName("Adidas"))

  private val shoes =
    Category(CategoryId(id("00000000-0000-0000-0000-000000000003")), CategoryName("Shoes"))

  private val accessories =
    Category(CategoryId(id("00000000-0000-0000-0000-000000000006")), CategoryName("Accessories"))

  private val seedItems =
    List(
      Item(
        ItemId(id("00000000-0000-0000-0000-000000000004")),
        ItemName("Air Max"),
        ItemDescription("Running shoes"),
        USD(120),
        nike,
        shoes
      ),
      Item(
        ItemId(id("00000000-0000-0000-0000-000000000005")),
        ItemName("Samba"),
        ItemDescription("Classic trainers"),
        USD(90),
        adidas,
        shoes
      ),
      Item(
        ItemId(id("00000000-0000-0000-0000-000000000007")),
        ItemName("Cap"),
        ItemDescription("Everyday cotton cap"),
        USD(25),
        nike,
        accessories
      )
    )

  def create: IO[InMemoryServices] =
    (
      Ref.of[IO, Map[UserName, (User, Password)]](Map.empty),
      Ref.of[IO, Map[JwtToken, User]](Map.empty),
      Ref.of[IO, List[Brand]](List(nike, adidas)),
      Ref.of[IO, List[Category]](List(shoes, accessories)),
      Ref.of[IO, List[Item]](seedItems),
      Ref.of[IO, Map[UserId, Cart]](Map.empty),
      Ref.of[IO, Map[UserId, List[Order]]](Map.empty)
    ).mapN { case (users, tokens, brandStore, categoryStore, itemStore, carts, orderStore) =>
      val itemsService = items(itemStore, brandStore, categoryStore)

      InMemoryServices(
        auth(users, tokens),
        brands(brandStore),
        categories(categoryStore),
        healthCheck,
        itemsService,
        orders(orderStore),
        shoppingCart(carts, itemsService)
      )
    }

  private def auth(
      users: Ref[IO, Map[UserName, (User, Password)]],
      tokens: Ref[IO, Map[JwtToken, User]]
  ): Auth[IO] =
    new Auth[IO] {
      override def findUser(token: JwtToken): IO[Option[User]] =
        tokens.get.map(_.get(token))

      override def newUser(username: UserName, password: Password): IO[JwtToken] =
        for {
          user <- users.modify { current =>
            if current.contains(username) then
              current -> IO.raiseError[User](new IllegalArgumentException("User already exists"))
            else {
              val user = User(UserId(UUID.randomUUID()), username)
              current.updated(username, user -> password) -> IO.pure(user)
            }
          }.flatten
          token <- issueToken(user)
        } yield token

      override def login(username: UserName, password: Password): IO[JwtToken] =
        users.get.flatMap { current =>
          current.get(username) match {
            case Some((user, savedPassword)) if savedPassword == password => issueToken(user)
            case _ => IO.raiseError(new IllegalArgumentException("Invalid username or password"))
          }
        }

      override def logout(token: JwtToken, username: UserName): IO[Unit] =
        tokens.update(_.removed(token)).void

      private def issueToken(user: User): IO[JwtToken] =
        IO(JwtToken(UUID.randomUUID().toString)).flatTap(token => tokens.update(_.updated(token, user)))
    }

  private def brands(store: Ref[IO, List[Brand]]): Brands[IO] =
    new Brands[IO] {
      override def findAll: IO[List[Brand]] =
        store.get

      override def create(name: BrandName): IO[BrandId] =
        IO(BrandId(UUID.randomUUID())).flatTap(id => store.update(Brand(id, name) :: _))
    }

  private def categories(store: Ref[IO, List[Category]]): Categories[IO] =
    new Categories[IO] {
      override def findAll: IO[List[Category]] =
        store.get

      override def create(name: CategoryName): IO[CategoryId] =
        IO(CategoryId(UUID.randomUUID())).flatTap(id => store.update(Category(id, name) :: _))
    }

  private val healthCheck: HealthCheck[IO] =
    new HealthCheck[IO] {
      override def status: IO[AppStatus] =
        IO.pure(AppStatus(RedisStatus(Status.Okay), PostgresStatus(Status.Okay)))
    }

  private def items(
      store: Ref[IO, List[Item]],
      brands: Ref[IO, List[Brand]],
      categories: Ref[IO, List[Category]]
  ): Items[IO] =
    new Items[IO] {
      override def findAll: IO[List[Item]] =
        store.get.map(_.sortBy(_.name.value))

      override def findBy(brand: BrandName): IO[List[Item]] =
        findAll.map(_.filter(_.brand.name == brand))

      override def findById(itemId: ItemId): IO[Option[Item]] =
        store.get.map(_.find(_.uuid == itemId))

      override def create(item: CreateItem): IO[ItemId] =
        (brands.get, categories.get).flatMapN { (allBrands, allCategories) =>
          (
            allBrands.find(_.uuid == item.brandId),
            allCategories.find(_.uuid == item.categoryId)
          ) match {
            case (Some(brand), Some(category)) =>
              IO(ItemId(UUID.randomUUID())).flatTap { id =>
                val newItem =
                  Item(id, item.name, item.description, item.price, brand, category)
                store.update(newItem :: _)
              }
            case _ =>
              IO.raiseError(new IllegalArgumentException("Unknown brand or category"))
          }
        }

      override def update(item: UpdateItem): IO[Unit] =
        store.update(_.map(existing => if existing.uuid == item.id then existing.copy(price = item.price) else existing))
    }

  private def orders(store: Ref[IO, Map[UserId, List[Order]]]): Orders[IO] =
    new Orders[IO] {
      override def get(userId: UserId, orderId: OrderId): IO[Option[Order]] =
        store.get.map(_.getOrElse(userId, Nil).find(_.id == orderId))

      override def findBy(userId: UserId): IO[List[Order]] =
        store.get.map(_.getOrElse(userId, Nil))

      override def create(
          userId: UserId,
          paymentId: PaymentId,
          items: NonEmptyList[CartItem],
          total: Money
      ): IO[OrderId] =
        IO(OrderId(UUID.randomUUID())).flatTap { id =>
          val order = Order(id, paymentId, items.toList.map(item => item.item.uuid -> item.quantity).toMap, total)
          store.update(current => current.updated(userId, order :: current.getOrElse(userId, Nil)))
        }
    }

  private def shoppingCart(
      carts: Ref[IO, Map[UserId, Cart]],
      items: Items[IO]
  ): ShoppingCartService[IO] =
    new ShoppingCartService[IO] {
      override def add(userId: UserId, itemId: ItemId, quantity: Quantity): IO[Unit] =
        carts.update { current =>
          val cart = current.getOrElse(userId, Cart(Map.empty))
          current.updated(userId, cart.copy(items = cart.items.updated(itemId, quantity)))
        }

      override def get(userId: UserId): IO[CartTotal] =
        carts.get.map(_.getOrElse(userId, Cart(Map.empty))).flatMap { cart =>
          cart.items.toList.traverse { case (itemId, quantity) =>
            items.findById(itemId).map(_.map(CartItem(_, quantity)))
          }.map(_.flatten).map { cartItems =>
            CartTotal(cartItems, cartItems.foldLeft(USD(0): Money)((total, item) => total + item.item.price * item.quantity.value))
          }
        }

      override def delete(userId: UserId): IO[Unit] =
        carts.update(_.removed(userId)).void

      override def removeItem(userId: UserId, itemId: ItemId): IO[Unit] =
        carts.update { current =>
          current.get(userId).fold(current) { cart =>
            current.updated(userId, cart.copy(items = cart.items.removed(itemId)))
          }
        }

      override def update(userId: UserId, cart: Cart): IO[Unit] =
        carts.update(_.updated(userId, cart)).void
    }
}
