package edu.cutie.lightbackend

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.flogger.FluentLogger
import edu.cutie.lightbackend.controller.AuthController
import edu.cutie.lightbackend.controller.ProductController
import edu.cutie.lightbackend.controller.ReviewController
import edu.cutie.lightbackend.controller.SearchController
import edu.cutie.lightbackend.domain.Models
import edu.cutie.lightbackend.domain.PersonEntity
import edu.cutie.lightbackend.domain.ProductEntity
import edu.cutie.lightbackend.service.DefaultSearchService
import edu.cutie.lightbackend.service.DefaultUserService
import edu.cutie.lightbackend.service.UserService
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
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.client.WebClientOptions


val data: KotlinEntityDataStore<Persistable> by lazy {
  val config = JsonObject()
    .put("jdbcUrl", "jdbc:hsqldb:mem:test?shutdown=true")
    .put("driverClassName", "org.hsqldb.jdbcDriver")
    .put("maximumPoolSize", 10)
  val source = HikariCPDataSourceProvider().getDataSource(config)
  SchemaModifier(source, Models.DEFAULT).createTables(TableCreationMode.DROP_CREATE)
  KotlinEntityDataStore<Persistable>(KotlinConfiguration(Models.DEFAULT, source, useDefaultLogging = true))
}

private val logger = FluentLogger.forEnclosingClass()

class MainVerticle : CoroutineVerticle() {
  private val userService: UserService = DefaultUserService()
  override suspend fun start() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val router = createRouter()
    val options = WebClientOptions(userAgent = "XemThay/1.2.3", logActivity = true)
    val webClient = WebClient.create(vertx, options)


    val searchService = DefaultSearchService()
    AuthController(router, webClient, userService)
    ProductController(router, searchService)
    ReviewController(router)
    SearchController(router)

    CommandLineRunner()
    data.withTransaction {
      val product = insert(ProductEntity().apply { name = "Prof" })
      val person = insert(PersonEntity().apply {
        name = "Quang"
        email = "quang@gmail.com"
      })
      /*
      insert(ReviewEntity().apply {
        fromUser = person.id
        toProduct = product.id
      })
      */
    }

    Json.mapper.apply {
      registerModules(KotlinModule(), JodaModule())
      configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
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
    route().handler(CorsHandler.create("http:\\/\\/localhost:[[0-9]+|https:\\/\\/qtmx\\.netlify\\.com|https:\\/\\/pickachoo\\.devel\\.faith|https:\\/\\/\\w+--qtmx\\.netlify\\.com")
      .allowCredentials(true)
    )
  }
}

fun main(args: Array<String>) {
  Vertx.vertx().deployVerticle(MainVerticle())
}
