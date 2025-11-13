package library.items

// Sealed trait to represent all possible item types
sealed trait ItemType2 {
  def title: String
}

// Subclasses representing specific item types
case class Book2(title: String) extends ItemType2
case class Magazine2(title: String) extends ItemType2
case class DVD2(title: String) extends ItemType2
