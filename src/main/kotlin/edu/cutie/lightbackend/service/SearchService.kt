package edu.cutie.lightbackend.service

import edu.cutie.lightbackend.domain.ProductEntity
import edu.cutie.lightbackend.helper.WithLogger
import io.vertx.core.json.Json
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType


// TODO: Will change to codegen and proxy pattern later
// Reading: https://github.com/hubrick/vertx-elasticsearch-service
interface SearchService {
  suspend fun putIfAbsent(product: ProductEntity)
}

val elasticsearchClient by lazy {
  val host: String = System.getenv("ELASTICSEARCH_HOST") ?: "localhost"
  val port: Int = System.getenv("ELASTICSEARCH_PORT")?.toInt() ?: 9200
  val scheme: String = System.getenv("ELASTICSEARCH_SCHEME") ?: "http"
  val username = System.getenv("ELASTICSEARCH_USER") ?: "elastic"
  val password = System.getenv("ELASTICSEARCH_PASSWORD")

  val credentialsProvider = BasicCredentialsProvider()
  credentialsProvider.setCredentials(AuthScope.ANY,
    UsernamePasswordCredentials(username, password))

  val builder = RestClient.builder(HttpHost(host, port, scheme))
    .setHttpClientConfigCallback { httpClientBuilder ->
      httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
    }
  RestHighLevelClient(builder)
}

class DefaultSearchService : SearchService, WithLogger {
  override suspend fun putIfAbsent(product: ProductEntity) {
    val indexRequest = with(product) {
      IndexRequest("product", "_doc", id.toString()).source(Json.encodePrettily(this), XContentType.JSON)
      /*
      .source("name", name,
        "department", department,
        "reviews", reviews
      )
      */

    }
    elasticsearchClient.indexAsync(indexRequest, ActionListener.wrap({
      logger.atInfo().log("Indexed %s", product)
    }, {
      logger.atWarning().withCause(it).log("Failed to index %s", product)
    }))
  }
}
