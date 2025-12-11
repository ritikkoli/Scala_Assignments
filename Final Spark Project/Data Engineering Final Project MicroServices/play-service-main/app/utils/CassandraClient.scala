package utils

import com.datastax.oss.driver.api.core.{CqlSession, CqlSessionBuilder}
import com.typesafe.config.ConfigFactory

import java.net.InetSocketAddress
import javax.inject.{Inject, Singleton}



@Singleton
class CassandraClient @Inject()( ) {
private val config= ConfigFactory.load()
  private val keyspace = config.getString("cassandra.keyspace")
  private val username = config.getString("cassandra.username")
  private val password = config.getString("cassandra.password")
  private val host     = config.getString("cassandra.contact-points.host")
  private val port     = config.getInt("cassandra.contact-points.port")

  val session: CqlSession =
    CqlSession.builder()
      .addContactPoint(new InetSocketAddress(host, port))
      .withLocalDatacenter("us-east-1")
      .withAuthCredentials(username, password)
      .withKeyspace(keyspace)
      .withSslContext(SslContextBuilder.build())
      .build()
}

