package edu.cutie.lightbackend.domain

import io.requery.*
import java.sql.Timestamp


@Entity
interface Review: Persistable {
  @get:Key @get:ForeignKey(references = Person::class)
  var fromUser: Int
  @get:Key @get:ForeignKey(references = Product::class)
  var toProduct: Int
  var score: Int
  var difficultyScore: Int
  var comment: String
  @get:Column(value = "now()")
  var createdAt: Timestamp
}
