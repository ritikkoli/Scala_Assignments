error id: file://<WORKSPACE>/Day6/IntergalacticTransportSystem.scala:scala/Unit#
file://<WORKSPACE>/Day6/IntergalacticTransportSystem.scala
empty definition using pc, found symbol in pc: scala/Unit#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -Unit#
	 -scala/Predef.Unit#
offset: 188
uri: file://<WORKSPACE>/Day6/IntergalacticTransportSystem.scala
text:
```scala
abstract class Spacecraft{
    def launch:String
    def land(fuelValue:String):Unit=println("Landing the spacecraft")

}

class CargoShip extends Spacecraft{
    override def launch: Unit@@=println("CargoShip launching")
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/Unit#