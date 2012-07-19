package co.gargoyle.supercab.android.activities;

import java.io.IOException;
import java.util.List;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.map.ExtendedMapView;
import co.gargoyle.supercab.android.map.ExtendedMapView.OnMoveListener;
import co.gargoyle.supercab.android.map.XOverlay;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.inject.Inject;

public class HailActivity extends RoboMapActivity {

  private static final String t = "HailActivity";

  @InjectView(R.id.location_text) private TextView mAddressText;
  @InjectView(R.id.hail_button) private Button mHailButton;
  @InjectView(R.id.map) private ExtendedMapView mMapView;

  @Inject private GeoUtils mGeoUtils;

  private XOverlay mXOverlay;
  private MyLocationOverlay mMyLocationOverlay;
  private MapController mMapController;

  private Handler mHandler;

  private Location mLastKnownLocation;
  private boolean mHasGeolocated;

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
        if (!mHasGeolocated) {
          centerMapOnLocation(location);
          mHasGeolocated = true;
        }
      }
    };
    mXOverlay = new XOverlay(getXBitmap());

    mMapView.getOverlays().add(mMyLocationOverlay);
    mMapView.getOverlays().add(mXOverlay);

    mMapView.setOnMoveListener(new OnMoveListener() {
      public void onMove(MapView mapView, GeoPoint center, boolean stopped) {
        Log.d(t, String.format("onMove center: %s stopped: %b", center.toString(), stopped));
        if (stopped) {
          updateAddressWithGeoPoint(center);
        }
      }
    });

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

    mHandler = null;
  }

  @Override
  protected void onResume() {
    super.onResume();
    mHandler = new Handler();

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

  private void updateAddressWithGeoPoint(GeoPoint center) {
    Log.d("address", "updateAddressWithGeoPoint()");
    Address address = geoCodeNewPoint(center);

    String addressString = address.getAddressLine(0);
    Log.d("address", "current address text: " + mAddressText.getText().toString());
    Log.d("address", "new address text: " + addressString);
    //mAddressText.setText(addressString);
    updateAddressTextOnUiThread(addressString);
  }

  private void updateAddressTextOnUiThread(final CharSequence newText) {
    Runnable updateUITimerTask = new Runnable() {
      public void run() {
        // do whatever you want to change here, like:
        //mAddressText.setText(newText);
        mAddressText.setText(newText);
      }
    };
    mHandler.post(updateUITimerTask);
  }

  private Address geoCodeNewPoint(GeoPoint center) {
    Geocoder geocoder = new Geocoder(this);
    Location location = mGeoUtils.geoPointToLocation(center);
    try {
      List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
      Address address = addresses.get(0);
      return address;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  private Bitmap getXBitmap() {
    Drawable xDrawable = getResources().getDrawable(R.drawable.x);
    Bitmap bitmap = ((BitmapDrawable)xDrawable).getBitmap();
    return bitmap;
  }
}
