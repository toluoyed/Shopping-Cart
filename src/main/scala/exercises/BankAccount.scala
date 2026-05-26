package exercises

import cats.Monad
import cats.effect.kernel.Ref
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*

 
trait BankAccount[F[_]] {
  def deposit(amount: Int): F[Unit]
  def withdraw(amount: Int): F[Either[String,Unit]]
  def balance(): F[Int]
}

object BankAccount {
  def make[F[_] : Monad : Ref.Make]: F[BankAccount[F]] = {
    Ref.of[F, Int](0).map { ref =>
      new BankAccount[F] {
        def deposit(amount: Int): F[Unit] = ref.update(_ + amount)
        def withdraw(amount: Int): F[Either[String,Unit]] =
          ref.get.flatMap { currentBalance =>
            if currentBalance < amount then
              Left("Insufficient Funds").pure[F]
            else
              ref.update(_ - amount).map(_ => Right(()))
          }
          // A safer version uses `Ref.modify`:
          //
          // ref.modify { currentBalance =>
          //   if currentBalance < amount then
          //     // Leave the state unchanged and return the failure result.
          //     (currentBalance, Left("Insufficient Funds"))
          //   else
          //     // Store the new balance and return success.
          //     (currentBalance - amount, Right(()))
          // }
          //
          // `modify` is better for a real bank account because the check and
          // update happen atomically. With the `flatMap` version above, another
          // fiber could change the balance between `get` and `update`.

        def balance(): F[Int] = ref.get
      }
    }
  }
}
