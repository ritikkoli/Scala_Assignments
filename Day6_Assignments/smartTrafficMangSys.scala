import java.sql.{Connection, DriverManager, ResultSet, Statement, PreparedStatement}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.io.StdIn

object SmartTrafficManagementSystem {
  def main(args: Array[String]): Unit = {
    // Load JDBC driver
    Class.forName("com.mysql.cj.jdbc.Driver")


    val url = "jdbc:mysql://azuremysql8823.mysql.database.xxxx.com:xxxx/xxxxx"
    val username = "xxxxx"
    val password = "XXXXXXXXXX" 

    val connection: Connection = DriverManager.getConnection(url, username, password)

    try {
      val statement: Statement = connection.createStatement()

      // Create tables if not exist
      val createVehiclesTable =
        """
          CREATE TABLE IF NOT EXISTS Vehicles (
            vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
            license_plate VARCHAR(20),
            vehicle_type VARCHAR(20),
            owner_name VARCHAR(100)
          )
        """

      val createSignalsTable =
        """
          CREATE TABLE IF NOT EXISTS TrafficSignals (
            signal_id INT AUTO_INCREMENT PRIMARY KEY,
            location VARCHAR(100),
            status VARCHAR(10)
          )
        """

      val createViolationsTable =
        """
          CREATE TABLE IF NOT EXISTS Violations (
            violation_id INT AUTO_INCREMENT PRIMARY KEY,
            vehicle_id INT,
            signal_id INT,
            violation_type VARCHAR(50),
            timestamp DATETIME,
            FOREIGN KEY (vehicle_id) REFERENCES Vehicles(vehicle_id),
            FOREIGN KEY (signal_id) REFERENCES TrafficSignals(signal_id)
          )
        """

      statement.execute(createVehiclesTable)
      statement.execute(createSignalsTable)
      statement.execute(createViolationsTable)

      println("Tables created successfully.\n")

      // Menu-driven program
      var continue = true
      while (continue) {
        println("\n==== Smart Traffic Management System ====")
        println("1. Add Vehicle")
        println("2. Add Traffic Signal")
        println("3. Record Violation")
        println("4. Update Signal Status")
        println("5. View Vehicles")
        println("6. View Traffic Signals")
        println("7. View Violations")
        println("8. Exit")
        print("Enter choice: ")

        val choice = StdIn.readInt()

        choice match {
          case 1 =>
            addVehicle(connection)
          case 2 =>
            addTrafficSignal(connection)
          case 3 =>
            recordViolation(connection)
          case 4 =>
            updateSignalStatus(connection)
          case 5 =>
            viewVehicles(connection)
          case 6 =>
            viewSignals(connection)
          case 7 =>
            viewViolations(connection)
          case 8 =>
            continue = false
            println("Exiting... Goodbye!")
          case _ =>
            println("Invalid choice! Try again.")
        }
      }

    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      connection.close()
    }
  }

  // ---------------- Functions ----------------

  def addVehicle(connection: Connection): Unit = {
    print("Enter License Plate: ")
    val plate = StdIn.readLine()
    print("Enter Vehicle Type (car/bike/truck): ")
    val vtype = StdIn.readLine()
    print("Enter Owner Name: ")
    val owner = StdIn.readLine()

    val sql = "INSERT INTO Vehicles (license_plate, vehicle_type, owner_name) VALUES (?, ?, ?)"
    val ps: PreparedStatement = connection.prepareStatement(sql)
    ps.setString(1, plate)
    ps.setString(2, vtype)
    ps.setString(3, owner)
    ps.executeUpdate()
    println("Vehicle added successfully.")
  }

  def addTrafficSignal(connection: Connection): Unit = {
    print("Enter Signal Location: ")
    val loc = StdIn.readLine()
    print("Enter Status (green/yellow/red): ")
    val status = StdIn.readLine()

    val sql = "INSERT INTO TrafficSignals (location, status) VALUES (?, ?)"
    val ps: PreparedStatement = connection.prepareStatement(sql)
    ps.setString(1, loc)
    ps.setString(2, status)
    ps.executeUpdate()
    println("Traffic signal added successfully.")
  }

  def recordViolation(connection: Connection): Unit = {
    print("Enter Vehicle ID: ")
    val vid = StdIn.readInt()
    print("Enter Signal ID: ")
    val sid = StdIn.readInt()
    print("Enter Violation Type (speeding/signal jump): ")
    StdIn.readLine() // consume newline
    val vtype = StdIn.readLine()
    val timestamp = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val sql = "INSERT INTO Violations (vehicle_id, signal_id, violation_type, timestamp) VALUES (?, ?, ?, ?)"
    val ps: PreparedStatement = connection.prepareStatement(sql)
    ps.setInt(1, vid)
    ps.setInt(2, sid)
    ps.setString(3, vtype)
    ps.setString(4, timestamp.format(formatter))
    ps.executeUpdate()
    println("Violation recorded successfully.")
  }

  def updateSignalStatus(connection: Connection): Unit = {
    print("Enter Signal ID to update: ")
    val sid = StdIn.readInt()
    print("Enter new Status (green/yellow/red): ")
    StdIn.readLine() // consume newline
    val newStatus = StdIn.readLine()

    val sql = "UPDATE TrafficSignals SET status = ? WHERE signal_id = ?"
    val ps: PreparedStatement = connection.prepareStatement(sql)
    ps.setString(1, newStatus)
    ps.setInt(2, sid)
    val rows = ps.executeUpdate()

    if (rows > 0) println("Signal status updated.")
    else println("No signal found with that ID.")
  }

  def viewVehicles(connection: Connection): Unit = {
    val rs: ResultSet = connection.createStatement().executeQuery("SELECT * FROM Vehicles")
    println("\n--- Vehicles ---")
    while (rs.next()) {
      println(s"${rs.getInt("vehicle_id")} | ${rs.getString("license_plate")} | ${rs.getString("vehicle_type")} | ${rs.getString("owner_name")}")
    }
  }

  def viewSignals(connection: Connection): Unit = {
    val rs: ResultSet = connection.createStatement().executeQuery("SELECT * FROM TrafficSignals")
    println("\n--- Traffic Signals ---")
    while (rs.next()) {
      println(s"${rs.getInt("signal_id")} | ${rs.getString("location")} | ${rs.getString("status")}")
    }
  }

  def viewViolations(connection: Connection): Unit = {
    val query =
      """SELECT v.violation_id, ve.license_plate, ve.owner_name, ts.location, v.violation_type, v.timestamp
        |FROM Violations v
        |JOIN Vehicles ve ON v.vehicle_id = ve.vehicle_id
        |JOIN TrafficSignals ts ON v.signal_id = ts.signal_id
        |ORDER BY v.timestamp DESC""".stripMargin

    val rs: ResultSet = connection.createStatement().executeQuery(query)
    println("\n--- Violations ---")
    while (rs.next()) {
      println(s"${rs.getInt("violation_id")} | ${rs.getString("license_plate")} | ${rs.getString("owner_name")} | ${rs.getString("location")} | ${rs.getString("violation_type")} | ${rs.getString("timestamp")}")
    }
  }
}
