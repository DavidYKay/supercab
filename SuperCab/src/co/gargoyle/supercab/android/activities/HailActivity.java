package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.inject.Inject;

public class HailActivity extends RoboMapActivity {

  private static final String t = "HailActivity";

  @InjectView(R.id.hail_button) private Button mHailButton;
  @InjectView(R.id.map) private MapView mMapView;
  
  @Inject private GeoUtils mGeoUtils;

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
      if (mGeoUtils.isBetterLocation(location, mLastKnownLocation)) {
        Log.d(LOCATION_TAG, "centerMapOnLocation:" + mGeoUtils.locationToString(location));
        mLastKnownLocation = location;
        mMapController.setCenter(mGeoUtils.locationToGeoPoint(location));
      } else {
        Log.d(LOCATION_TAG, "poor location fix" + mGeoUtils.locationToString(location));
      }
    } else {
      Log.d(LOCATION_TAG, "invalid location" + mGeoUtils.locationToString(location));

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

}
