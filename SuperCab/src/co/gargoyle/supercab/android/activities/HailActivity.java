package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import co.gargoyle.supercab.android.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class HailActivity extends RoboMapActivity {

  private static final String t = "HailActivity";

  @InjectView(R.id.hail_button) private Button mHailButton;
  @InjectView(R.id.map) private MapView mMapView;

  private MyLocationOverlay mMyLocationOverlay;
  private MapController mMapController;

  private Location mLastKnownLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(t, "Starting up, creating directories");

    setContentView(R.layout.hail);

    mMapController = mMapView.getController();
    mMapController.setZoom(20); //Fixed Zoom Level

    mMyLocationOverlay = new MyLocationOverlay(this, mMapView) {
      @Override
      public synchronized void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        centerMapOnLocation(location);
      }
    };
    mMapView.getOverlays().add(mMyLocationOverlay);

    centerMapOnLastKnownLocation();
  }

  ////////////////////////////////////////////////////////////
  // Overrides
  ////////////////////////////////////////////////////////////

  @Override
  protected void onPause() {
    super.onPause();

    mMyLocationOverlay.disableMyLocation();
    mMyLocationOverlay.disableCompass();
  }

  @Override
  protected void onResume() {
    super.onResume();

    mMyLocationOverlay.enableMyLocation();
    mMyLocationOverlay.enableCompass();
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

  ////////////////////////////////////////////////////////////
  // Button Callbacks
  ////////////////////////////////////////////////////////////


  //  @Override
  //  protected boolean isLocationDisplayed() {
  //    return true;
  //  }

  public void onHailButtonClicked(View view) {

  }

  public void onLocateButtonClicked(View view) {

  }


  ////////////////////////////////////////////////////////////
  // Private Methods
  ////////////////////////////////////////////////////////////

  private void centerMapOnLastKnownLocation() {
    centerMapOnLocation(
        mMyLocationOverlay.getLastFix()
        );
  }

  private static final String LOCATION_TAG = "location";

  private void centerMapOnLocation(Location location) {
    if (isValidLocation(location)) {
      if (isBetterLocation(location, mLastKnownLocation)) {
        Log.d(LOCATION_TAG, "centerMapOnLocation:" + locationToString(location));
        mLastKnownLocation = location;
        mMapController.setCenter(locationToGeoPoint(location));
      } else {
        Log.d(LOCATION_TAG, "poor location fix" + locationToString(location));
      }
    } else {
      Log.d(LOCATION_TAG, "invalid location" + locationToString(location));

    }
  }

  private boolean isValidLocation(Location location) {
    if (location == null) {
      return false;
    }

    if (location.getProvider() == null) {
      return false;
    }
    if (
        location.getLatitude() == 0 &&
        location.getLongitude() == 0
       ) {
      return false;
    }
    return true;
  }

  private GeoPoint locationToGeoPoint(Location location) {

    //double latitude = location.getLatitude();
    //double longitude = location.getLongitude();
    int latitude = (int) (location.getLatitude()  / 1E6);
    int longitude = (int) (location.getLongitude() / 1E6);

    GeoPoint geoPoint = new GeoPoint(latitude, longitude);

    return geoPoint;
  }

  ////////////////////////////////////////////////////////////
  // Geo Utils
  ////////////////////////////////////////////////////////////

  private static final int TWO_MINUTES = 1000 * 60 * 2;

  /** Determines whether one Location reading is better than the current Location fix
   * @param location  The new Location that you want to evaluate
   * @param currentBestLocation  The current Location fix, to which you want to compare the new one
   */
  protected boolean isBetterLocation(Location location, Location currentBestLocation) {
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

  private String locationToString(Location location) {
    if (location == null) {
      return "null";
    } else {
      return location.toString();
    }
  }

}
