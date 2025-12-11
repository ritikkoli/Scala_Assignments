package generator

import java.sql.{Connection, DriverManager, PreparedStatement}
import java.time.{LocalDate, LocalDateTime, LocalTime}
import scala.util.Random

object SyntheticDataToMySQL {

  // ----------------------------
  // CONFIGURATION
  // ----------------------------
  val NUM_CUSTOMERS = 1000
  val NUM_PRODUCTS = 100
  val NUM_TRANSACTIONS = 10000

  val random = new Random()

  // UPDATE with your RDS endpoint + credentials
  val jdbcUrl =
    "jdbc:mysql://database:3306/ecommerce?rewriteBatchedStatements=true"

  val dbUser = "admin"
  val dbPass = "abc"


  // ----------------------------
  // RANDOM VALUE HELPERS
  // ----------------------------

  def randomDate(start: LocalDate, end: LocalDate): LocalDate = {
    val days = java.time.temporal.ChronoUnit.DAYS.between(start, end).toInt
    start.plusDays(random.nextInt(days + 1))
  }

  def randomTimestamp(start: LocalDate, end: LocalDate): String = {
    val d = randomDate(start, end)
    val t = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60))
    LocalDateTime.of(d, t).toString
  }


  // ----------------------------
  // JDBC UTILS
  // ----------------------------

  def getConnection(): Connection = {
    Class.forName("com.mysql.cj.jdbc.Driver")
    DriverManager.getConnection(jdbcUrl, dbUser, dbPass)
  }


  // ----------------------------
  // GENERATORS
  // ----------------------------

  def insertCustomers(conn: Connection): Unit = {
    println("Inserting customers...")

    val sql =
      """INSERT INTO customers(customer_id, name, email, gender, signup_date)
         VALUES (?, ?, ?, ?, ?)"""

    val stmt: PreparedStatement = conn.prepareStatement(sql)

    val startDate = LocalDate.now().minusYears(5)
    val endDate = LocalDate.now()

    (1 to NUM_CUSTOMERS).foreach { id =>
      val name = s"Customer$id"
      val email = s"customer$id@example.com"
      val gender = Seq("M", "F", "O")(random.nextInt(3))
      val signup = randomDate(startDate, endDate).toString

      stmt.setInt(1, id)
      stmt.setString(2, name)
      stmt.setString(3, email)
      stmt.setString(4, gender)
      stmt.setString(5, signup)

      stmt.addBatch()

      if (id % 500 == 0) stmt.executeBatch() // commit every 500 rows
    }

    stmt.executeBatch()
    stmt.close()

    println(s"Inserted $NUM_CUSTOMERS customers.")
  }


  def insertProducts(conn: Connection): Unit = {
    println("Inserting products...")

    val categories = Seq(
      "Electronics", "Fashion", "Home", "Sports", "Beauty",
      "Books", "Gaming", "Groceries", "Health", "Automotive"
    )

    val sql =
      """INSERT INTO products(product_id, name, category, price)
         VALUES (?, ?, ?, ?)"""

    val stmt = conn.prepareStatement(sql)

    (1 to NUM_PRODUCTS).foreach { id =>
      val name = s"Product$id"
      val category = categories(random.nextInt(categories.length))
      val price = 100 + random.nextInt(9900)

      stmt.setInt(1, id)
      stmt.setString(2, name)
      stmt.setString(3, category)
      stmt.setBigDecimal(4, new java.math.BigDecimal(price))

      stmt.addBatch()

      if (id % 200 == 0) stmt.executeBatch()
    }

    stmt.executeBatch()
    stmt.close()

    println(s"Inserted $NUM_PRODUCTS products.")
  }


  def insertTransactions(conn: Connection): Unit = {
    println("Inserting transactions... (this may take a moment)")

    val sql =
      """INSERT INTO transactions(txn_id, customer_id, product_id, qty, amount, txn_timestamp)
         VALUES (?, ?, ?, ?, ?, ?)"""

    val stmt = conn.prepareStatement(sql)

    val startDate = LocalDate.of(2020, 1, 1)
    val endDate = LocalDate.of(2024, 12, 31)

    (1 to NUM_TRANSACTIONS).foreach { id =>
      val customerId = 1 + random.nextInt(NUM_CUSTOMERS)
      val productId = 1 + random.nextInt(NUM_PRODUCTS)
      val qty = 1 + random.nextInt(10)
      val price = 100 + random.nextInt(9900)
      val amount = qty * price
      val ts = randomTimestamp(startDate, endDate)

      stmt.setLong(1, id)
      stmt.setInt(2, customerId)
      stmt.setInt(3, productId)
      stmt.setInt(4, qty)
      stmt.setBigDecimal(5, new java.math.BigDecimal(amount))
      stmt.setString(6, ts)

      stmt.addBatch()

      if (id % 2000 == 0) {
        println(s"  Inserted $id / $NUM_TRANSACTIONS")
        stmt.executeBatch()
      }
    }

    stmt.executeBatch()
    stmt.close()

    println(s"Inserted $NUM_TRANSACTIONS transactions.")
  }


  // ----------------------------
  // MAIN EXECUTION
  // ----------------------------

  def main(args: Array[String]): Unit = {
    println("\n=== Synthetic MySQL Data Generator (Direct Insert) ===")

    val conn = getConnection()

    println("Connected to MySQL successfully!")

    // Optional: clean existing data
    // conn.prepareStatement("DELETE FROM transactions").execute()
    // conn.prepareStatement("DELETE FROM customers").execute()
    // conn.prepareStatement("DELETE FROM products").execute()

    insertCustomers(conn)
    insertProducts(conn)
    insertTransactions(conn)

    conn.close()

    println("\n=== DONE: Synthetic data inserted into MySQL successfully ===")
  }
}
