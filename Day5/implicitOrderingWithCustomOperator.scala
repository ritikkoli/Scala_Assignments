class Person3(val name: String, val age: Int)

object PersonOrdering {
  implicit class AdditionalPerson3(p: Person3) {
    def >(other: Person3): Boolean = p.age > other.age
    def <(other: Person3): Boolean = p.age < other.age
    def >=(other: Person3): Boolean = p.age >= other.age
    def <=(other: Person3): Boolean = p.age <= other.age
  }

  def main(args: Array[String]): Unit = {
    val p1 = new Person3("Ravi", 25)
    val p2 = new Person3("Meena", 30)
    println(p1 < p2)
    println(p1 >= p2)
  }
}