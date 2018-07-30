package edu.cutie.lightbackend.config

import java.util.*

object SecurityConfig {
  val facebookKey = System.getenv("FACEBOOK_ID")
  val facebookSecret = System.getenv("FACEBOOK_SECRET")
  val jdbcDatabaseUrl = System.getenv("JDBC_DATABASE_URL")
  val secretJwtKey = Base64.getEncoder().encodeToString((System.getenv("JWT_SECRET") ?: "hail Stalin").toByteArray())!!
}
