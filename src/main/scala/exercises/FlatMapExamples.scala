package exercises

import cats.effect.{IO, IOApp}

import scala.util.Try

/** A tour of `flatMap` across three different "contexts":
 *
 *   - List — context of "many possible values"
 *   - Option — context of "a value that might be missing"
 *   - IO — context of "a description of an effect to run"
 *
 * The shape of `flatMap` is always the same:
 *
 * F[A].flatMap(a => F[B]): F[B]
 *
 * i.e. "given something in the context F, and a function that takes its value and produces a *new* F, give me back the
 * combined F".
 *
 * `map` lets you transform the value *inside* the context. `flatMap` lets the next step itself decide what the context
 * looks like — so it can branch (List), short-circuit (Option), or schedule more effects (IO).
 *
 * A `for`-comprehension is just sugar for nested `flatMap` calls with a final `map` at the end (the `yield`). Anything
 * you can write with one, you can write with the other — pick whichever reads better.
 */
object FlatMapExamples extends IOApp.Simple {

  // ─────────────────────────── 1. List ───────────────────────────
  //
  // For List, `flatMap` is "for each element, produce a list, then
  // concatenate them all". This is how you get cartesian products /
  // nested loops without actually writing nested loops.

  val pairs: List[(Int, Char)] =
    List(1, 2, 3).flatMap(n => List('a', 'b').map(c => (n, c)))
  // == List((1,a), (1,b), (2,a), (2,b), (3,a), (3,b))

  // Same thing, as a for-comprehension. Notice how it reads like
  // a nested loop — that's exactly what it desugars to.
  val pairsForComp: List[(Int, String, Char)] =
    for {
      n <- List(1, 2, 3)
      name <- List("paul", "tolu")
      c <- List('a', 'b')
    } yield (n, name, c)

  // A flatMap that returns an *empty* list effectively filters that
  // branch out. Here we keep only the even numbers, doubled.
  val evensDoubled: List[Int] =
    List(1, 2, 3, 4, 5).flatMap(n => if (n % 2 == 0) List(n * 2) else List.empty)
  // == List(4, 8)

  extension [A](xs: List[A])
    def filterUsingFlatMap(p: A => Boolean) =
      xs.flatMap(x => if p(x) then List(x) else Nil)

  val ys = List
    .range(1, 10)
    .filterUsingFlatMap(x => x % 3 == 0)
    .map(x => x * x)
    .sum

  println(ys)

  // ────────────────────────── 2. Option ──────────────────────────
  //
  // For Option, `flatMap` is "if I have a value, run the next step;
  // if I don't, short-circuit to None". This is how you chain
  // computations that each might fail, without a pyramid of
  // `if (x.isDefined) ...` checks.

  def parseInt(s: String): Option[Int] =
    s.toIntOption

  // this is basically like safeDivide from last week
  def reciprocal(n: Int): Option[Double] =
    Option.when(n != 0)(1.0 / n)

  // "Parse, then take the reciprocal." If either step fails, we get None.
  def parseAndReciprocal(s: String): Option[Double] =
    parseInt(s).flatMap(reciprocal)

  // Same logic, but combining *two* inputs. The for-comp shines here —
  // it looks like straight-line code even though each `<-` is a
  // potential short-circuit.
  def divide(numStr: String, denomStr: String): Option[Double] =
    for {
      num <- parseInt(numStr)
      denom <- parseInt(denomStr)
      result <- reciprocal(denom).map(_ * num) // 1/denom * num = num/denom
    } yield result

  // ──────────────────────────── 3. IO ────────────────────────────
  //
  // For IO, `flatMap` is "run this effect, take its result, and use
  // it to decide what effect to run next". This is how you sequence
  // effects: you can't write `val x = readLine(); println(x)` in pure
  // FP — but you can write `readLine.flatMap(println)`, which is the
  // *description* of doing those two things in order.
  //
  // The CounterExample next door uses exactly this pattern.

  val askAndGreet: IO[Unit] =
    for {
      _ <- IO.println("What's your name?")
      name <- IO.pure("Tolu") // pretend this is IO.readLine
      _ <- IO.println(s"Hello, $name!")
    } yield ()

  // Same thing without sugar — it's just nested flatMaps with a final map.
  val askAndGreetDesugared: IO[Unit] =
    IO.println("What's your name?").flatMap { _ =>
      IO.pure("Tolu").flatMap { name =>
        IO.println(s"Hello, $name!")
      }
    }

  // ────────────────────────── run them all ──────────────────────────

  override def run: IO[Unit] =
    for {
      _ <- IO.println("── List ──")
      _ <- IO.println(s"pairs:        $pairs")
      _ <- IO.println(s"pairsForComp: $pairsForComp")
      _ <- IO.println(s"evensDoubled: $evensDoubled")

      _ <- IO.println("\n── Option ──")
      _ <- IO.println(s"""parseAndReciprocal("4"):      ${parseAndReciprocal("4")}""")
      _ <- IO.println(s"""parseAndReciprocal("0"):      ${parseAndReciprocal("0")}""")
      _ <- IO.println(s"""parseAndReciprocal("nope"):   ${parseAndReciprocal("nope")}""")
      _ <- IO.println(s"""divide("10", "4"):            ${divide("10", "4")}""")
      _ <- IO.println(s"""divide("10", "0"):            ${divide("10", "0")}""")
      _ <- IO.println(s"""divide("ten", "4"):           ${divide("ten", "4")}""")

      _ <- IO.println("\n── IO ──")
      _ <- askAndGreet
    } yield ()
}