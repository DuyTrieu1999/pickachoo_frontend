package edu.cutie.lightbackend

import edu.cutie.lightbackend.domain.DepartmentEntity
import edu.cutie.lightbackend.helper.WithLogger

class CommandLineRunner : WithLogger {
  init {
    val departments = listOf(DepartmentEntity().apply { name = "Math" })
    println(data.insert(departments))
  }
}
