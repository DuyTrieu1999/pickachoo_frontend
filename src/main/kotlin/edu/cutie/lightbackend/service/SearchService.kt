package edu.cutie.lightbackend.service

import edu.cutie.lightbackend.domain.ProductEntity
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient


// TODO: Will change to codegen and proxy pattern later
// Reading: https://github.com/hubrick/vertx-elasticsearch-service
interface SearchService {
  suspend fun putIfAbsent(product: ProductEntity)
}

val elasticsearchClient by lazy {
  RestHighLevelClient(
    RestClient.builder(
      HttpHost("localhost", 9200, "http"),
      HttpHost("localhost", 9201, "http")))
}
class DefaultSearchService : SearchService {
  override suspend fun putIfAbsent(product: ProductEntity) {
    launch(Vertx.currentContext().dispatcher()) {
      val indexRequest = with(product) {
        IndexRequest("product", "_doc", id.toString()).source(this)
      }
      elasticsearchClient.index(indexRequest)
    }
  }
}
