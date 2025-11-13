error id: file://<WORKSPACE>/Day6/IntergalacticTransportSystem.scala:scala/Predef.println(+1).
file://<WORKSPACE>/Day6/IntergalacticTransportSystem.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -println.
	 -println#
	 -println().
	 -scala/Predef.println.
	 -scala/Predef.println#
	 -scala/Predef.println().
offset: 622
uri: file://<WORKSPACE>/Day6/IntergalacticTransportSystem.scala
text:
```scala
abstract class Spacecraft{
    def launch:Unit
    def land(fuelValue:String):Unit=println("Landing the spacecraft")

}

class CargoShip extends Spacecraft{
    override def launch= println("CargoShip launching")
    override def land(fuelValue: String): Unit = println(s"CargoShip landing with fuel value: $fuelValue")
}

class PassengerShip extends Spacecraft{
override def launch: Unit = println("PassengerShip launching")
override def land(fuelValue: String): Unit = println(s"PassengerShip landing with fuel value: $fuelValue")
}

class LuxuryCruiser extends PassengerShip{
    def playEntertainmentSystem():Unit= pri@@ntln("Playing entertainment system")
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 