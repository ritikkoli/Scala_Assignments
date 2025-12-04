case class Address(city: String, pincode: Int)
case class Person2(name: String, address: Address)

object unapplyChain extends App {
  val p = Person2("Ravi", Address("Chennai", 600001))

  p match {
    case Person2(_, Address(city, pin)) =>
      println(s"$city - $pin")
    case null =>
      println("No match")
  }
}
