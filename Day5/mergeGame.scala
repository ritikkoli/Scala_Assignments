object mergeGame{
    def main(args:Array[String]):Unit={
        val a=List(1,2)
        val b=List(3,4)
        val c1=a++b
        val c2=a::b
        println(s"a++b ${c1}")
        println(s"a::b ${c2}")
    }
}