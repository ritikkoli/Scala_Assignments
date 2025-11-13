error id: file://<WORKSPACE>/Day6/personalizedRobotAssistantSystem.scala:[543..543) in Input.VirtualFile("file://<WORKSPACE>/Day6/personalizedRobotAssistantSystem.scala", "trait Robot{
def start():Unit
def shutdown():Unit
def status():Unit= println("Robot is operational")
}

trait SpeechModule{
    def speak(message:String):Unit= println(s"Robot says: $message")
}

trait MovementModule{
    def moveForward():Unit= println("Robot is moving forward")
    def moveBackward():Unit= println("Robot is moving backward")
}

trait EnergySaver extends Robot{
    def activateEnergySaver():Unit=println("Energy saver mode activated")
    override def shutdown():Unit=println("Robot shutting down to save energy")
}

class")
file://<WORKSPACE>/file:<WORKSPACE>/Day6/personalizedRobotAssistantSystem.scala
file://<WORKSPACE>/Day6/personalizedRobotAssistantSystem.scala:21: error: expected identifier; obtained eof
class
     ^
#### Short summary: 

expected identifier; obtained eof