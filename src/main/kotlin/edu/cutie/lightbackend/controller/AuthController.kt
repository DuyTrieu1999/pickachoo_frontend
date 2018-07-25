package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.helper.coroutineHandler
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class AuthController(router: Router, path: String = "/auth") {

  init {
    router.get("$path/getToken").coroutineHandler { getToken(it) }
  }

  private fun getToken(context: RoutingContext) {

  }
}
