package co.gargoyle.supercab.android.utilities;

import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.inject.Inject;

public class GeoUtils {

  @Inject
  public GeoUtils() {
  }
  
  private static final int TWO_MINUTES = 1000 * 60 * 2;

  /** Determines whether one Location reading is better than the current Location fix
   * @param location  The new Location that you want to evaluate
   * @param currentBestLocation  The current Location fix, to which you want to compare the new one
   */
  public boolean isBetterLocation(Location location, Location currentBestLocation) {
    if (currentBestLocation == null) {
      // A new location is always better than no location
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    // If it's been more than two minutes since the current location, use the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
      return true;
      // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
                                                currentBestLocation.getProvider());

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
      return true;
    }
    return false;
  }

  /** Checks whether two providers are the same */
  private boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }

  public String locationToString(Location location) {
    if (location == null) {
      return "null";
    } else {
      return location.toString();
    }
  }
  
  public Location geoPointToLocation(GeoPoint geoPoint) {
    double latitude = geoPoint.getLatitudeE6() / 1E6;
    double longitude = geoPoint.getLongitudeE6() / 1E6;

    Location location = new Location("GeoUtils");
    location.setLatitude(latitude);
    location.setLongitude(longitude);

    return location;
  }

  public GeoPoint locationToGeoPoint(Location location) {
    //double latitude = location.getLatitude();
    //double longitude = location.getLongitude();
    int latitude = (int) (location.getLatitude()  * 1E6);
    int longitude = (int) (location.getLongitude() * 1E6);

    GeoPoint geoPoint = new GeoPoint(latitude, longitude);

    return geoPoint;
  }


}
