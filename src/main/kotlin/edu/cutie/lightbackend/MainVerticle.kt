package edu.cutie.lightbackend

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.flogger.FluentLogger
import edu.cutie.lightbackend.controller.AuthController
import edu.cutie.lightbackend.controller.ProductController
import edu.cutie.lightbackend.controller.ReviewController
import edu.cutie.lightbackend.domain.*
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle


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

  private fun Router.createPublicEndpoints() {
    get("/api").handler { ctx ->
      ctx.response().end("lol")
    }
  }

  private fun Router.createEndpoints() {}

  override suspend fun start() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val router = createRouter()

    AuthController(router)
    ProductController(router)
    ReviewController(router)

    data.withTransaction {
      insert(ReviewEntity().apply {
        this.fromUser = insert(PersonEntity().apply { this.username = "Lol" }).id
        this.toProduct = insert(ProductEntity().apply {
          departmentId = insert(DepartmentEntity().apply { this.name = "Math" }).id
          name = "Quang"
        }).id
      })
    }

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
    route().handler(CorsHandler.create("http://localhost:8080|https://qtmx.netlify.com").allowCredentials(true))
    createPublicEndpoints()
    createEndpoints()
  }
}

fun main(args: Array<String>) {
  Vertx.vertx().deployVerticle(MainVerticle())
}
