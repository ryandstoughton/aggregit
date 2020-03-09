package com.stoughton.ryan.aggregit.gitlab;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class DateStringDeserializer extends JsonDeserializer<String> {

  /*
   * Parse date strings into YYYY-MM-DD format
   */
  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return p.getText().substring(0, 10);
  }
}
