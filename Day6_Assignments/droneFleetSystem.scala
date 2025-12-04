trait Drone{
    def activate():Unit
    def deactivate():Unit
    def status():Unit=println("Drone Status")
}

trait NavigationModule extends Drone{
    def flyTo(destination: String):Unit={
        println(s"Its destination is $destination")
    }

    override def deactivate():Unit=println("Navigation systems shutting down")
}

trait DefenseModule extends Drone{  
    def activateShields():Unit=print("Defense Module")
    override def deactivate(): Unit = println("Defense systems deactivated")

}

trait CommunicationModule extends Drone{
    def sendMessage(msg:String):Unit=println(s"Communication module $msg shutting down")
}

class BasicDrone extends Drone{
     override def activate():Unit=println("Basic Drone activated")
     override def deactivate():Unit=println("Basic Drone deactivated")
}

object DroneImplObject extends App{
    val drone = new BasicDrone with NavigationModule with DefenseModule

    drone.activate()
    drone.deactivate()
    drone.status()
    drone.flyTo("Apple")
    drone.activateShields()

    println("-----")

    val drone2 = new BasicDrone with NavigationModule with DefenseModule with CommunicationModule
    drone2.activate()
    drone2.deactivate()
    drone2.status()
    drone2.flyTo("Apple")
    drone2.activateShields()
    drone2.sendMessage("For Drone 2")
    
}