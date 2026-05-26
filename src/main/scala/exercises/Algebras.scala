package exercises

import cats.Apply
import cats.Functor
import cats.Monad
import cats.effect.kernel.Ref
import cats.effect.std.Console
import cats.syntax.apply.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import dev.profunktor.redis4cats.RedisCommands

final case class Item(name: String)

trait Counter[F[_]] {
  def incr: F[Unit]
  def get: F[Int]
}

trait Items[F[_]] {
  def getAll: F[List[Item]]
  def add(item: Item): F[Unit]
}

object Counter {
  final case class RedisKey(value: String)

  // This interpreter gives meaning to the Counter algebra using Redis.
  // `make` is a smart constructor: it builds a Counter[F] from RedisCommands.
  def make[F[_]: Functor](
    key: RedisKey,
    cmd: RedisCommands[F, String, Int]
  ): Counter[F] =
    new Counter[F] {
      def incr: F[Unit] =
        cmd.incr(key.value).void

      def get: F[Int] =
        cmd.get(key.value).map(_.getOrElse(0))
    }

  // This interpreter gives meaning to the same algebra using in-memory state.
  // It is useful for learning and for tests because it has no external dependency.
  def inMemory[F[_]: Functor: Ref.Make]: F[Counter[F]] =
    Ref.of[F, Int](0).map { ref =>
      new Counter[F] {
        def incr: F[Unit] =
          ref.update(_ + 1)

        def get: F[Int] =
          ref.get
      }
    }
}

object Items {
  // This interpreter stores items in a Ref so the program can be run and tested
  // without a database. We prepend on add because it keeps the update function simple.
  def inMemory[F[_]: Functor: Ref.Make]: F[Items[F]] =
    Ref.of[F, List[Item]](Nil).map { ref =>
      new Items[F] {
        def getAll: F[List[Item]] =
          ref.get

        def add(item: Item): F[Unit] =
          ref.update(existingItems => item :: existingItems)
      }
    }
}

// This is the program. It does not know whether the interpreters use Redis,
// a database, or in-memory state. It only depends on the algebras.
class ItemsCounter[F[_]: Apply](
  counter: Counter[F],
  items: Items[F]
) {
  def addItem(item: Item): F[Unit] =
    items.add(item) *> counter.incr
}

def program[F[_]: Console: Monad]: F[Unit] =
  for {
    _ <- Console[F].println("Enter your name: ")
    n <- Console[F].readLine
    _ <- Console[F].println(s"Hello $n!")
  } yield ()
