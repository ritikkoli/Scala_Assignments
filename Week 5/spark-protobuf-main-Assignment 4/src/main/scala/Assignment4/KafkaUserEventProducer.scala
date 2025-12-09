package Assignment4

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import example.UserEvent.UserEvent  // <-- ScalaPB-generated class

object KafkaUserEventProducer {

  def main(args: Array[String]): Unit = {

    // -------------------------------------------
    // Kafka configuration
    // -------------------------------------------
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

    val topic = "user-events"

    val producer = new KafkaProducer[String, Array[Byte]](props)

    // -------------------------------------------
    // Send 10 test UserEvent messages
    // -------------------------------------------
    for (i <- 1 to 10) {
      val event = UserEvent(
        userId = s"user-$i",
        action = if (i % 2 == 0) "click" else "login",
        value = i * 10.5
      )

      val bytes = event.toByteArray

      val record = new ProducerRecord[String, Array[Byte]](
        topic,
        event.userId,   // key
        bytes           // protobuf payload
      )

      producer.send(record)
      println(s"Sent → $event")
    }

    producer.flush()
    producer.close()

    println("✓ All UserEvent messages sent to Kafka successfully.")
  }
}
