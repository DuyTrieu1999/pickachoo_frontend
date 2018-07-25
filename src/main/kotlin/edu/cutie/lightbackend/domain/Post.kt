package edu.cutie.lightbackend.domain

import io.requery.Entity
import io.requery.Generated
import io.requery.Key
import io.requery.Persistable

//@Entity
interface Post: Persistable {
  @get:Key @get:Generated var id: Int
  var content: String
  var link: String
}
