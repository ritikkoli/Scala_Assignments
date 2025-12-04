case class Vec2D(x: Double, y: Double) {

  // Vector addition
  def +(that: Vec2D): Vec2D =
    Vec2D(this.x + that.x, this.y + that.y)

  // Vector subtraction
  def -(that: Vec2D): Vec2D =
    Vec2D(this.x - that.x, this.y - that.y)

  // Scalar multiplication (vector * scalar)
  def *(scalar: Double): Vec2D =
    Vec2D(this.x * scalar, this.y * scalar)

  override def toString: String =
    s"Vec2D($x,$y)"
}

// Implicit conversion to allow: scalar * vector
object Vec2DImplicits {
  implicit class ScalarOps(val scalar: Double) extends AnyVal {
    def *(v: Vec2D): Vec2D = Vec2D(v.x * scalar, v.y * scalar)
  }
}

object Vec2DTest extends App {
  import Vec2DImplicits.*

  val v1 = Vec2D(2, 3)
  val v2 = Vec2D(4, 1)

  println(v1 + v2)  // Vec2D(6,4)
  println(v1 - v2)  // Vec2D(-2,2)
  println(v1 * 3)   // Vec2D(6,9)
  println(3 * v1)   // Vec2D(6,9)
}
