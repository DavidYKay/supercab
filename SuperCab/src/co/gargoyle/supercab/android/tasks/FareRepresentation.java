package co.gargoyle.supercab.android.tasks;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import com.google.common.base.Optional;

import co.gargoyle.supercab.android.model.Fare;

public class FareRepresentation extends JacksonRepresentation<Fare> {

  public FareRepresentation(Representation representation) {
    super(representation, Fare.class);
  }

  public Optional<Fare> getFare() {
    
    Fare fare = getObject();
    if (fare == null) {
      return Optional.absent();
    } else {
      return Optional.of(fare);
    }
  }

}
