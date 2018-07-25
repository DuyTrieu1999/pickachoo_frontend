package edu.cutie.lightbackend.domain

import io.requery.*

@Entity
interface Department: Persistable {
  @get:Key
  @get:Generated
  var id: Int

  @get:Column(nullable = false)
  var name: String
}
