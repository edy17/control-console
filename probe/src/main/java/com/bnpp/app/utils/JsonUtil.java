package com.bnpp.app.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class JsonUtil {

  public static final DateTimeFormatter K8S_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss'Z'");

  public static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");

  private JsonUtil() {
    throw new UnsupportedOperationException();
  }
}
