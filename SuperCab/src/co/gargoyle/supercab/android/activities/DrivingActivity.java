package co.gargoyle.supercab.android.activities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class DrivingActivity extends AbstractMapActivity {

  public static final String KEY_FARE = "fare";

  private static final String TAG = "DrivingActivity";
  private static final String LOCATION_TAG = "location";

  @InjectView(R.id.fare_status) private TextView mFareStatusLabel;
  @InjectView(R.id.confirmation_pickup_text) private TextView mPickupLabel;
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

    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    Log.i(TAG, "Starting up, creating directories");

    Intent i = getIntent();
    //Fare fare = i.getParcelableExtra(Constants.KEY_FARE);
    //if (fare == null) {
    //  onCouldNotFindFare();
    //} else {
    //  mFare = fare;
    //}

    long fareId = i.getIntExtra(Constants.KEY_FARE_ID, -1);
    if (fareId == -1) {
      onCouldNotFindFare();
    } else {
      Optional<Fare> fare = getFareFromDb(fareId);
      if (!fare.isPresent()) {
        onCouldNotFindFare();
      } else {
        mFare = fare.get();
      }
    }

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

    mPickupDropoffOverlay = PickupDropoffOverlay.Factory.createFromFare(
        getBoundedPinForMapOverlayWithMode(PointType.PICKUP),
        getBoundedPinForMapOverlayWithMode(PointType.DROPOFF),
        mFare);
    mPickupDropoffOverlay.setTapListener(new PickupDropoffOverlayTapListener() {
      @Override
      public void itemWasTapped(PickupDropoffItem item) {
        Toast.makeText(DrivingActivity.this,
                       item.getTitle(),
                       Toast.LENGTH_SHORT).show();
      }
    });
    mMapView.getOverlays().add(mPickupDropoffOverlay);

    updateUiWithFare(mFare);
    //centerMapAction();
    fitPinsAndMe();
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

    cancelFare();
  }

  ////////////////////////////////////////////////////////////
  // Mode Management
  ////////////////////////////////////////////////////////////

  protected void setStatus(FareStatus status) {
    //mFareStatusLabel.setText(getString(sTextForMode.get(status)));
    
    mFareStatusLabel.setText(getString(sTextForMode.get(status)));
  }

  ////////////////////////////////////////////////////////////
  // View Management
  ////////////////////////////////////////////////////////////

  private void updateUiWithFare(Fare fare) {
    setStatus(fare.status);
    
    Location location = GeoUtils.pickupPointToLocation(fare.source);
    mPickupLabel.setText(StringUtils.makeWebLinkFromUrl(
        GeoUtils.makeGoogleMapsUrl(location),
                      fare.source.toString()));
        
                      //"geo:0,0?q=my+street+address",
                      //"geo:-1.29885,36.79089?q=iHub",
                      //"http://maps.google.com/?q=5.352135,100.299683&z=17",
                      //"http://maps.google.com/?q=-1.29885,36.79089&z=17",
    mPickupLabel.setMovementMethod(LinkMovementMethod.getInstance());
  }

  ////////////////////////////////////////////////////////////
  // Map Management
  ////////////////////////////////////////////////////////////

  private void fitPinsAndMe() {
    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();

    Location lastFix = mMyLocationOverlay.getLastFix();
    if (lastFix != null) {
      points.add(GeoUtils.locationToGeoPoint(lastFix));
    }

    points.add(GeoUtils.pickupPointToGeoPoint(mFare.source));
    points.add(GeoUtils.pickupPointToGeoPoint(mFare.destination));

    zoomMapToFitPoints(mMapController, points);
  }

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
  // Fare
  ////////////////////////////////////////////////////////////

  // TODO: Unify with ConfirmationActivity code
  private void cancelFare() {
    // Tell the API we're done
    mFare.status = FareStatus.cancelled;

    // PUT fare to server, letting people know that we're cancelling it
    final PutFareTask task = new PutFareTask(this, new PutFareListener() {
      @Override
      public void completed(Optional<Fare> fare) {
        setProgressBarIndeterminateVisibility(false);
        if (fare.isPresent() && fare.get().status == FareStatus.cancelled) {
          Toast.makeText(DrivingActivity.this, "Fare Cancelled!", Toast.LENGTH_SHORT).show();
          // clear out the fare from the DB
          deleteFareFromDb(fare.get());

          // back to the main screen
          startActivity(new Intent(DrivingActivity.this, FareListActivity.class));
          finish();
        } else {
          // Something happened. better not risk it
        }
        //mTasks.remove(this);
      }

      @Override
      public void handleError(Throwable exception) {
        setProgressBarIndeterminateVisibility(false);
        handleThrowable(exception);
      }
    });

    //mTasks.add(task);
    setProgressBarIndeterminateVisibility(true);
    task.execute(mFare);
  }

  private void onFareArrived() {
    mFare.status = FareStatus.active;

    PutFareTask task = new PutFareTask(this, new PutFareListener() {
      @Override
      public void completed(Optional<Fare> fare) {
        setProgressBarIndeterminateVisibility(false);
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
        setProgressBarIndeterminateVisibility(false);
        handleThrowable(exception);
      }
    });
    setProgressBarIndeterminateVisibility(true);
    task.execute(mFare);

  }

  ////////////////////////////////////////////////////////////
  // Util
  ////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////
  // Logout
  ////////////////////////////////////////////////////////////


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


  private void onCouldNotFindFare() {
    Toast.makeText(DrivingActivity.this, "Error! No fare found.", Toast.LENGTH_SHORT).show();
    finish();
  }

  ////////////////////////////////////////////////////////////
  // DB
  ////////////////////////////////////////////////////////////

  private void deleteFareFromDb(Fare fare) {
    RuntimeExceptionDao<Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);

    // delete all fares with the matching id
    //dao.deleteById(fare.id);

    DeleteBuilder<Fare, Integer> builder = dao.deleteBuilder();
    try {
      dao.delete(builder.prepare());
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private Optional<Fare> getFareFromDb(long fareId) {
    // get the fare from the DB.
    RuntimeExceptionDao<Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);

    QueryBuilder<Fare, Integer> builder = dao.queryBuilder();

    Where<Fare, Integer> where = builder.where();
    try {
      where.eq("id", fareId);
      builder.setWhere(where);

      // get all fares that are waiting
      List<Fare> fares = dao.query(builder.prepare());
      if (fares.size() > 0) {
        Fare fare = fares.get(0);
        dao.refresh(fare);
        return Optional.of(fare);
      } else {
        return Optional.absent();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Optional.absent();
  }

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
      databaseHelper = OpenHelperManager.getHelper(this, SCOrmLiteHelper.class);
    }
    return databaseHelper;
  }

  ////////////////////////////////////////////////////////////
  // Utils
  ////////////////////////////////////////////////////////////

  void handleThrowable(Throwable t) {
    goBlooey(t);
  }

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
