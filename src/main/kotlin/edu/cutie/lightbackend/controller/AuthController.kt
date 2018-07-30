package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.helper.coroutineHandler
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.service.UserDetail
import edu.cutie.lightbackend.service.getUserDetail
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext


class AuthController(router: Router, path: String = "/auth") {

  init {
    router.get("$path/getToken").coroutineHandler { getToken(it) }
    router.get("$path/me").coroutineHandler { getMe(it) }
  }

  private fun getToken(context: RoutingContext) { // TODO implement me with Oauth2 flow
    context.response().endWithJson(UserDetail(1).toJwt())
  }
  private fun getMe(context: RoutingContext) {
    context.response().endWithJson(context.getUserDetail())
  }
}
