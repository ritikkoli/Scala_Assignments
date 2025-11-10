object tailRecursiveFactorial{
    def main(args:Array[String]):Unit={
        @annotation.tailrec
        def factorial(n:Int,acc:Int=1):Int={
            if (n<=1)acc 
            else factorial(n-1,acc*n)
        }
        println(factorial(5))
    }
}