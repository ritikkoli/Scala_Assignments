error id: file://<WORKSPACE>/Day5/safeDivision.scala:scala/package.List.
file://<WORKSPACE>/Day5/safeDivision.scala
empty definition using pc, found symbol in pc: scala/package.List.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -List.
	 -List#
	 -List().
	 -scala/Predef.List.
	 -scala/Predef.List#
	 -scala/Predef.List().
offset: 209
uri: file://<WORKSPACE>/Day5/safeDivision.scala
text:
```scala
object safeDivision {
  def safeDivide(a: Int, b: Int): Either[String, Double] =
    if (b == 0) Left("Division by zero") else Right(a.toDouble / b)

  def main(args: Array[String]): Unit = {
    val pairs = L@@ist((10, 2), (5, 0), (8, 4))
    val results = pairs.map { case (a, b) => safeDivide(a, b) }
    val valids = results.collect { case Right(v) => v }
    val errors = results.collect { case Left(e) => e }
    println(s"Valid results: $valids")
    println(s"Errors: $errors")
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/package.List.