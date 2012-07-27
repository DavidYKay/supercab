package co.gargoyle.supercab.android.model.json;

import java.io.IOException;
import java.util.Locale;

import android.location.Address;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.model.PickupPoint;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class CustomPickupPointDeserializer extends JsonDeserializer<PickupPoint> {

  @Override
  public PickupPoint deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException,
      JsonProcessingException {
       
      ObjectCodec oc = jsonParser.getCodec();
      JsonNode node = oc.readTree(jsonParser);

      Address address = new Address(Locale.getDefault());

      address.setLatitude(node.get("lat").doubleValue());
      address.setLongitude(node.get("lon").doubleValue());
      address.setAddressLine(0, node.get("address").textValue());
      
      PickupPoint pickupPoint = new PickupPoint(PointType.UNKNOWN, address);

      return pickupPoint;
  }

}
