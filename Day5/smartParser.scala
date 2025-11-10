object smartParser{
    def main(args:Array[String]):Unit={
        println(parseAndDivide("25"))
        println(parseAndDivide("0"))
        println(parseAndDivide("abc"))
    }

    // Define safeDivide as a partial function
    val safeDivide: PartialFunction[Int, String] = {
        case x if x != 0 => (100 / x).toString
    }
    val safe = safeDivide.lift   // Int => Option[String]

    def parseAndDivide(input:String):Either[String,Int]={
        input.toIntOption match {
            case None =>
                Left("Invalid number")                      // Parsing failed
            case Some(num) =>
                safe(num) match {
                    case None       => Left("Division by zero")  // safeDivide was undefined
                    case Some(num)  => Right(num.toInt)          // Convert result to Int
                }
        }
    }
}