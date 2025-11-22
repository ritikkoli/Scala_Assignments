package consumer

import akka.actor.ActorRef
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import akka.stream.Materializer
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._
import org.slf4j.LoggerFactory
import akka.kafka.ConsumerMessage.CommittableMessage

class KafkaConsumerService(
                            processor: ActorRef,
                            config: Config
                          )(implicit mat: Materializer, ec: ExecutionContext) {

  private val log = LoggerFactory.getLogger(getClass)

  private val bootstrap = config.getString("bootstrap.servers")
  private val groupId  = config.getString("group.id")
  private val topics   = config.getStringList("topics").asScala.toSet

  private val settings =
    ConsumerSettings(mat.system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrap)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  def start(): Unit = {
    log.info(s"Starting Kafka Consumer (groupId=$groupId) for topics: ${topics.mkString(",")}")

    Consumer
      .plainSource(settings, Subscriptions.topics(topics))
      .map { record =>
        // Log metadata for each record
        log.info(
          s"Consumed record -> topic=${record.topic()}, partition=${record.partition()}, offset=${record.offset()}, key=${record.key()}, timestamp=${record.timestamp()}"
        )

        // Forward payload and topic to processor actor
        processor ! (record.topic(), record.value())
      }
      .runWith(Sink.ignore)
  }
}
