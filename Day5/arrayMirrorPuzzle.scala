object arrayMirrorPuzzle{
    def main(args:Array[String]):Unit={
        def mirrorArray(arr:Array[Int]):Array[Int]={
            val mirrored=new Array [Int](arr.length*2)
            for(i<-arr.indices){
                mirrored(i)=arr(i)
                mirrored(mirrored.length-1-i)=arr(i)
            }
            mirrored
        }

        val inputArray=Array(1,2,3)
        println(mirrorArray(inputArray).mkString(","))
    }
}
