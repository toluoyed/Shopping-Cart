package exercises

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import munit.FunSuite

final class ItemsCounterSpec extends FunSuite:

  test("addItem stores the item and increments the counter") {
    val result =
      for
        counter <- Counter.inMemory[IO]
        items <- Items.inMemory[IO]
        program = new ItemsCounter[IO](counter, items)
        book = Item("Book")
        _ <- program.addItem(book)
        count <- counter.get
        savedItems <- items.getAll
      yield (count, savedItems)

    assertEquals(result.unsafeRunSync(), (1, List(Item("Book"))))
  }

  test("addItem can be called multiple times and increments once per item") {
    val result =
      for
        counter <- Counter.inMemory[IO]
        items <- Items.inMemory[IO]
        program = new ItemsCounter[IO](counter, items)
        _ <- program.addItem(Item("Book"))
        _ <- program.addItem(Item("Pen"))
        count <- counter.get
        savedItems <- items.getAll
      yield (count, savedItems)

    assertEquals(result.unsafeRunSync(), (2, List(Item("Pen"), Item("Book"))))
  }
