package edu.cutie.lightbackend.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.requery.Converter;
import io.requery.Nullable;

public class StringListConverter implements Converter<List<String>, String> {
  private static final String SEPARATOR = "\0007";

  @Override
  public Class<List<String>> getMappedType() {
    //noinspection unchecked
    return (Class) List.class;
  }

  @Override
  public Class<String> getPersistedType() {
    return String.class;
  }

  @Nullable
  @Override
  public Integer getPersistedSize() {
    return null;
  }

  @Override
  public String convertToPersisted(List<String> list) {
    if (list == null) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    int size = list.size();
    int index = 0;
    for (String s : list) {
      ++index;
      sb.append(s);
      if (index < size) sb.append(SEPARATOR);
    }

    return sb.toString();
  }

  @Override
  public List<String> convertToMapped(Class<? extends List<String>> type, String value) {
    return (value == null) ? Collections.emptyList() : Arrays.asList(value.split(SEPARATOR));
  }
}
