package co.gargoyle.supercab.android.network;

import java.io.IOException;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import android.util.Log;
import co.gargoyle.supercab.android.model.Fare;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

public class FareRepresentation extends JacksonRepresentation<Fare> {

  private static final String TAG = "FareRepresentation";

  public FareRepresentation(Representation representation) {
    super(representation, Fare.class);

    ObjectMapper mapper = getObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public Optional<Fare> getFare() {
    try {
      String text = getText();
      Log.d(TAG, "JSON Text was: " + text);
      
      Fare fare = getObjectMapper().reader(Fare.class).readValue(text);
      
      
      if (fare == null) {
        return Optional.absent();
      } else {
        return Optional.of(fare);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Optional.absent();

  }

}
