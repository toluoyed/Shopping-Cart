package exercises

import cats.effect.kernel.Ref
import cats.effect.IO


class SharedState {
  val run: IO[Unit] =
    Ref.of[IO, Int](9).flatMap { counter =>
      for
        _ <- counter.update(_ + 1)
        _ <- counter.update(_ + 1)
        value <- counter.get
        _ <- IO.println(value)
      yield ()
    }
}
