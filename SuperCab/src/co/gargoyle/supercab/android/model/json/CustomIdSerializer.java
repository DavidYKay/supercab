package co.gargoyle.supercab.android.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomIdSerializer extends JsonSerializer<String> {

  public CustomIdSerializer() {
    super();
  }

  @Override
  public void serialize(String value, JsonGenerator jgen, SerializerProvider provider)
  throws IOException, JsonGenerationException {
    if (value == null) {
      // nothing to do here
    } else {
      jgen.writeString(value);
    }
  }

}
