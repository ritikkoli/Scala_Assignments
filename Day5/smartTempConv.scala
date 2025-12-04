object smartTempConverter{
    def main(args:Array[String]):Unit={
     def convertTemp(value:Double, scale:String):Double = {
        scale.toLowerCase match {
            case "c" => (value - 32) * 5 / 9
            case "f" => (value * 9 / 5) + 32
            case _   => value
        }
     }

       def convertTempIfElse(value: Double, scale: String): Double = {
       if (scale.toLowerCase == "c") {
          (value - 32) * 5 / 9
       } else if (scale.toLowerCase == "f") {
          (value * 9 / 5) + 32
       } else {
          value
       }
      }
     println(convertTemp(0,"C"))
     println(convertTemp(212,"F"))
     println(convertTemp(100,"X"))


     println(convertTempIfElse(0,"C"))
     println(convertTempIfElse(212,"F"))
     println(convertTempIfElse(100,"X"))
    }
}
