package exercises

trait BankAccount[F[_]] {
  def deposit(amount: Int): F[Unit]
  def withdraw(amount: Int): F[Either[String,Unit]]
  def balance(): F[Int]
}