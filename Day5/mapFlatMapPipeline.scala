import scala.util.Try

object mapFlatMapPipeline extends App{
  
    val data = List("10", "20", "x", "30")

    val result = data.flatMap(s => Try(s.toInt).toOption).map(x => x * x)

    println(result)
  
}