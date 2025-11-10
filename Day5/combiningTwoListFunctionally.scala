object combiningTwoListFunctionally{
def main(args:Array[String]):Unit={
  

val students = List("Asha", "Bala", "Chitra")
val subjects = List("Math", "Physics")

val result =
  students.flatMap { student =>
    subjects.withFilter(subject => student.length >= subject.length)
            .map(subject => (student, subject))
  }

println(result)
// Output: List((Asha,Math), (Chitra,Math), (Chitra,Physics))




}
    }
