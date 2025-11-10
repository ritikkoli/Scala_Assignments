error id: file://<WORKSPACE>/Day5/smartTempConv.scala:
file://<WORKSPACE>/Day5/smartTempConv.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -scale.
	 -scale#
	 -scale().
	 -scala/Predef.scale.
	 -scala/Predef.scale#
	 -scala/Predef.scale().
offset: 176
uri: file://<WORKSPACE>/Day5/smartTempConv.scala
text:
```scala
object smartTempConverter{
    def main(args:Array[String]):Unit={
     def convertTemp(value:Double,scale:String):Double={
        scale.toLowerCase match {
            if sca@@le == "c" => (value -32) *5/9
            case "f" => (value *9/5) +32
            case _ => value
        }
     }

     println(convertTemp(0,"C"))
     println(convertTemp(212,"F"))
     println(convertTemp(100,"X"))
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 