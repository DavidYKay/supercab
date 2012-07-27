package co.gargoyle.supercab.android.activities;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.activities.parent.AbstractMapActivity;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.map.ExtendedMapView;
import co.gargoyle.supercab.android.map.ExtendedMapView.OnMoveListener;
import co.gargoyle.supercab.android.map.PickupDropoffItem;
import co.gargoyle.supercab.android.map.PickupDropoffOverlay;
import co.gargoyle.supercab.android.map.PickupDropoffOverlayTapListener;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.utilities.GeoUtils;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
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

  private static final int TEXT_ACTIVE_COLOR   = R.color.text_active;
  private static final int TEXT_INACTIVE_COLOR = R.color.text_inactive;

  private static final int ZOOM_LEVEL_CITY         = 15;
  private static final int ZOOM_LEVEL_NEIGHBORHOOD = 20;

  @InjectView(R.id.bottom_bar_pickup) private View mPickupBar;
  @InjectView(R.id.bottom_bar_confirmation) private View mConfirmationBar;
  @InjectView(R.id.location_hint) private ImageView mPinHint;
  @InjectView(R.id.location_text) private TextView mAddressText;
  @InjectView(R.id.hail_button) private Button mHailButton;
  @InjectView(R.id.map) private ExtendedMapView mMapView;

  @InjectView(R.id.confirmation_pickup_text) private TextView mConfirmationPickupText;
  @InjectView(R.id.confirmation_dropoff_text) private TextView mConfirmationDropoffText;

  @Inject private GeoUtils mGeoUtils;

  private PickupDropoffOverlay mPickupDropoffOverlay;
  private MyLocationOverlay mMyLocationOverlay;
  private MapController mMapController;

  private Handler mHandler;

  private Address mLastKnownAddress;
  private Location mLastKnownLocation;
  private PointType mMode = PointType.PICKUP;
  private boolean mHasGeolocated;

  @Inject private PreferenceUtils mPreferenceUtils;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(TAG, "Starting up, creating directories");


    Optional<Fare> pendingFare = getFareFromDb();
    if (pendingFare.isPresent()) {
      proceedToConfirmation(pendingFare.get());
    } else {
      setContentView(R.layout.hail);

      mMapView.setBuiltInZoomControls(true);

      mMapController = mMapView.getController();
      mMapController.setZoom(ZOOM_LEVEL_NEIGHBORHOOD); // Fixed Zoom Level

      mMyLocationOverlay = new MyLocationOverlay(this, mMapView) {
        @Override
        public synchronized void onLocationChanged(Location location) {
          super.onLocationChanged(location);
          checkAndUpdateLastKnownLocation(location);
        }
      };

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

      mMapView.getOverlays().add(mMyLocationOverlay);

      mMapView.setOnMoveListener(new OnMoveListener() {
        public void onMove(MapView mapView, GeoPoint center, boolean stopped) {
          Log.d(TAG, String.format("onMove center: %s stopped: %b", center.toString(), stopped));
          if (stopped) {
            updateAddressWithGeoPoint(center);
          } else {
            setAddressLabelActive(false);
            mLastKnownAddress = null;
          }
        }
      });

      centerMapAction();
    }
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

  public void onHailButtonClicked(View view) {
    Log.i(TAG, "onHailButtonClicked()");

    if (isUiThread()) {
      addPickupDropoffAtCurrentAddress();
    } else {
      Runnable updateUITimerTask = new Runnable() {
        public void run() {
          addPickupDropoffAtCurrentAddress();
        }
      };
      mHandler.post(updateUITimerTask);
    }
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

    Fare fare = getFareFromUi();
    saveFareToDb(fare);
    proceedToConfirmation(fare);
  }

  public void onCancelConfirmButtonClicked(View view) {
    Log.i(TAG, "onCancelConfirmButtonClicked()");

    // Clear pins, reset to 0
    setMode(PointType.PICKUP);
  }

  ////////////////////////////////////////////////////////////
  // Mode Management
  ////////////////////////////////////////////////////////////

  private void setMode(PointType mode) {

    if (mMode == PointType.WAITING && mode == PointType.PICKUP) {
      // Complete reset
      enterStandardMode();
      mPickupDropoffOverlay.clear();
    }

    mMode = mode;

    mPinHint.setImageDrawable(getPinDrawableForMode(mode));

    mHailButton.setText(getHailTextForMode(mode));
    mHailButton.invalidate();

    if (mode == PointType.WAITING) {
      enterConfirmationMode();
    } else {
      // NOP
    }
  }

  private void enterStandardMode() {
    setBottomBarConfirmation(false);
    centerMapAction();
  }

  private void enterConfirmationMode() {
    Toast.makeText(DrivingActivity.this, "Confirm???", Toast.LENGTH_SHORT).show();
    zoomMapToFitBothPins();

    populateBottomBar();
    setBottomBarConfirmation(true);
  }

  ////////////////////////////////////////////////////////////
  // View Management
  ////////////////////////////////////////////////////////////

  private void populateBottomBar() {
    PickupPoint source = mPickupDropoffOverlay.get(0);
    PickupPoint destination = mPickupDropoffOverlay.get(1);

    mConfirmationPickupText.setText(source.address);
    mConfirmationDropoffText.setText(destination.address);
  }

  private void setBottomBarConfirmation(boolean confirmation) {
    if (confirmation) {
      mPickupBar.setVisibility(View.GONE);
      mConfirmationBar.setVisibility(View.VISIBLE);
    } else {
      mPickupBar.setVisibility(View.VISIBLE);
      mConfirmationBar.setVisibility(View.GONE);
    }
  }

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

  private void zoomMapToFitBothPins() {
    mMapController.zoomToSpan(mPickupDropoffOverlay.getLatSpanE6(), mPickupDropoffOverlay.getLonSpanE6());

    Optional<GeoPoint> result = mPickupDropoffOverlay.getCenterPoint();
    if (result.isPresent()) {
      mMapController.animateTo(result.get());
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

  private Optional<Address> geoCodeNewPoint(GeoPoint center) {
    Geocoder geocoder = new Geocoder(this);
    Location location = mGeoUtils.geoPointToLocation(center);
    try {
      List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
          location.getLongitude(), 1);
      Address address = addresses.get(0);
      return Optional.of(address);
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.absent();
    }
  }

  private void addPickupDropoffAtCurrentAddress() {
    boolean success = addPickupAtCurrentAddress(mMode);
    if (!success) {
      return;
    }

    if (mMode == PointType.PICKUP) {
      setMode(PointType.DROPOFF);
    } else if (mMode == PointType.DROPOFF) {
      setMode(PointType.WAITING);
    } else {
      throw new RuntimeException("Can't pickup, dropoff, pickup.");
      //setMode(FareType.PICKUP);
    }
  }

  private boolean addPickupAtCurrentAddress(PointType pointType) {
    Address address = copyAddress(mLastKnownAddress);
    if (address == null) {
      return false;
    } else {

      // Modify address to new location.
      GeoPoint geoPoint = mMapView.getMapCenter();

      address.setLatitude(GeoUtils.integerToDoubleValue(geoPoint.getLatitudeE6()));
      address.setLongitude(GeoUtils.integerToDoubleValue(geoPoint.getLongitudeE6()));

      PickupPoint testPickup = new PickupPoint(pointType, address);

      mPickupDropoffOverlay.addPickup(testPickup);
      mMapView.invalidate();
      return true;
    }
  }

  ////////////////////////////////////////////////////////////
  // Address Text Management
  ////////////////////////////////////////////////////////////

  private void setAddressLabelLoading() {
    setAddressLabelActive(false);

    final String newText = getResources().getString(R.string.loading_address);
    Runnable updateUITimerTask = new Runnable() {
      public void run() {
        mAddressText.setText(newText);
      }
    };
    mHandler.post(updateUITimerTask);
  }

  private void setAddressLabelActive(final boolean active) {
    final int newColor;
    if (active) {
      newColor = getResources().getColor(TEXT_ACTIVE_COLOR);
    } else {
      newColor = getResources().getColor(TEXT_INACTIVE_COLOR);
    }

    if (newColor == mAddressText.getCurrentTextColor()) {
      return;
    } else {
      Runnable updateUITimerTask = new Runnable() {
        public void run() {
          mAddressText.setTextColor(newColor);
          mAddressText.invalidate();
        }
      };
      mHandler.post(updateUITimerTask);
    }
  }

  private void updateAddressTextOnUiThread(final CharSequence newText) {
    Runnable updateUITimerTask = new Runnable() {
      public void run() {
        mAddressText.setText(newText);
        setAddressLabelActive(true);
      }
    };
    mHandler.post(updateUITimerTask);
  }

  private void updateAddressWithGeoPoint(GeoPoint center) {
    Log.d("address", "updateAddressWithGeoPoint()");
    setAddressLabelLoading();

    Optional<Address> address = geoCodeNewPoint(center);

    if (!address.isPresent()) {
      return;
    }

    mLastKnownAddress = address.get();

    String addressString = mLastKnownAddress.getAddressLine(0);
    Log.d("address", "current address text: " + mAddressText.getText().toString());
    Log.d("address", "new address text: " + addressString);
    updateAddressTextOnUiThread(addressString);
  }

  ////////////////////////////////////////////////////////////
  // Resources
  ////////////////////////////////////////////////////////////

  private HashMap<PointType, Bitmap> getPinOverlayMappings() {
    HashMap<PointType, Bitmap> mappings = new HashMap<PointType, Bitmap>();

    PointType[] modes = new PointType[] {
      PointType.PICKUP,
      PointType.DROPOFF
    };

    for (PointType mode : modes) {
      mappings.put(mode, getPinBitmapForMode(mode));
    }

    return mappings;
  }

  private Bitmap getPinBitmapForMode(PointType mode) {
    Drawable drawable = getPinDrawableForMode(mode);
    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
    return bitmap;
  }

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

  private String getHailTextForMode(PointType mode) {
    String prompt;
    if (mode == PointType.PICKUP) {
      prompt = getResources().getString(R.string.pick_me_up_here);
    } else {
      prompt = getResources().getString(R.string.drop_me_off_here);
    }
    return prompt;
  }

  ////////////////////////////////////////////////////////////
  // Fare
  ////////////////////////////////////////////////////////////

  private Optional<Fare> getFareFromDb() {
    RuntimeExceptionDao <Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);

    QueryBuilder<Fare, Integer> builder = dao.queryBuilder();
    
    Where<Fare, Integer> where = builder.where();
    try {
      where.eq("status", FareStatus.waiting);
      builder.setWhere(where);
      
      // get all fares that are waiting
      List<Fare> fares = dao.query(builder.prepare());

      if (fares.size() > 0) {
        return Optional.of(fares.get(0));
      } else {
        return Optional.absent();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Optional.absent();
  }

  private void saveFareToDb(Fare fare) {
    RuntimeExceptionDao <Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);
    dao.create(fare);
  }
  
  private Fare getFareFromUi() {
    PickupPoint source = mPickupDropoffOverlay.get(0);
    PickupPoint destination = mPickupDropoffOverlay.get(1);
    Date timeRequested = new Date();

    Fare fare = new Fare(source, destination, timeRequested);
    fare.status = FareStatus.waiting;

    return fare;
  }

  ////////////////////////////////////////////////////////////
  // Util
  ////////////////////////////////////////////////////////////


  private boolean isUiThread() {
    if (Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
      return true;
    } else {
      return false;
    }
  }

  private Address copyAddress(Address origAddress) {
    Parcel p = Parcel.obtain();
    p.writeValue(origAddress);
    p.setDataPosition(0);
    Address newAddress = (Address)p.readValue(Address.class.getClassLoader());
    p.recycle();
    return newAddress;
  }
  
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
  
  private void proceedToConfirmation(Fare fare) {
    Intent i = new Intent(DrivingActivity.this, ConfirmationActivity.class);
    i.putExtra(KEY_FARE, fare);
    startActivity(i);

    finish();
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
}
