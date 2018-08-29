package edu.cutie.lightbackend.helper

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

abstract class Controller(router: Router, endpoint: String, private val ITEM_PER_PAGE: Int = 250) {
  init {
    router.post("$endpoint/create").coroutineHandler { create(it) }
    router.put("$endpoint/update").coroutineHandler { update(it) }
    router.get("$endpoint/:id").coroutineHandler { getOne(it) }
    router.get("$endpoint/").coroutineHandler {
      listAll(it, it.queryParam("limit").firstOrNull()?.toInt()
        ?: ITEM_PER_PAGE, it.queryParam("offset").firstOrNull()?.toInt() ?: 0)
    }
    router.delete("$endpoint/:id").coroutineHandler { delete(it) }
  }

  open suspend fun create(context: RoutingContext) = notImplementedResponse(context)
  open fun update(context: RoutingContext) = notImplementedResponse(context)
  open fun getOne(context: RoutingContext) = notImplementedResponse(context)
  open fun listAll(context: RoutingContext, limit: Int, offset: Int) = notImplementedResponse(context)
  open fun delete(context: RoutingContext) = notImplementedResponse(context)

  private fun notImplementedResponse(context: RoutingContext) =
    context.response().endWithJson("Not implemented", HttpResponseStatus.NOT_IMPLEMENTED)
}
