object digitSumUsingSimpRecursion{
    def main(args:Array[String]):Unit={

        def digitSum(n:Int):Int={
            if(n==0) 0
            else n%10 + digitSum(n/10)
        }

        println(digitSum(1345))
    }
    
}