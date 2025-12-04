object building2dMultiplicationTable{

    def main(args:Array[String]):Unit={
        def multiplicationTable(n:Int):List[String]={
            (1 to n).toList.flatMap { i =>
        (1 to n).map { j =>
          s"$i x $j = ${i*j}"
        }
            }
        }
        val table = multiplicationTable(3)
        table.foreach(println)
    }
}