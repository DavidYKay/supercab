package co.gargoyle.supercab.android.activities;

import java.sql.SQLException;
import java.util.HashMap;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.activities.parent.AbstractMapActivity;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.map.ExtendedMapView;
import co.gargoyle.supercab.android.map.PickupDropoffItem;
import co.gargoyle.supercab.android.map.PickupDropoffOverlay;
import co.gargoyle.supercab.android.map.PickupDropoffOverlayTapListener;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.tasks.PutFareTask;
import co.gargoyle.supercab.android.tasks.listeners.PutFareListener;
import co.gargoyle.supercab.android.utilities.Constants;
import co.gargoyle.supercab.android.utilities.GeoUtils;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;

public class DrivingActivity extends AbstractMapActivity {

  public static final String KEY_FARE = "fare";

  private static final String TAG = "DrivingActivity";
  private static final String LOCATION_TAG = "location";

  @InjectView(R.id.fare_status) private TextView mFareStatusLabel;
  @InjectView(R.id.map) private ExtendedMapView mMapView;

  @Inject private GeoUtils mGeoUtils;

  private PickupDropoffOverlay mPickupDropoffOverlay;
  private MyLocationOverlay mMyLocationOverlay;
  private MapController mMapController;

  @SuppressWarnings("unused")
  private Handler mHandler;

  private Fare mFare;

  private Location mLastKnownLocation;
  private boolean mHasGeolocated;

  @Inject private PreferenceUtils mPreferenceUtils;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(TAG, "Starting up, creating directories");

    Intent i = getIntent();
    Fare fare = i.getParcelableExtra(Constants.KEY_FARE); 

    //Optional<Fare> fare = getFareFromDb(fareId);

    if (fare == null) {
      Toast.makeText(DrivingActivity.this, "Error! No fare found.", Toast.LENGTH_SHORT).show();
      finish();
    }
    mFare = fare;

    setContentView(R.layout.driving);

    mMapView.setBuiltInZoomControls(true);

    mMapController = mMapView.getController();
    mMapController.setZoom(ZOOM_LEVEL_CITY); // Fixed Zoom Level

    mMyLocationOverlay = new MyLocationOverlay(this, mMapView) {
      @Override
      public synchronized void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        checkAndUpdateLastKnownLocation(location);
      }
    };
    mMapView.getOverlays().add(mMyLocationOverlay);

    mPickupDropoffOverlay = new PickupDropoffOverlay(
        getBoundedPinForMapOverlayWithMode(PointType.PICKUP),
        getBoundedPinForMapOverlayWithMode(PointType.DROPOFF)
        );

    mPickupDropoffOverlay.setTapListener(new PickupDropoffOverlayTapListener() {
      @Override
      public void itemWasTapped(PickupDropoffItem item) {
        Toast.makeText(DrivingActivity.this,
                       item.getTitle(),
                       Toast.LENGTH_SHORT).show();
      }
    });
    mMapView.getOverlays().add(mPickupDropoffOverlay);

    centerMapAction();
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

  // @Override
  // protected boolean isLocationDisplayed() {
  // return true;
  // }

  public void onArrivedButtonClicked(View view) {
    Log.i(TAG, "onArrivedButtonClicked()");

    onFareArrived();
  }

  public void onProfileButtonClicked(View view) {
    Log.i(TAG, "onProfileButtonClicked()");

    logout();
  }

  public void onLocateButtonClicked(View view) {
    Log.i(TAG, "onLocateButtonClicked()");

    centerMapAction();
  }

  public void onConfirmButtonClicked(View view) {
    Log.i(TAG, "onConfirmButtonClicked()");

  }

  public void onCancelFareButtonClicked(View view) {
    Log.i(TAG, "onCancelFareButtonClicked()");

  }

  ////////////////////////////////////////////////////////////
  // Mode Management
  ////////////////////////////////////////////////////////////


  ////////////////////////////////////////////////////////////
  // View Management
  ////////////////////////////////////////////////////////////


  ////////////////////////////////////////////////////////////
  // Map Management
  ////////////////////////////////////////////////////////////

  private void zoomMapAction() {
    int zoomLevel = mMapView.getZoomLevel();

    if (zoomLevel < ZOOM_LEVEL_CITY) {
      mMapController.setZoom(ZOOM_LEVEL_CITY);
    } else if (zoomLevel < ZOOM_LEVEL_NEIGHBORHOOD) {
      mMapController.setZoom(ZOOM_LEVEL_NEIGHBORHOOD);
    } else {
      // We're zoomed in enough. Stop.
    }

  }

  private void centerMapAction() {
    if (mLastKnownLocation != null) {
      centerMapAction(mLastKnownLocation);
    } else {
      Location lastFix = mMyLocationOverlay.getLastFix();
      if (lastFix != null) {
        centerMapAction(lastFix);
      }
    }
  }

  private void centerMapAction(Location location) {
    centerMapAction(mGeoUtils.locationToGeoPoint(location));
  }

  private void centerMapAction(GeoPoint point) {
    GeoPoint mapCenter = mMapView.getMapCenter();
    if (mapCenter.equals(point)) {
      // Zoom in
      zoomMapAction();
    } else {
      // Pan over
      //mMapController.setCenter(point);
      mMapController.animateTo(point);
    }
  }

  private void checkAndUpdateLastKnownLocation(Location location) {
    if (isValidLocation(location)) {
      if (mGeoUtils.isBetterLocation(location, mLastKnownLocation)) {
        Log.d(LOCATION_TAG, "centerMapOnLocation:" + mGeoUtils.locationToString(location));
        mLastKnownLocation = location;

        if (!mHasGeolocated) {
          centerMapAction(location);
          mHasGeolocated = true;
        }
      } else {
        Log.d(LOCATION_TAG, "poor location fix" + mGeoUtils.locationToString(location));
      }
    } else {
      Log.d(LOCATION_TAG, "invalid location" + mGeoUtils.locationToString(location));

    }
  }

  ////////////////////////////////////////////////////////////
  // Geocoding
  ////////////////////////////////////////////////////////////

  private boolean isValidLocation(Location location) {
    if (location == null) {
      return false;
    }

    if (location.getProvider() == null) {
      return false;
    }
    if (location.getLatitude() == 0 && location.getLongitude() == 0) {
      return false;
    }
    return true;
  }

  ////////////////////////////////////////////////////////////
  // Address Text Management
  ////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////
  // Resources
  ////////////////////////////////////////////////////////////

  private Drawable getPinDrawableForMode(PointType mode) {
    Drawable newPin;
    if (mode == PointType.PICKUP) {
      newPin = getResources().getDrawable(R.drawable.map_pin_green);
    } else {
      newPin = getResources().getDrawable(R.drawable.map_pin_red);
    }
    return newPin;
  }

  private Drawable getBoundedPinForMapOverlayWithMode(PointType mode) {
    Drawable pin = getPinDrawableForMode(mode);

    pin.setBounds(0, 0, pin.getIntrinsicWidth(), pin.getIntrinsicHeight());
    return pin;
  }

  ////////////////////////////////////////////////////////////
  // Fare
  ////////////////////////////////////////////////////////////

  private void onFareArrived() {
    mFare.status = FareStatus.active;

    PutFareTask task = new PutFareTask(this, new PutFareListener() {
      @Override
      public void completed(Optional<Fare> fare) {
        if (fare.isPresent()) {
          Toast.makeText(DrivingActivity.this, "Customer Notified!", Toast.LENGTH_SHORT).show();
          mFare = fare.get();
          setStatus(mFare.status);
        } else {
          // Something happened. better not risk it
        }
      }

      @Override
      public void handleError(Throwable exception) {
        goBlooey(exception);
      }
    });
    task.execute(mFare);

  }
  
  ////////////////////////////////////////////////////////////
  // Util
  ////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////
  // Logout
  ////////////////////////////////////////////////////////////
  
  protected void setStatus(FareStatus status) {
    mFareStatusLabel.setText(getString(sTextForMode.get(status)));
  }

  private void logout() {
    RuntimeExceptionDao <UserModel, Integer> dao = getHelper().getRuntimeDao(UserModel.class);
    DeleteBuilder<UserModel, Integer> builder = dao.deleteBuilder();
    PreparedDelete<UserModel> deleteAll;
    try {
      deleteAll = builder.prepare();
      dao.delete(deleteAll);
      mPreferenceUtils.clearUser();

      startActivity(new Intent(DrivingActivity.this, LoginActivity.class));
      finish();
    } catch (SQLException e) {
      e.printStackTrace();
      goBlooey(e);
    }
   
  }

  ////////////////////////////////////////////////////////////
  // Nav
  ////////////////////////////////////////////////////////////
  
  ////////////////////////////////////////////////////////////
  // ORMLite
  ////////////////////////////////////////////////////////////

  private SCOrmLiteHelper databaseHelper;
  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (databaseHelper != null) {
      OpenHelperManager.releaseHelper();
      databaseHelper = null;
    }
  }

  private SCOrmLiteHelper getHelper() {
    if (databaseHelper == null) {
      databaseHelper =
          OpenHelperManager.getHelper(this, SCOrmLiteHelper.class);
    }
    return databaseHelper;
  }
  
  ////////////////////////////////////////////////////////////
  // Utils
  ////////////////////////////////////////////////////////////
  
  void goBlooey(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Exception!").setMessage(t.toString()).setPositiveButton("OK", null).show();
  }
  
  private static final HashMap<FareStatus, Integer> sTextForMode = new HashMap<FareStatus, Integer>();

  static {
    sTextForMode.put(FareStatus.waiting  , R.string.mode_driver_waiting);
    sTextForMode.put(FareStatus.accepted , R.string.mode_driver_accepted);
    sTextForMode.put(FareStatus.active   , R.string.mode_driver_active);
    sTextForMode.put(FareStatus.complete , R.string.mode_driver_complete);
    }
}
