package co.gargoyle.supercab.android;

import java.util.Date;

import android.location.Address;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;

public class FixtureFactory {

  public static PickupPoint createExamplePickupPoint(FareType type) {
    Address address;
    if (type == FareType.PICKUP) {
      address  = Constants.ADDRESS_IHUB;
    } else {
      address = Constants.ADDRESS_AIRPORT;
    }
    PickupPoint pickup = new PickupPoint(type, address);
    return pickup;
  }

  public static Fare createExampleFare() {
    Date time = new Date(Constants.FEB_13_2009);

    Fare fare = new Fare(
        createExamplePickupPoint(FareType.PICKUP),
        createExamplePickupPoint(FareType.DROPOFF),
        time
    );
    return fare;
  }
}
