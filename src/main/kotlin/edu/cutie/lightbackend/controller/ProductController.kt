package edu.cutie.lightbackend.controller

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.ProductEntity
import edu.cutie.lightbackend.domain.validate
import edu.cutie.lightbackend.helper.Controller
import edu.cutie.lightbackend.helper.WithLogger
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.service.SearchService
import io.netty.handler.codec.http.HttpResponseStatus
import io.requery.meta.AttributeDelegate
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.async
import kotlin.reflect.full.staticProperties

class ProductController(router: Router, private val searchService: SearchService) : Controller(router, "/product"), WithLogger {
  companion object {
    private val cloudinary = Cloudinary()
    private val orders = ProductEntity::class.staticProperties.associateBy({ it.name.toLowerCase() }, { it.get() as? AttributeDelegate<ProductEntity, Any> })
    // TODO: hacky reflection. use code generation or just type it out instead
  }

  override suspend fun create(context: RoutingContext) { // TODO: add support for ReCaptcha
    val p = context.bodyAsJson.mapTo(ProductEntity::class.java)
    if (!p.validate()) {
      context.response().endWithJson("Validation Failed", HttpResponseStatus.BAD_REQUEST)
      logger.atWarning().log("Validation failed for product %s", p)
      return
    }
    val response = async {
      cloudinary.uploader().upload(p.picture, ObjectUtils.emptyMap())
    }
    p.picture = response.await()["secure_url"].toString()
    val np = data.insert(p)
    searchService.putIfAbsent(np)
    context.response().endWithJson(np)
  }

  override fun listAll(context: RoutingContext, limit: Int, offset: Int) {
    val order = context.queryParam("order").mapNotNull(String::toLowerCase).mapNotNull {
      if (it.first() == '-') orders[it.substring(1)]?.desc() else orders[it]
    }
    val p = data.select(ProductEntity::class).orderBy(*order.toTypedArray()).limit(limit).offset(offset).get().toList()
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
