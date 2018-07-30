package edu.cutie.lightbackend.helper

import io.requery.Persistable
import io.requery.meta.StringAttributeDelegate
import io.requery.query.Tuple

fun Tuple.toMap(fields: Array<StringAttributeDelegate<out Persistable, String>>): Map<String, Any> =
  fields.associateBy({ it.name }, { this[it] })
