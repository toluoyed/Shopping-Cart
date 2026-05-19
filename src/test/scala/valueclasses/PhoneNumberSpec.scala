package valueclasses

import munit.FunSuite

final class PhoneNumberSpec extends FunSuite:

  test("creates a phone number when the input has exactly 10 digits") {
    val result = mkPhone("1234567890")

    assertEquals(result.map(_.value), Some("1234567890"))
  }

  test("rejects a phone number shorter than 10 digits") {
    val result = mkPhone("123456789")

    assertEquals(result, None)
  }

  test("rejects a phone number longer than 10 digits") {
    val result = mkPhone("12345678901")

    assertEquals(result, None)
  }

  test("rejects a phone number containing non-digit characters") {
    val result = mkPhone("12345abcde")

    assertEquals(result, None)
  }
