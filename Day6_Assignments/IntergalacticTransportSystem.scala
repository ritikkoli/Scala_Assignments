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

final class LuxuryCruiser extends PassengerShip{
    def playEntertainmentSystem():Unit= println("Playing entertainment system")
}

trait Autopilot{
    def autoNavigate():Unit= println("Auto navigating the spacecraft")
}

object Main2 extends App{
    val cargoShip= new CargoShip
    cargoShip.launch
    cargoShip.land("High")

    val passengerShip= new PassengerShip
    passengerShip.launch
    passengerShip.land("Medium")

    val luxuryCruiser= new LuxuryCruiser
    luxuryCruiser.launch
    luxuryCruiser.land("Low")
    luxuryCruiser.playEntertainmentSystem()

    val autoPilotShip= new PassengerShip with Autopilot
    autoPilotShip.launch
    autoPilotShip.autoNavigate()
    autoPilotShip.land("Full")
}