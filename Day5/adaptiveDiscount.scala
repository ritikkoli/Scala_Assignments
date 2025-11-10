object adaptiveDiscount{
    def main(args:Array[String]):Unit={
        val goldDiscount=discountStrategy("gold")
        val silverDiscount=discountStrategy("silver")
        val regularDiscount=discountStrategy("regular")


        println(goldDiscount(1000))      

        println(silverDiscount(1000))      

        println(regularDiscount(1000))      
        }

        def discountStrategy(memberType:String):Double=>Double={
            memberType match
                case "gold" => (price:Double) => price * (1-0.2)
                case "silver" => (price:Double) => price * (1-0.1)
                case _ => (price:Double) => price
        }
}