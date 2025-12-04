case class Circle(r:Double)
case class Rectangle(w:Double,h:Double) 
object shapeAnalyzer{
    def main(args:Array[String]):Unit={
      println(area(Circle(3)))
      println(area(Rectangle(4,5)))
      println(area("Triangle"))
    }

    def area(shape:Any):Double=shape match{
        case Circle(r)=>math.Pi*r*r
        case Rectangle(w,h)=>w*h
        case _=> -1.0
    }

}