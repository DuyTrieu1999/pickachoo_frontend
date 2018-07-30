package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.DepartmentEntity
import edu.cutie.lightbackend.helper.Controller
import edu.cutie.lightbackend.helper.endWithJson
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class DepartmentController(router: Router): Controller(router, "/department") {
  override fun listAll(context: RoutingContext, page: Int) {
    val departments = data.select(DepartmentEntity::class)
      .orderBy(DepartmentEntity.ID)
      .limit(ITEM_PER_PAGE)
      .offset(ITEM_PER_PAGE * page).get().toList()
    context.response().endWithJson(departments)
  }
}
