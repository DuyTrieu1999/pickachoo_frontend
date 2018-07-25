package edu.cutie.lightbackend.domain

import io.requery.*

enum class ProductType {
  PROFESSOR, CLASS, SCHOOL
}

@Entity
interface Product: Persistable {
  @get:Key @get:Generated var id: Int
  @get:Column(nullable = false) var name: String
  //@get:ForeignKey(references = Department::class, referencedColumn = "id") var departmentId: Int

  //@get:Column(nullable = false, index = true)
  //var type: ProductType

  // we will want to cache this later
  // var review: Double
}
