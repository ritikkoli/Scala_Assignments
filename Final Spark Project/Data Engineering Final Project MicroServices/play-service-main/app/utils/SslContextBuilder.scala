package utils

import javax.net.ssl.{SSLContext, TrustManagerFactory}
import java.security.KeyStore
import java.io.FileInputStream

object SslContextBuilder {

  def build(): SSLContext = {
    //val trustStore = System.getProperty("user.dir") + "/Users/racit/cassandra_truststore.jks"
    val trustStore = "/Users/racit/cassandra_truststore.jks"

    val ks = KeyStore.getInstance("JKS")
    ks.load(new FileInputStream(trustStore), "changeit".toCharArray)

    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
    tmf.init(ks)

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, tmf.getTrustManagers, null)
    sslContext
  }
}
