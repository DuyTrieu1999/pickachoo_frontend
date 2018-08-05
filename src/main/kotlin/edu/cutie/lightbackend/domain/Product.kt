package edu.cutie.lightbackend.domain

import io.requery.*
import java.sql.Timestamp

enum class ProductType {
  PROFESSOR, CLASS, SCHOOL
}

@Entity
interface Product: Persistable {
  @get:Key @get:Generated var id: Int
  @get:Column(nullable = false) var name: String

  @get:Column(value = "'Toán'")
  var department: String

  //@get:Column(nullable = false, index = true)
  //var type: ProductType

  var description: String?
  var address: String?
  var links: String?

  @get:Column(value = "50.0")
  var score: Double
  @get:Column(value = "50.0")
  var difficulty: Double

  @get:Column(value = "0")
  var reviews: Int

  @get:Column(value = "now()")
  var createdAt: Timestamp
}
