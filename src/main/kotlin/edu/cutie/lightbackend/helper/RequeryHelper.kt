package edu.cutie.lightbackend.helper

import io.requery.Persistable
import io.requery.meta.AttributeDelegate
import io.requery.query.Tuple

fun Tuple.toMap(fields: Array<AttributeDelegate<out Persistable, out Any>>) =
  fields.associateBy({ it.name }, { this[it] }).toMutableMap()
