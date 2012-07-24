package co.gargoyle.supercab.android.model.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomDateSerializer extends JsonSerializer<Date> {

  public CustomDateSerializer() {
    super();
  }

  @Override
  public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonGenerationException {
    //SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'ZZZ (z)");
    //SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD HH:MM:ss.uuuuuu TZ");
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    String format = formatter.format(value);
    jgen.writeString(format);
  }

}
