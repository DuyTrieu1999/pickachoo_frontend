package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.helper.coroutineHandler
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.service.elasticsearchClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import mbuhot.eskotlin.query.fulltext.multi_match
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.search.builder.SearchSourceBuilder


// Being a service is better. But controller works for now.

class SearchController(router: Router, endpoint: String = "/search") {
  init {
    router.get(endpoint).coroutineHandler { search(it) }
  }

  private fun search(context: RoutingContext) {
    val q = context.queryParam("q").firstOrNull() ?: "Quang"
    val query = multi_match {
      query = q
      fields = listOf("name^3", "description")
    }
    val searchSourceBuilder = SearchSourceBuilder().query(query)
    val searchRequest = SearchRequest("product").source(searchSourceBuilder)
    val response = elasticsearchClient.search(searchRequest)
    val hits = response.hits.hits
    context.response().endWithJson(hits)
  }
}
