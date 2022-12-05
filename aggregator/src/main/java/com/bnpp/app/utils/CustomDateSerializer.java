package com.bnpp.app.utils;

import static com.bnpp.app.utils.JsonUtil.DATE_TIME_FORMATTER;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDateTime;

public class CustomDateSerializer extends StdSerializer<LocalDateTime> {

  public CustomDateSerializer() {
    this(null);
  }

  public CustomDateSerializer(Class<LocalDateTime> t) {
    super(t);
  }

  @Override
  public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider arg2)
      throws IOException {
    gen.writeString(DATE_TIME_FORMATTER.format(value));
  }
}
