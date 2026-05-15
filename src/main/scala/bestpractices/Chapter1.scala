package bestpractices

import cats.Functor
import cats.effect.kernel.Ref
import cats.syntax.functor.*
import cats.effect.{IO, IOApp}


trait Counter[F[_]] {
  def incr: F[Unit]

  def get: F[Int]
}

object Counter {
  def make[F[_] : Functor : Ref.Make]: F[Counter[F]] =
    Ref.of[F, Int](0).map { ref =>
      new Counter[F] {
        def incr: F[Unit] = ref.update(_ + 1)

        def get: F[Int] = ref.get
      }
    }
}

object Example extends IOApp.Simple {

  override def run: IO[Unit] =
    Counter.make[IO].flatMap(program)

  def program(c: Counter[IO]): IO[Unit] =
    for {
      _ <- c.get.flatMap[Unit](IO.println)
      _ <- c.incr
      _ <- c.get.flatMap(IO.println)
      _ <- c.incr.replicateA(5).void
      _ <- c.get.flatMap(IO.println)
    } yield ()


}

object AnotherExample extends App {

  def safeDivide(a:Int, b:Int): Option[Int] =
    if b == 0 then {
      None
    }
    else
      Some(a/b)


//  println(Some(7).flatMap(a => safeDivide(a,0)).flatMap(safeDivide(_,4)))
  println(
    for{
      x <- Some(7)
      y <- safeDivide(x,2)
      z <- safeDivide(y,0)
    } yield z
  )
//  Some(7).flatMap(safeDivide(_,0))
}

