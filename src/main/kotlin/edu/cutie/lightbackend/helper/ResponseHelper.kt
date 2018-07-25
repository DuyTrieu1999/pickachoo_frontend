package edu.cutie.lightbackend.helper

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json

fun HttpServerResponse.endWithJson(obj: Any?, code: HttpResponseStatus = HttpResponseStatus.OK) {
  this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(ResponseWithCode(obj, code.code())))
}

private data class ResponseWithCode(val content: Any? = null, val code: Int = HttpResponseStatus.OK.code())
