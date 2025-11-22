package tables

import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeMapping {

  private val formatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit val localDateTimeColumnType: BaseColumnType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ldt => Timestamp.valueOf(ldt), // LocalDateTime → DB
      ts => {
        val text = ts.toString.substring(0, 19) // "2025-02-10 15:00:00"
        LocalDateTime.parse(text, formatter)
      }
    )
}
