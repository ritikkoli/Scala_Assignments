error id: file://<WORKSPACE>/Day3/generics.scala:
file://<WORKSPACE>/Day3/generics.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 613
uri: file://<WORKSPACE>/Day3/generics.scala
text:
```scala
// Day1/variables.scala

// Variable declarations in Scala

// Using 'val' for immutable variables
val immutableVariable: Int = 10
println(s"Immutable Variable: $immutableVariable")

// Using 'var' for mutable variables
var mutableVariable: String = "Hello"
println(s"Mutable Variable (before change): $mutableVariable")
mutableVariable = "Hello, Scala!"
println(s"Mutable Variable (after change): $mutableVariable")

// Using 'lazy val' for lazy initialization
lazy val lazyVariable: Double = {
  println("Lazy variable initialized")
  3.14
}
println("Before accessing lazy variable")
println(s"Lazy Variable: $l@@azyVariable") // This will trigger initialization
```


#### Short summary: 

empty definition using pc, found symbol in pc: 