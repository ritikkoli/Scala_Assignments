error id: file://<WORKSPACE>/Day6/smartHomeAutomationSystem.scala:
file://<WORKSPACE>/Day6/smartHomeAutomationSystem.scala
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
offset: 768
uri: file://<WORKSPACE>/Day6/smartHomeAutomationSystem.scala
text:
```scala
trait device{
    def turnOn():Unit
    def turnOff():Unit
    def status():Unit=println("Device is operational")
}

trait Connectivity{
    def connect():Unit= println("Device connected to network")
    def disconnect():Unit= println("Device disconnected from network")
}

trait EnergySaver extends device{
    def activateEnergySaver():Unit= println("Energy saver mode activated")
    override def turnOff(): Unit = {
        println("Device powered down to save energy")
    }
}

class SmartLight extends device with Connectivity with EnergySaver{
     override def turnOn(): Unit = {
        //super.turnOn()
        println("SmartLight is now ON with connectivity features")
    }
        override def turnOff(): Unit = {
            super.turnOff()
            p@@rintln("SmartLight is now OFF")
        }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 