object personalizedCalculator {
  def main(args: Array[String]): Unit = {
    val add = calculate("add")
    val sub = calculate("sub")
    val multiply = calculate("multiply")
    val div = calculate("div")

    println(add(10, 5))
    println(sub(10, 5))
    println(multiply(10, 5))
    println(div(10, 5))
  }

  def calculate(op: String)(x: Int, y: Int): Int = {
    op match {
      case "add" => x + y
      case "sub" => x - y
      case "multiply" => x * y
      case "div" => x / y
    }
  }
}