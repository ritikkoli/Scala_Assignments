object intentionalCrasher{
    def main(args:Array[String]):Unit={
        val safe=safeDivide.lift
        println(safe(10))
        println(safe(0))
    }

    val safeDivide:PartialFunction[Int,String]={
        case x if x!=0 => s"Result is ${100/x}"
        //case x => s"not defined for ${x}"
    }
}