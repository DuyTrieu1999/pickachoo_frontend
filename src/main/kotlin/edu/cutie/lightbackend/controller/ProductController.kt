package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.ProductEntity
import edu.cutie.lightbackend.helper.Controller
import edu.cutie.lightbackend.helper.WithLogger
import edu.cutie.lightbackend.helper.endWithJson
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ProductController(router: Router): Controller(router, "/product"), WithLogger {
  override fun create(context: RoutingContext) { // TODO: add support for ReCaptcha
    logger.atInfo().log(context.bodyAsString)
    val p = context.bodyAsJson.mapTo(ProductEntity::class.java)
    val np = data.insert(p)
    context.response().endWithJson(np)
  }

  override fun listAll(context: RoutingContext, page: Int) {
    val p = data.select(ProductEntity::class).orderBy(ProductEntity.ID).limit(ITEM_PER_PAGE).offset(ITEM_PER_PAGE * page).get().toList()
    context.response().endWithJson(p)
  }

  override fun getOne(context: RoutingContext) {
    val id = context.pathParam("id").toInt()
    val p = data.select(ProductEntity::class).where(ProductEntity.ID eq id).limit(1).get().firstOrNull()
    if (p != null)
      context.response().endWithJson(p)
    else
      context.response().endWithJson("Not found", HttpResponseStatus.NOT_FOUND)
  }
}
