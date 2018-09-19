package edu.cutie.lightbackend.helper

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json

fun HttpServerResponse.endWithJson(obj: Any? = null, code: HttpResponseStatus = HttpResponseStatus.OK) =
  setStatusCode(code.code())
    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.createOptimized("application/json; charset=utf-8"))
    .end(Json.encode(ResponseWithCode(obj, code.code())))

private data class ResponseWithCode(val content: Any? = null, val code: Int = HttpResponseStatus.OK.code())
