package co.gargoyle.supercab.android;

import java.io.BufferedReader;
import java.io.FileReader;
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
  
  public static String readFileAsString(String filePath)
      throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
            new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
          String readData = String.valueOf(buf, 0, numRead);
          fileData.append(readData);
          buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
      }

}
