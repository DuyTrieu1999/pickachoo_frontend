package edu.cutie.lightbackend.service

import com.google.common.flogger.FluentLogger
import edu.cutie.lightbackend.config.SecurityConfig
import edu.cutie.lightbackend.helper.endWithJson
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import org.joda.time.DateTime


enum class Role(val id: Int) {
  ADMIN(0), MODERATOR(1), USER(2)
}

private val logger = FluentLogger.forEnclosingClass()

fun RoutingContext.getUserDetail(): UserDetail = try {
  val claims = Jwts.parser()
    .setSigningKey(SecurityConfig.secretJwtKey)
    .parseClaimsJws(request().getHeader(HttpHeaders.AUTHORIZATION))
    .body
  with(claims) {
    UserDetail(get("id") as Int, Role.valueOf(get("role").toString()))
  }
} catch (e: Exception) {
  logger.atWarning().log(request().remoteAddress().host() + " has invalid jwt")
  response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code()).endWithJson(HttpResponseStatus.UNAUTHORIZED.reasonPhrase(), HttpResponseStatus.UNAUTHORIZED)
  throw e
}

data class UserDetail(val userId: Int, val role: Role = Role.USER) {
  fun toJwt(): String = Jwts.builder()
    .claim("id", userId)
    .claim("role", role)
    .setExpiration(DateTime.now().plusDays(2).toDate())
    .signWith(SignatureAlgorithm.HS512, SecurityConfig.secretJwtKey)
    .compact()
}
