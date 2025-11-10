error id: file://<WORKSPACE>/Day5/sentencePipeline.scala:toUpper.
file://<WORKSPACE>/Day5/sentencePipeline.scala
empty definition using pc, found symbol in pc: toUpper.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -s/head/toUpper.
	 -s/head/toUpper#
	 -s/head/toUpper().
	 -scala/Predef.s.head.toUpper.
	 -scala/Predef.s.head.toUpper#
	 -scala/Predef.s.head.toUpper().
offset: 209
uri: file://<WORKSPACE>/Day5/sentencePipeline.scala
text:
```scala
object sentencePipeline{
    def main(args:Array[String]):Unit={
        val trimSpaces:String=>String=_.trim
        val toLower:String=>String=_.toLowerCase
        val capitalizeFirst:String=>String=s.head.@@toUpper+s.tail

    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: toUpper.