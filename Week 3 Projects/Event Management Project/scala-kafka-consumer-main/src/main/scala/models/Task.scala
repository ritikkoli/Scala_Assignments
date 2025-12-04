package models

import java.time.LocalDateTime

case class Task(
                 id: Long,
                 eventId: Long,
                 teamId: Long,
                 taskName: String,
                 instructions: String,
                 deadline: LocalDateTime,
                 status: String,
                 createdAt: LocalDateTime
               )
