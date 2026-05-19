package valueclasses

case class PhoneNumber private (value: String) extends AnyVal

object PhoneNumber:
  def from(value: String): Option[PhoneNumber] =
    Option.when(value.matches("\\d{10}"))(new PhoneNumber(value))

def mkPhone(value: String): Option[PhoneNumber] =
  PhoneNumber.from(value)
