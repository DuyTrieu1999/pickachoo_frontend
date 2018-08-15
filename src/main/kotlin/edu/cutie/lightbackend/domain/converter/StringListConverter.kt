package edu.cutie.lightbackend.domain.converter

import io.requery.Converter

internal class StringListConverter : Converter<List<String>, String> {
  override fun getMappedType(): Class<List<String>> = List::class.java as Class<List<String>>

  override fun getPersistedType(): Class<String> = String::class.java

  override fun getPersistedSize(): Int? = null

  override fun convertToPersisted(value: List<String>?): String = value?.joinToString { it.replace(",", "") } ?: ""

  override fun convertToMapped(type: Class<out List<String>>, value: String?): List<String> =
    value?.split(",".toRegex())?.dropLastWhile(String::isEmpty) ?: emptyList()
}
