package edu.cutie.lightbackend.domain

import edu.cutie.lightbackend.converter.StringListConverter
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
  var privateLevel: Int
  @get:Convert(StringListConverter::class)
  var tags: List<String>
  @get:Column(value = "now()")
  var createdAt: Timestamp
}
