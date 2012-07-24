package co.gargoyle.supercab.android;

import java.util.Locale;

import android.location.Address;

public class Constants {

  public static final String DK_PHONE_NUMBER = "+254727114825";
  public static final String NEXMO_PHONE_NUMBER = "+447559600567";
  
  public static final int FEB_13_2009 = 1234567890;
  public static final int FEB_14_2009 = 1234591200;
  
  public static final int ONE_MINUTE_SECONDS = 60;

  public static Address ADDRESS_IHUB = new Address(Locale.getDefault());
  public static Address ADDRESS_AIRPORT = new Address(Locale.getDefault());

  static {
    ADDRESS_IHUB.setLatitude(-1.29885); 
    ADDRESS_IHUB.setLongitude(36.79089);
    ADDRESS_IHUB.setAddressLine(0,"iHub");

    ADDRESS_AIRPORT.setLatitude(-1.331);
    ADDRESS_AIRPORT.setLongitude(36.9251);
    ADDRESS_AIRPORT.setAddressLine(0, "Jomo Kenyatta Airport");
  }

}
