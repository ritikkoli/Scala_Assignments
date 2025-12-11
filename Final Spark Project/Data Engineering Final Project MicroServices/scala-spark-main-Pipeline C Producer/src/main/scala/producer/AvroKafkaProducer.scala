package producer

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.{Schema}
import org.apache.avro.io.{DatumWriter, EncoderFactory}
import org.apache.avro.generic.GenericDatumWriter
import scala.util.Random
import java.io.ByteArrayOutputStream

object AvroKafkaProducer {

  def main(args: Array[String]): Unit = {

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

    val topic = "events_avro"
    val producer = new KafkaProducer[String, Array[Byte]](props)

    // Load schema
    val schemaStream = getClass.getResourceAsStream("/event.avsc")
    val schemaString = scala.io.Source.fromInputStream(schemaStream).mkString
    val schema = new Schema.Parser().parse(schemaString)

    val eventTypes = Seq("view", "wishlist", "add_to_cart", "purchase")

    while (true) {

      val record: GenericRecord = new GenericData.Record(schema)
      record.put("event_id", java.util.UUID.randomUUID().toString)
      record.put("customer_id", Random.nextInt(5000) + 1)
      record.put("event_type", eventTypes(Random.nextInt(eventTypes.length)))
      record.put("product_id", Random.nextInt(500) + 1)
      record.put("event_timestamp", java.time.Instant.now.toString)

      // Avro Serialization
      val out = new ByteArrayOutputStream()
      val writer: DatumWriter[GenericRecord] = new GenericDatumWriter[GenericRecord](schema)
      val encoder = EncoderFactory.get().binaryEncoder(out, null)
      writer.write(record, encoder)
      encoder.flush()
      out.close()

      val avroBytes = out.toByteArray()

      val message = new ProducerRecord[String, Array[Byte]](
        topic,
        record.get("event_id").toString,
        avroBytes
      )

      producer.send(message)

      println(s"Sent Avro Event: $record")

      Thread.sleep(2000)
    }
  }
}
