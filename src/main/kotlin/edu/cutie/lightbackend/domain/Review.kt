package edu.cutie.lightbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import edu.cutie.lightbackend.converter.StringListConverter
import io.requery.*
import java.sql.Timestamp


@Entity
@JsonIgnoreProperties("createdAt", allowGetters = true)
interface Review: Persistable {
  @get:Key @get:ForeignKey(references = Person::class)
  var fromUser: Int
  @get:Key @get:ForeignKey(references = Product::class)
  var toProduct: Int
  @get:Convert(StringListConverter::class)
  var coursesTaken: List<String>

  var score: Int
  var difficulty: Int
  var price: Int?
  var comment: String
  var privateLevel: Int
  @get:Convert(StringListConverter::class)
  var tags: List<String>
  @get:Column(value = "now()")
  var createdAt: Timestamp
}

fun ReviewEntity.validate() = privateLevel in (0..1) && tags.size <= 3 && score in (0..100) && difficulty in (0..100)
