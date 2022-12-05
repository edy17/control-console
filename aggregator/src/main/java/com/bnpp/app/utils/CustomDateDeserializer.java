package com.bnpp.app.utils;

import static com.bnpp.app.utils.JsonUtil.DATE_TIME_FORMATTER;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;

public class CustomDateDeserializer extends StdDeserializer<LocalDateTime> {

  public CustomDateDeserializer() {
    this(null);
  }

  public CustomDateDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
    String date = jsonparser.getText();
    return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
  }
}
