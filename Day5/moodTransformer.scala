object moodTransformer{
    def moodChanger(prefix:String):String=>String={
        word=>s"$prefix-$word-$prefix"
    }

    def main(args:Array[String]):Unit={
        val happyMood=moodChanger("happy")
        println(happyMood("day"))

        val sadMood=moodChanger("angry")
        println(sadMood("crowd"))
    }
}