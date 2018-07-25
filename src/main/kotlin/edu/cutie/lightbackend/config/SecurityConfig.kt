package edu.cutie.lightbackend.config

object SecurityConfig {
  val facebookKey = System.getenv("FACEBOOK_ID")
  val facebookSecret = System.getenv("FACEBOOK_SECRET")
}
