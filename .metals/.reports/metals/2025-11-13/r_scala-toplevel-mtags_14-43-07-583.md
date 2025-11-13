error id: file://<WORKSPACE>/Day6/droneFleetSystem.scala:[362..363) in Input.VirtualFile("file://<WORKSPACE>/Day6/droneFleetSystem.scala", "trait Drone{
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

trait DefenseModule{
    def
}")
file://<WORKSPACE>/file:<WORKSPACE>/Day6/droneFleetSystem.scala
file://<WORKSPACE>/Day6/droneFleetSystem.scala:17: error: expected identifier; obtained rbrace
}
^
#### Short summary: 

expected identifier; obtained rbrace