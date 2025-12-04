object wordFrequencyCounter{

def main(args:Array[String]):Unit={
        val lines = List(
            "Scala is powerful",
            "Scala is concise",
            "Functional programming is powerful"
        )

        val words = lines.flatMap(_.split(" "))

        val wordFreq = words.groupBy(identity).mapValues(_.size).toMap

        println(wordFreq)
}
}