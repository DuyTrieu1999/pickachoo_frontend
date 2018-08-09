package edu.cutie.lightbackend.domain

import io.requery.*

@Entity
interface Person: Persistable {
  @get:Key @get:Generated var id: Int
  @get:Column(unique = true, index = true, nullable = false)
  var email: String

  @get:Column(index = true, unique = true)
  var fbId: Long
  var name: String
}
