package exercises

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import munit.FunSuite

final class BankAccountSpec extends FunSuite:

  test("starts with a zero balance") {
    val result =
      for
        account <- BankAccount.make[IO]
        balance <- account.balance()
      yield balance

    assertEquals(result.unsafeRunSync(), 0)
  }

  test("deposit increases the balance") {
    val result =
      for
        account <- BankAccount.make[IO]
        _ <- account.deposit(50)
        balance <- account.balance()
      yield balance

    assertEquals(result.unsafeRunSync(), 50)
  }

  test("withdraw returns success and reduces the balance when funds are available") {
    val result =
      for
        account <- BankAccount.make[IO]
        _ <- account.deposit(100)
        withdrawal <- account.withdraw(40)
        balance <- account.balance()
      yield (withdrawal, balance)

    assertEquals(result.unsafeRunSync(), (Right(()), 60))
  }

  test("withdraw returns an error and leaves the balance unchanged when funds are insufficient") {
    val result =
      for
        account <- BankAccount.make[IO]
        _ <- account.deposit(30)
        withdrawal <- account.withdraw(40)
        balance <- account.balance()
      yield (withdrawal, balance)

    assertEquals(result.unsafeRunSync(), (Left("Insufficient Funds"), 30))
  }

  test("withdrawing the full balance succeeds and leaves zero") {
    val result =
      for
        account <- BankAccount.make[IO]
        _ <- account.deposit(75)
        withdrawal <- account.withdraw(75)
        balance <- account.balance()
      yield (withdrawal, balance)

    assertEquals(result.unsafeRunSync(), (Right(()), 0))
  }
