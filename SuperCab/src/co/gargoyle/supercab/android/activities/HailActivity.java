package co.gargoyle.supercab.android.activities;

import java.io.IOException;
import java.util.HashMap;
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
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.map.ExtendedMapView;
import co.gargoyle.supercab.android.map.ExtendedMapView.OnMoveListener;
import co.gargoyle.supercab.android.map.PickupDropoffItem;
import co.gargoyle.supercab.android.map.PickupDropoffOverlay;
import co.gargoyle.supercab.android.map.PickupDropoffOverlayTapListener;
import co.gargoyle.supercab.android.map.PickupPoint;
import co.gargoyle.supercab.android.map.XOverlay;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.common.base.Optional;
import com.google.inject.Inject;

public class HailActivity extends RoboMapActivity {

  private static final String TAG = "HailActivity";
  private static final String LOCATION_TAG = "location";

  private static final int TEXT_ACTIVE_COLOR   = R.color.text_active;
  private static final int TEXT_INACTIVE_COLOR = R.color.text_inactive;

  @InjectView(R.id.location_hint) private ImageView mPinHint;
  @InjectView(R.id.location_text) private TextView mAddressText;
  @InjectView(R.id.hail_button) private Button mHailButton;
  @InjectView(R.id.map) private ExtendedMapView mMapView;

  @Inject private GeoUtils mGeoUtils;

  private PickupDropoffOverlay mPickupDropoffOverlay;
  private XOverlay mXOverlay;
  private MyLocationOverlay mMyLocationOverlay;
  private MapController mMapController;

  private Handler mHandler;

  private Address mLastKnownAddress;
  private Location mLastKnownLocation;
  private FareType mMode = FareType.PICKUP;
  private boolean mHasGeolocated;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(TAG, "Starting up, creating directories");

    setContentView(R.layout.hail);

    mMapView.setBuiltInZoomControls(true);

    mMapController = mMapView.getController();
    mMapController.setZoom(20); // Fixed Zoom Level

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

    mXOverlay = new XOverlay(getPinOverlayMappings(), FareType.PICKUP);

    mPickupDropoffOverlay = new PickupDropoffOverlay(
        getBoundedPinForMapOverlayWithMode(FareType.PICKUP),
        getBoundedPinForMapOverlayWithMode(FareType.DROPOFF)
        );

    mPickupDropoffOverlay.setTapListener(new PickupDropoffOverlayTapListener() {
      @Override
      public void itemWasTapped(PickupDropoffItem item) {
        Toast.makeText(HailActivity.this,
                       item.getTitle(),
                       Toast.LENGTH_SHORT).show();
      }
    });
    mMapView.getOverlays().add(mPickupDropoffOverlay);

    mMapView.getOverlays().add(mMyLocationOverlay);
    mMapView.getOverlays().add(mXOverlay);

    mMapView.setOnMoveListener(new OnMoveListener() {
      public void onMove(MapView mapView, GeoPoint center, boolean stopped) {
        Log.d(TAG, String.format("onMove center: %s stopped: %b", center.toString(), stopped));
        if (stopped) {
          updateAddressWithGeoPoint(center);
        } else {
          setAddressLabelActive(false);
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

  // @Override
  // protected boolean isLocationDisplayed() {
  // return true;
  // }

  public void onHailButtonClicked(View view) {
    Log.i(TAG, "onHailButtonClicked()");

    if (isUiThread()) {
      addPickupDropoffAtCurrentAddress();
    } else {
      final String newText = getResources().getString(R.string.loading_address);
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

  }

  public void onLocateButtonClicked(View view) {
    Log.i(TAG, "onLocateButtonClicked()");

  }

  ////////////////////////////////////////////////////////////
  // Mode Management
  ////////////////////////////////////////////////////////////

  private void setMode(FareType mode) {
    mMode = mode;

    mXOverlay.setMode(mode);

    mPinHint.setImageDrawable(getPinDrawableForMode(mode));

    mHailButton.setText(getHailTextForMode(mode));
    mHailButton.invalidate();

  }

  ////////////////////////////////////////////////////////////
  // Map Management
  ////////////////////////////////////////////////////////////

  private void zoomMapToFitBothPins() {
    mMapController.zoomToSpan(mPickupDropoffOverlay.getLatSpanE6(), mPickupDropoffOverlay.getLonSpanE6());

    Optional<GeoPoint> result = mPickupDropoffOverlay.getCenterPoint();
    if (result.isPresent()) {
      mMapController.animateTo(result.get());
    }
  }

  private void centerMapOnLastKnownLocation() {
    centerMapOnLocation(mMyLocationOverlay.getLastFix());
  }

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

    if (mMode == FareType.PICKUP) {
      setMode(FareType.DROPOFF);
    } else if (mMode == FareType.DROPOFF) {
      setMode(FareType.PICKUP);

      //Toast.makeText(HailActivity.this, "Confirm???", Toast.LENGTH_SHORT).show();
      //zoomMapToFitBothPins();

    } else {
      throw new RuntimeException("Can't pickup, dropoff, pickup.");
      //setMode(FareType.PICKUP);
    }
  }

  private boolean addPickupAtCurrentAddress(FareType fareType) {
    Address address = copyAddress(mLastKnownAddress);
    if (address == null) {
      return false;
    } else {

      // Modify address to new location.
      GeoPoint geoPoint = mMapView.getMapCenter();

      address.setLatitude(GeoUtils.integerToDoubleValue(geoPoint.getLatitudeE6()));
      address.setLongitude(GeoUtils.integerToDoubleValue(geoPoint.getLongitudeE6()));

      PickupPoint testPickup = new PickupPoint(fareType, address);

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

  private Bitmap getXBitmap() {
    Drawable xDrawable = getResources().getDrawable(R.drawable.x);
    Bitmap bitmap = ((BitmapDrawable) xDrawable).getBitmap();
    return bitmap;
  }


  private HashMap<FareType, Bitmap> getPinOverlayMappings() {
    HashMap<FareType, Bitmap> mappings = new HashMap<FareType, Bitmap>();

    FareType[] modes = new FareType[] {
      FareType.PICKUP,
      FareType.DROPOFF
    };

    for (FareType mode : modes) {
      mappings.put(mode, getPinBitmapForMode(mode));
    }

    return mappings;
  }

  private Bitmap getPinBitmapForMode(FareType mode) {
    Drawable drawable = getPinDrawableForMode(mode);
    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
    return bitmap;
  }

  private Drawable getPinDrawableForMode(FareType mode) {
    Drawable newPin;
    if (mode == FareType.PICKUP) {
      newPin = getResources().getDrawable(R.drawable.map_pin_green);
    } else {
      newPin = getResources().getDrawable(R.drawable.map_pin_red);
    }
    return newPin;
  }

  private Drawable getBoundedPinForMapOverlayWithMode(FareType mode) {
    Drawable pin = getPinDrawableForMode(mode);

    pin.setBounds(0, 0, pin.getIntrinsicWidth(), pin.getIntrinsicHeight());
    return pin;
  }

  private String getHailTextForMode(FareType mode) {
    String prompt;
    if (mode == FareType.PICKUP) {
      prompt = getResources().getString(R.string.pick_me_up_here);
    } else {
      prompt = getResources().getString(R.string.drop_me_off_here);
    }
    return prompt;
  }

  ////////////////////////////////////////////////////////////
  // Listeners
  ////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////
  // Util
  ////////////////////////////////////////////////////////////


  private boolean isUiThread() {
    if (Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
      //throw new RuntimeException("This is not to be executed in the Main-thread ");
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

}
