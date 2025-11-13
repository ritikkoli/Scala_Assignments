trait Device{
    def turnOn():Unit
    def turnOff():Unit
    def status():Unit=println("Device is operational")
}

trait Connectivity{
    def connect():Unit= println("Device connected to network")
    def disconnect():Unit= println("Device disconnected from network")
}

trait EnergySaver extends Device{
    def activateEnergySaver():Unit= println("Energy saver mode activated")
    override def turnOff(): Unit = {
        println("Device powered down to save energy")
    }
}

class SmartLight extends Device with EnergySaver with Connectivity {
  override def turnOn(): Unit = {
    println("SmartLight is now ON with connectivity features")
  }
}



  

class SmartThermostat extends Device with Connectivity{
    override def turnOn(): Unit = {
        println("SmartThermostat is now ON with connectivity features")
    }
    override def turnOff(): Unit = {
        println("SmartThermostat is now OFF")
    }
}

trait VoiceControl{
    def turnOn():Unit= println("Voice command: Turn on devices")
    def turnOff():Unit= println("Voice command: Turn off devices")
}

object SmartHomeTest extends App{

val smartLight= new SmartLight
    smartLight.turnOn()
    smartLight.activateEnergySaver()
    smartLight.status()
    smartLight.turnOff()
    smartLight.connect()
    smartLight.disconnect()

    println("----")

    

    val smartThermostat= new SmartThermostat
    smartThermostat.turnOn()
    smartThermostat.connect()
    smartThermostat.status()
    smartThermostat.turnOff()
    smartThermostat.disconnect()

    println("-----")

val voiceLight= new SmartLight with VoiceControl{
    override def turnOn(): Unit = {
        super.turnOn()
        println("SmartLight turned on via voice control")
    }
    override def turnOff(): Unit = {
        super.turnOff()
        println("SmartLight turned off via voice control")
    }
}
voiceLight.turnOn()
voiceLight.turnOff()
  
}