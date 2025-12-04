package main

import library.items._
import library.users._
import library.operations.LibraryOperations._

object LibraryMain extends App {

  // Explicit member
  val names = new Member2("Ritik")
  val books: Book2 = Book2("Scala Programming Book")
  borrow(books)(using names)          

  // Using implicit default member
  val dvd2: DVD2 = DVD2("Inception")
  borrow(dvd2)                  // Default Member borrows 'Inception'

  // Using implicit conversion from String to Book
  borrow("Harry Potter")        // Default Member borrows 'Harry Potter'

  // Demonstrate sealed trait pattern matching
  val items: List[ItemType2] = List(
    Book2("Functional Programming in Scala"),
    Magazine2("India Times Today"),
    DVD2("Matrix")
  )
  items.foreach(itemDescription)
}
