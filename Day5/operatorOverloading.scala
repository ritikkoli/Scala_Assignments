case class Counter(val value:Int){
    def +(that:Counter):Int={
        (this.value+that.value)
    }
    override def toString:String=s"$value"
}
object operatorOverloading{
    def main(args:Array[String]):Unit={
        val a=new Counter(5)
        val b=new Counter(7)
        println(a+b) 

    }
}