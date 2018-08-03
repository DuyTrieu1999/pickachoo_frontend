package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.*
import edu.cutie.lightbackend.helper.Controller
import edu.cutie.lightbackend.helper.WithLogger
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.helper.toMap
import io.requery.kotlin.eq
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ReviewController(router: Router, endpoint: String = "/review") : Controller(router, endpoint), WithLogger {
  init {
    router.get("$endpoint/product/:id").handler { findByProductId(it) }
    router.get("$endpoint/user/:uid").handler { findByUserId(it) }
  }

  override fun create(context: RoutingContext) {
    // val user = context.getUserDetail() TODO: Add auth logic
    val review = context.bodyAsJson.mapTo(ReviewEntity::class.java).apply {
      // fromUser = user.userId
    }
    val p = data.withTransaction {
      insert(review)
      val p = select(Product::class).where(Product::id eq review.toProduct).get().first().apply {
        difficulty = 1.0 * reviews / (reviews + 1) * difficulty + review.difficultyScore / (reviews + 1)
        score = 1.0 * reviews / (reviews + 1) * score + review.score / (reviews + 1)
        reviews++
      }
      update(p)
    }
    context.response().endWithJson(p)
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

  private fun findByUserId(context: RoutingContext) {
    val userId = context.pathParam("uid").toInt()
    val fields = arrayOf(PersonEntity.USERNAME, ReviewEntity.COMMENT, ReviewEntity.DIFFICULTY_SCORE, ReviewEntity.SCORE)
    val result = data
      .select(*fields)
      .join(PersonEntity::class).on(ReviewEntity.FROM_USER eq PersonEntity.ID)
      .where(ReviewEntity.FROM_USER eq userId).get().map { it.toMap(fields) }
    context.response().endWithJson(result)
  }

  override fun listAll(context: RoutingContext, page: Int) {
    val order = if (context.queryParam("desc").isEmpty()) ReviewEntity.SCORE else ReviewEntity.SCORE.desc()
    val reviews = data.select(Review::class).orderBy(order).limit(ITEM_PER_PAGE).offset(ITEM_PER_PAGE * page).get().toList()
    context.response().endWithJson(reviews)
  }
}
