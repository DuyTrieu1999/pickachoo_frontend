package edu.cutie.lightbackend.controller

import edu.cutie.lightbackend.config.SecurityConfig
import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.PersonEntity
import edu.cutie.lightbackend.helper.WithLogger
import edu.cutie.lightbackend.helper.coroutineHandler
import edu.cutie.lightbackend.helper.endWithJson
import edu.cutie.lightbackend.service.UserDetail
import edu.cutie.lightbackend.service.UserService
import edu.cutie.lightbackend.service.getUserDetail
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientResponse
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.coroutines.awaitResult

class AuthController(router: Router,
                     private val webClient: WebClient,
                     private val userService: UserService,
                     endpoint: String = "/auth") : WithLogger {

  init {
    router.get("$endpoint/getToken").coroutineHandler { getToken(it) }
    router.get("$endpoint/me").coroutineHandler { getMe(it) }
  }

  private suspend fun getToken(context: RoutingContext) { // TODO code smell
    val accessToken = context.queryParam("access_token").first()
    val requestUri = "/debug_token?input_token=$accessToken&access_token=${SecurityConfig.facebookKey}|${SecurityConfig.facebookSecret}"

    val verifyResult = awaitResult<HttpResponse<Buffer>> {
      webClient.getAbs("https://graph.facebook.com$requestUri").send(it)
    }.bodyAsJsonObject().getJsonObject("data")

    if (verifyResult.getBoolean("is_valid")) {
      val uid = verifyResult.getString("user_id").toLong()
      val id = userService.findByFacebookId(uid)?.id ?: kotlin.run {
        val newUser = awaitResult<HttpResponse<Buffer>> {
          webClient.getAbs("https://graph.facebook.com/me?access_token=$accessToken&fields=email,name").send(it)
        }.bodyAsJsonObject()
        data.insert(PersonEntity().apply {
          email = newUser["email"]
          name = newUser["name"]
          fbId = newUser.getString("id").toLong()
        }).id
      }
      context.response().endWithJson(UserDetail(id).toJwt())
    } else {
      context.response().endWithJson("Invalid access token", HttpResponseStatus.UNAUTHORIZED)
    }
  }

  private fun getMe(context: RoutingContext) {
    context.response().endWithJson(context.getUserDetail())
  }
}
