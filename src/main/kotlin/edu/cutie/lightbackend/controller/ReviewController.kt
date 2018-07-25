package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.ReviewEntity
import edu.cutie.lightbackend.helper.Controller
import edu.cutie.lightbackend.helper.endWithJson
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ReviewController(router: Router) : Controller(router, "/review") {
  override fun create(context: RoutingContext) {
    val review = context.bodyAsJson.mapTo(ReviewEntity::class.java)
    context.response().endWithJson(data.insert(review))
  }

  override fun listAll(context: RoutingContext, page: Int) {
    val order = if (context.queryParam("desc").isEmpty()) ReviewEntity.SCORE else ReviewEntity.SCORE.desc()
    val reviews = data.select(ReviewEntity::class).orderBy(order).limit(ITEM_PER_PAGE).offset(ITEM_PER_PAGE * page).get().toList()
    context.response().endWithJson(reviews)
  }
}
