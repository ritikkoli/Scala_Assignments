package utils

import java.sql.DriverManager

object TestMySQL {
  def main(args: Array[String]): Unit = {
    Class.forName("com.mysql.cj.jdbc.Driver")
    val conn = DriverManager.getConnection(
      "jdbc:mysql://database-1.cev02em2c3cb.us-east-1.rds.amazonaws.com:3306/ecommerce",
      "admin",
      "Jaisadguru"
    )
    println("Connected successfully!")
    conn.close()
  }
}

