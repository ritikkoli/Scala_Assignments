error id: file://<WORKSPACE>/Day6_Q5/itemType.scala:library/items/Book#title.
file://<WORKSPACE>/Day6_Q5/itemType.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol library/items/Book#title.
empty definition using fallback
non-local guesses:

offset: 190
uri: file://<WORKSPACE>/Day6_Q5/itemType.scala
text:
```scala
package library.items

// Sealed trait to represent all possible item types
sealed trait ItemType2 {
  def title: String
}

// Subclasses representing specific item types
case class Book(tit@@le: String) extends ItemType
case class Magazine(title: String) extends ItemType
case class DVD(title: String) extends ItemType

```


#### Short summary: 

empty definition using pc, found symbol in pc: 