package edu.cutie.lightbackend.domain

import io.requery.*


@Entity
interface Review: Persistable {
  //@get:Key @get:ForeignKey(references = Person::class) var from: Int
  //@get:Key @get:ForeignKey(references = Product::class) var to: Int
  var score: Int
  var comment: String
}
