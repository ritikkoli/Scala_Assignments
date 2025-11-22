package services

import javax.inject._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import play.api.libs.json.Json
import events.{AllocationCreatedEvent, EquipmentReturnedEvent, MaintenanceReportedEvent, OverdueEvent}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaProducerService @Inject()(implicit ec: ExecutionContext) {

  private val producerProps = new java.util.Properties()
  producerProps.put("bootstrap.servers", "localhost:9092")
  producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("acks", "all")

  private val producer = new KafkaProducer[String, String](producerProps)

  val topic = "equipment-allocation-events"
  val topic2 = "equipment-maintenance-events"
  val topic3 = "equipment-return-events"
  val topic4 = "equipment-overdue-events"


  def publishAllocationCreated(event: AllocationCreatedEvent): Future[Unit] = Future {
    val json = Json.toJson(event).toString()
    val record = new ProducerRecord[String, String](topic, event.allocationId.toString, json)
    println(record)
    producer.send(record)
  }

  def publishEquipmentReturned(event: EquipmentReturnedEvent): Future[Unit] = Future{
    //val json = Json.toJson(event).toString()
    //producer.send("equipment-return-events", event.allocationId.toString, json)

    val json = Json.toJson(event).toString()
    val record = new ProducerRecord[String, String](topic3, event.allocationId.toString, json)
    println(record)
    producer.send(record)
  }


  def publishMaintenanceReported(event2: MaintenanceReportedEvent): Future[Unit] = Future {

    // Convert event to JSON string
    //val jsonString = Json.toJson(event).toString()

    val jsonString = Json.toJson(event2).toString()
    val record = new ProducerRecord[String, String](topic2, event2.maintenanceId.toString, jsonString)
    println(record)
    producer.send(record)

    try {
      producer.send(record).get() // sync send with error propagation
      ()
    } catch {
      case ex: Exception =>
        println(s"Failed to publish MaintenanceReportedEvent: ${ex.getMessage}")
        throw ex
    }
  }


//  def publishOverdueEvent(event: OverdueEvent): Future[Unit] = {
//    val json = Json.toJson(event).toString()
//    send("equipment-overdue-events", event.allocationId.toString, json)
//  }

  def publishOverdueEvent(event: OverdueEvent): Future[Unit] = Future{
    //val json = Json.toJson(event).toString()
    //producer.send("equipment-return-events", event.allocationId.toString, json)

    val json = Json.toJson(event).toString()
    val record = new ProducerRecord[String, String](topic4, event.allocationId.toString, json)
    println(record)
    producer.send(record)
  }

}
