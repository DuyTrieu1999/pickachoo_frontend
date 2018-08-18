package edu.cutie.lightbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.requery.*
import java.sql.Timestamp

enum class ProductType {
  PROFESSOR, CLASS, SCHOOL
}

@Entity
@JsonIgnoreProperties("score", "difficulty", "reviews", "createdAt", allowGetters = true)
interface Product: Persistable {
  @get:Key @get:Generated var id: Int
  @get:Column(nullable = false) var name: String

  @get:Column(value = "'Toán'")
  var department: String

  @get:Column(nullable = false, index = true, value = "'PROFESSOR'")
  var type: ProductType

  var description: String?
  var address: String?
  var links: String?

  var point: String // WKT to store location may be

  var gradeFrom: Int // range
  var gradeTo: Int

  var picture: String?

  @get:Column(value = "50.0")
  var score: Double
  @get:Column(value = "50.0")
  var difficulty: Double

  var reviews: Int

  @get:Column(value = "now()")
  var createdAt: Timestamp
}

fun ProductEntity.validate() = gradeFrom >= 3 && gradeTo <= 13
  && department.length < 20 && (description?.length ?: 0) < 500
