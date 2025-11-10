object arrowPuzzle{

    def main(args:Array[String]):Unit={
        val animals=Map(
            "dog" -> "bark",
            "cat" -> "meow",
            "cow" -> "moo"
        )
        println(animals + ("lion" -> "roar"))
        println(animals("cat"))

        println(animals.getOrElse("tiger", "not found"))
    }
}