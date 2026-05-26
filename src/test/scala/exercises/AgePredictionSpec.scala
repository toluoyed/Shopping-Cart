package exercises

import eu.timepit.refined.refineV
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.{Greater, Less}
import munit.FunSuite

final class AgePredictionSpec extends FunSuite:

  private def age(value: Int): Age =
    Age(refineV[Greater[0] And Less[150]](value).fold(err => throw new IllegalArgumentException(err), identity))

  test("isAdult returns false for an age below 18") {
    assertEquals(isAdult(age(17)), false)
  }

  test("isAdult returns true at age 18") {
    assertEquals(isAdult(age(18)), true)
  }

  test("isAdult returns true for an older age") {
    assertEquals(isAdult(age(42)), true)
  }
