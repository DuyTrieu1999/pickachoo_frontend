package edu.cutie.lightbackend.config

object EnvConfig {
  val FRONTEND_BASE_URL = System.getenv("FRONTEND_BASE_URL")
  val BASE_URL = System.getenv("BASE_URL") ?: "http://localhost:8080"
  val JDBC_DATABASE_URL = System.getenv("JDBC_DATABASE_URL")
}
