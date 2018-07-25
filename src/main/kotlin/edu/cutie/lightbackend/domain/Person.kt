package edu.cutie.lightbackend.domain

import io.requery.*

@Entity
interface Person: Persistable {
  @get:Key @get:Generated var id: Int
  @get:Column(unique = true, index = true, nullable = false) var username: String
}
