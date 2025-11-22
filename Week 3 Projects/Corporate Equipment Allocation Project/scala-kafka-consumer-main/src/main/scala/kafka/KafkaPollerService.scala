package kafka

import java.time.Duration
import java.util.{Collections, Properties}
import scala.concurrent.{ExecutionContext, Future}
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecords}
import akka.actor.ActorRef
import play.api.libs.json._
import com.typesafe.config.ConfigFactory

class KafkaPollerService(actorRouter: ActorRef)(implicit ec: ExecutionContext) {

  private val conf = ConfigFactory.load().getConfig("kafka")
  private val bootstrap = conf.getString("bootstrap.servers")
  private val groupId = conf.getString("group.id")
  private val topics = conf.getStringList("topics")
  private val pollMs = conf.getInt("poll.duration.ms")

  private val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap)
  props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  private val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(topics)

  private val running = new java.util.concurrent.atomic.AtomicBoolean(true)

  def start(): Unit = {
    Future {
      try {
        while (running.get()) {
          val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(pollMs.toLong))
          val it = records.iterator()
          while (it.hasNext) {
            val rec = it.next()
            // send raw message to router actor -> will route by topic
            actorRouter ! KafkaMessage(rec.topic(), rec.key(), rec.value())
          }
        }
      } catch {
        case ex: Exception =>
          ex.printStackTrace()
      } finally {
        consumer.close()
      }
    }
  }

  def stop(): Unit = running.set(false)
}

// simple message wrapper
case class KafkaMessage(topic: String, key: String, value: String)
