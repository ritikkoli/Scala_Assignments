trait Robot{
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

trait EnergySaver2 extends Robot{
   def activateEnergySaver():Unit=println("Energy saver mode activated")
    override def shutdown():Unit=println("Robot shutting down to save energy")
}

class BasicRobot extends Robot{
    override def start():Unit=println("")
    override def shutdown(): Unit = println("")
}

object RobotTest extends App{
    // Robot with speech and movement capabilities
     val robot1 = new BasicRobot with SpeechModule with MovementModule
     robot1.start()             // BasicRobot starting up
     robot1.status()            // Robot is operational
     robot1.speak("Hello!")     // Robot says: Hello!
     robot1.moveForward()       // Moving forward
     robot1.shutdown()          // BasicRobot shutting down

     println("-----")

  // Robot with energy saver and movement capabilities
     val robot2 = new BasicRobot with EnergySaver2 with MovementModule
     robot2.start()             // BasicRobot starting up
     robot2.moveBackward()      // Moving backward
     robot2.activateEnergySaver() // Energy saver mode activated
     robot2.shutdown()          // Robot shutting down to save energy (from EnergySaver)
}
