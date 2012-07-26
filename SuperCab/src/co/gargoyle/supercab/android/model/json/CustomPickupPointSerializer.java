package co.gargoyle.supercab.android.model.json;

import java.io.IOException;

import android.location.Address;
import co.gargoyle.supercab.android.model.PickupPoint;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomPickupPointSerializer extends JsonSerializer<PickupPoint> {

  @Override
  public void serialize(PickupPoint pickupPoint, JsonGenerator generator, SerializerProvider provider)
      throws IOException, JsonProcessingException {

    Address address = pickupPoint.getAddress();
        // latitude
    generator.writeStartObject();
    generator.writeNumberField("lat", address.getLatitude());
    generator.writeNumberField("lon", address.getLongitude());
    generator.writeStringField("address", address.getAddressLine(0));
    //latitude = models.FloatField()
    //longitude = models.FloatField()
    //address = models.TextField()
    generator.writeEndObject();
  }

}
