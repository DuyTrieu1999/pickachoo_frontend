package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.PersonEntity
import edu.cutie.lightbackend.helper.Controller
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class UserController(router: Router): Controller(router, "/user") {
  override fun create(context: RoutingContext) {
    val user = context.bodyAsJson.mapTo(PersonEntity::class.java)
    data.insert(user)
  }
}
