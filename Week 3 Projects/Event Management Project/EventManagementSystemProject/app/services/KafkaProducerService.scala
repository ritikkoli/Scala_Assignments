package services

import javax.inject.{Inject, Singleton}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import play.api.libs.json.{JsValue, Json}
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaProducerService @Inject()(
                                      config: Configuration
                                    )(implicit ec: ExecutionContext) {

  private val bootstrapServers = config.get[String]("kafka.bootstrap.servers")

  private val producerProps = {
    val props = new java.util.Properties()
    props.put("bootstrap.servers", bootstrapServers)
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)

    // Additional properties
    val extraProps = config.get[Configuration]("kafka.producer.properties").entrySet
    extraProps.foreach { entry =>
      props.put(entry._1, entry._2.unwrapped().toString)
    }
    props
  }

  private val producer = new KafkaProducer[String, String](producerProps)

  def sendMessage(topic: String, key: String, payload: JsValue): Future[Unit] = Future {
    val record = new ProducerRecord[String, String](topic, key, Json.stringify(payload))
    println(record)
    producer.send(record)
  }
}
