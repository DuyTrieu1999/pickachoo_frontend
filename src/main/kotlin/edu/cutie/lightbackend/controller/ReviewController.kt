package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.*
import edu.cutie.lightbackend.helper.Controller
import edu.cutie.lightbackend.helper.WithLogger
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.helper.toMap
import io.requery.kotlin.Offset
import io.requery.kotlin.eq
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ReviewController(router: Router, endpoint: String = "/review") : Controller(router, endpoint), WithLogger {
  init {
    router.get("$endpoint/product/:id").handler { findByProductId(it) }
    router.get("$endpoint/user/:uid").handler { findByUserId(it) }
  }

  private val fields = arrayOf(PersonEntity.NAME, ReviewEntity.COMMENT, ReviewEntity.DIFFICULTY_SCORE, ReviewEntity.SCORE)

  override suspend fun create(context: RoutingContext) {
    // val user = context.getUserDetail() TODO: Add auth logic
    val review = context.bodyAsJson.mapTo(ReviewEntity::class.java).apply {
      // fromUser = user.userId
    }
    val p = data.withTransaction {
      insert(review)
      val p = select(ProductEntity::class).where(ProductEntity.ID eq review.toProduct).get().first().apply {
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
    val result = data
      .select(*fields)
      .join(PersonEntity::class).on(ReviewEntity.FROM_USER eq PersonEntity.ID)
      .where(ReviewEntity.TO_PRODUCT eq productId).get()
      .map { t -> t.toMap(fields.filterNot { it == PersonEntity.NAME && t[ReviewEntity.PRIVATE_LEVEL] > 0 }.toTypedArray()) }
    context.response().endWithJson(result)
  }

  private fun findByUserId(context: RoutingContext) {
    val userId = context.pathParam("uid").toInt()
    val result = data
      .select(*fields)
      .join(PersonEntity::class).on((ReviewEntity.FROM_USER eq PersonEntity.ID) and (ReviewEntity.PRIVATE_LEVEL eq 0))
      .where(ReviewEntity.FROM_USER eq userId).get().map { it.toMap(fields) }
    context.response().endWithJson(result)
  }

  override fun listAll(context: RoutingContext, limit: Int, offset: Int) {
    val order = if (context.queryParam("desc").isEmpty()) ReviewEntity.SCORE else ReviewEntity.SCORE.desc()
    val reviews = data.select(ReviewEntity::class).orderBy(order).limit(limit).offset(offset).get().toList()
    context.response().endWithJson(reviews)
  }
}
