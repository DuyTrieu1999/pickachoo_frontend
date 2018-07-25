package edu.cutie.lightbackend

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.flogger.FluentLogger
import edu.cutie.lightbackend.config.EnvConfig
import edu.cutie.lightbackend.controller.AuthController
import edu.cutie.lightbackend.controller.ProductController
import edu.cutie.lightbackend.domain.Models
import edu.cutie.lightbackend.helper.coroutineHandler
import edu.cutie.lightbackend.helper.endWithJson
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.auth.KeyStoreOptions
import io.vertx.kotlin.ext.auth.jwt.JWTAuthOptions
import io.vertx.kotlin.ext.auth.jwt.JWTOptions

val data: KotlinEntityDataStore<Persistable> by lazy {
  val config = JsonObject()
    .put("jdbcUrl", "jdbc:hsqldb:mem:test?shutdown=true")
    .put("driverClassName", "org.hsqldb.jdbcDriver")
    .put("maximumPoolSize", 10)
  val source = HikariCPDataSourceProvider().getDataSource(config)
  SchemaModifier(source, Models.DEFAULT).createTables(TableCreationMode.DROP_CREATE)
  KotlinEntityDataStore<Persistable>(KotlinConfiguration(Models.DEFAULT, source))
}

class MainVerticle : CoroutineVerticle() {
  private val logger = FluentLogger.forEnclosingClass()

  private val jwtConfig = JWTAuthOptions( // TODO: IMPORTANT, use SystemEnv
    keyStore = KeyStoreOptions(path = "/home/quang/IdeaProjects/lightbackend/keystore.jceks", password = "secret"))

  private val provider: JWTAuth by lazy { JWTAuth.create(vertx, jwtConfig) }

  private fun Router.createPublicEndpoints() {
    get("/api").handler { ctx ->
      ctx.response().end("lol")
    }
  }

  private fun Router.createEndpoints() {
    get("/public/newToken").coroutineHandler { ctx ->
      ctx.response().putHeader("Content-Type", "text/plain")
      ctx.response().end(provider.generateToken(JsonObject("username" to "Quang"), JWTOptions(expiresInMinutes = 1)))
    }
    get("/api/protected").handler { ctx ->
      ctx.response().endWithJson(ctx.user().principal())
    }
  }

  override suspend fun start() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val router = createRouter()

    ProductController(router)

    Json.mapper.registerModule(KotlinModule())
    Json.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    vertx.createHttpServer().apply {
      requestHandler(router::accept)
    }.listen(port) { res ->
      if (res.failed()) {
        res.cause().printStackTrace()
      } else {
        logger.atInfo().log("Server started with port $port")
      }
    }
  }

  private fun createRouter() = Router.router(vertx).apply {
    // route().handler(CookieHandler.create())
    // route().handler(SessionHandler.create(sessionStore))

    route().handler(BodyHandler.create())
    route().handler(StaticHandler.create())
    route().handler(CorsHandler.create("http://localhost:8081").allowCredentials(true))
    createPublicEndpoints()
    // route().handler(JWTAuthHandler.create(provider, EnvConfig.PUBLIC_PATH))
    createEndpoints()

  }
}
