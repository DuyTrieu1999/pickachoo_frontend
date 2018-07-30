package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.PersonEntity
import edu.cutie.lightbackend.domain.ReviewEntity
import edu.cutie.lightbackend.helper.Controller
import edu.cutie.lightbackend.helper.WithLogger
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.helper.toMap
import edu.cutie.lightbackend.service.getUserDetail
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ReviewController(router: Router, endpoint: String = "/review") : Controller(router, endpoint), WithLogger {
  init {
      router.get("$endpoint/product/:id").handler { findByProductId(it) }
  }

  override fun create(context: RoutingContext) {
    val user = context.getUserDetail()
    val review = context.bodyAsJson.mapTo(ReviewEntity::class.java).apply {
      // fromUser = user.userId
    }
    context.response().endWithJson(data.insert(review))
  }

  private fun findByProductId(context: RoutingContext) {
    val productId = context.pathParam("id").toInt()
    val fields = arrayOf(PersonEntity.USERNAME, ReviewEntity.COMMENT, ReviewEntity.DIFFICULTY_SCORE, ReviewEntity.SCORE)
    val result = data
      .select(*fields)
      .join(PersonEntity::class).on(ReviewEntity.FROM_USER eq PersonEntity.ID)
      .where(ReviewEntity.TO_PRODUCT eq productId).get().map { it.toMap(fields) }
    context.response().endWithJson(result)
  }

  override fun listAll(context: RoutingContext, page: Int) {
    val order = if (context.queryParam("desc").isEmpty()) ReviewEntity.SCORE else ReviewEntity.SCORE.desc()
    val reviews = data.select(ReviewEntity::class).orderBy(order).limit(ITEM_PER_PAGE).offset(ITEM_PER_PAGE * page).get().toList()
    context.response().endWithJson(reviews)
  }
}
