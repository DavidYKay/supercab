package co.gargoyle.supercab.android.model.json;

import java.io.IOException;

import android.location.Address;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomAddressSerializer extends JsonSerializer<Address> {

  @Override
  public void serialize(Address address, JsonGenerator generator, SerializerProvider provider)
      throws IOException, JsonProcessingException {

        // latitude
    generator.writeStartObject();
    generator.writeNumberField("latitude", address.getLatitude());
    generator.writeNumberField("longitude", address.getLongitude());
    generator.writeStringField("address", address.getAddressLine(0));
    //latitude = models.FloatField()
    //longitude = models.FloatField()
    //address = models.TextField()
    generator.writeEndObject();
  }

}
