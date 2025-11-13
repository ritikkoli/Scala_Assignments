package library.operations

import library.items._

import library.users.Member2

object LibraryOperations {

  // Default implicit member
  implicit val defaultMember: Member2 = new Member2("Default Member")

  // Implicit conversion from String to Book
  implicit def stringToBook(title: String): Book2 = Book2(title)

  // Borrow function using implicit member
  def borrow(item: ItemType2)(implicit member: Member2): Unit = {
    member.borrowItem(item)
  }

  // Describe the item using pattern matching
  def itemDescription(item: ItemType2): Unit = item match {
    case Book2(title)     => println(s"Book: '$title' is available for borrowing.")
    case Magazine2(title) => println(s"Magazine: '$title' - latest edition available.")
    case DVD2(title)      => println(s"DVD: '$title' - enjoy watching!")
  }
}
