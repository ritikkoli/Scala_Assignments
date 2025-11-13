error id: file://<WORKSPACE>/Day6/droneFleetSystem.scala:[812..812) in Input.VirtualFile("file://<WORKSPACE>/Day6/droneFleetSystem.scala", "trait Drone{
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
    def sendMessage(msg:String):Unit=println("Communication module shutting down")
}

class BasicDrone extends Drone{
     override def activate():Unit=println("Basic Drone activated")
     override def deactivate():Unit=println("Basic Drone deactivated")
}

object")
file://<WORKSPACE>/file:<WORKSPACE>/Day6/droneFleetSystem.scala
file://<WORKSPACE>/Day6/droneFleetSystem.scala:30: error: expected identifier; obtained eof
object
      ^
#### Short summary: 

expected identifier; obtained eof