package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.helper.WithLogger
import edu.cutie.lightbackend.helper.coroutineHandler
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.service.elasticsearchClient
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import mbuhot.eskotlin.query.compound.bool
import mbuhot.eskotlin.query.fulltext.multi_match
import mbuhot.eskotlin.query.term.range
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.search.builder.SearchSourceBuilder


// Being a service is better. But controller works for now.

class SearchController(router: Router, endpoint: String = "/search"): WithLogger {
  init {
    router.get(endpoint).coroutineHandler { search(it) }
  }

  // Sample http://localhost:8080/search?q=Quang&score=1&score=100&difficulty=2&difficulty=100
  private fun search(context: RoutingContext) {
    val q = context.queryParam("q").firstOrNull() ?: "Quang"
    val score = context.queryParam("score").take(2).map(String::toDouble)
    val difficulty = context.queryParam("difficulty").take(2).map(String::toDouble)
    val query = bool {
      must {
        multi_match {
          query = q
          fields = listOf("name^5", "description^2", "department^3", "address") // TODO: tweak this
        }
      }
      filter = listOf(
        range {
          "score" {
            from = score[0]
            to = score[1]
          }
        },
        range {
          "difficulty" {
            from = difficulty[0]
            to = difficulty[1]
          }
        }
      )
    }
    val searchSourceBuilder = SearchSourceBuilder().query(query)
    val searchRequest = SearchRequest("product").source(searchSourceBuilder)
    elasticsearchClient.searchAsync(searchRequest, ActionListener.wrap({ response ->
      val hits = response.hits.hits.map { it.sourceAsMap }
      context.response().endWithJson(hits)
    }, {
      logger.atWarning().withCause(it).log("Query %s failed", context.request().query())
      context.response().endWithJson(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase(), HttpResponseStatus.INTERNAL_SERVER_ERROR)
    }))
  }
}
