package co.gargoyle.supercab.android.activities.parent;

import roboguice.activity.RoboMapActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.inject.Inject;

public abstract class AbstractMapActivity extends RoboMapActivity {
  
  public static final int ZOOM_LEVEL_CITY         = 15;
  public static final int ZOOM_LEVEL_NEIGHBORHOOD = 20;
  
  @Inject protected GeoUtils mGeoUtils;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected boolean isRouteDisplayed() {
    // TODO Auto-generated method stub
    return false;
  }
  
  ////////////////////////////////////////////////////////////
  // Resources
  ////////////////////////////////////////////////////////////

  protected Drawable getPinDrawableForMode(PointType mode) {
    Drawable newPin;
    if (mode == PointType.PICKUP) {
      newPin = getResources().getDrawable(R.drawable.map_pin_green);
    } else {
      newPin = getResources().getDrawable(R.drawable.map_pin_red);
    }
    return newPin;
  }

  protected Drawable getBoundedPinForMapOverlayWithMode(PointType mode) {
    Drawable pin = getPinDrawableForMode(mode);

    pin.setBounds(0, 0, pin.getIntrinsicWidth(), pin.getIntrinsicHeight());
    return pin;
  }

}
