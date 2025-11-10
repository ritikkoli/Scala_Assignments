case class Order(id:Int, amount:Double, status:String)
object functionalFilteringTransformation{

def main(args:Array[String]):Unit={

    val orders = List(
        Order(1, 1200.0, "Delivered"),
        Order(2, 250.5, "Pending"),
        Order(3, 980.0, "Delivered"),
        Order(4, 75.0, "Cancelled")
    )

    val result = for {
        order <- orders
        if order.status == "Delivered"
        if order.amount > 500
    } yield s"Order #${order.id} -> ₹${order.amount}"
    println(result)

}
}