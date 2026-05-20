package exercises

case class PhoneNumber private (value: String) extends AnyVal

object PhoneNumber:
  def from(value: String): Option[PhoneNumber] = {
    if value.matches("\\d{10}") then
      Some(new PhoneNumber(value))
    else
      None
//    Option.when(value.matches("\\d{10}"))(new PhoneNumber(value))
  }

def mkPhone(value: String): Option[PhoneNumber] =
  PhoneNumber.from(value)
