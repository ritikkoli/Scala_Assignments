object tailRecursiveMaxElementArray{
    def main(args:Array[String]):Unit={

@annotation.tailrec
        def maxInArray(arr:Array[Int],acc:Int=Int.MinValue,index:Int=0):Int={
            if(index==arr.length) acc
            else maxInArray(arr,Math.max(acc,arr(index)),index+1)
        }

        val inputArray=Array(5,9,3,7,2)
        println(maxInArray(inputArray))
    }
}
