package co.gargoyle.supercab.android.activities.parent;

import java.util.List;

import roboguice.activity.RoboMapActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.map.PickupDropoffOverlay;
import co.gargoyle.supercab.android.model.GeoBoundingBox;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.common.base.Optional;
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
  // Map Management
  ////////////////////////////////////////////////////////////
  
  protected void zoomMapToFitPoints(MapController controller, List<GeoPoint> points) {
    Optional<GeoBoundingBox> optional = GeoUtils.getBoundingBox(points); 
    if (!optional.isPresent()) {
      // Can't zoom without a box!
    } else {
      GeoBoundingBox boundingBox = optional.get();

      controller.zoomToSpan(boundingBox.getLatitudeSpan(), boundingBox.getLongitudeSpan());
      controller.animateTo(boundingBox.getMidPoint());
    }
  }
  
  protected void zoomMapToFitBothPins(MapController controller, PickupDropoffOverlay overlay) {

    int latSpan = (int) (1.2 * overlay.getLatSpanE6());
    int lonSpan = (int) (1.2 * overlay.getLonSpanE6());
    controller.zoomToSpan(latSpan, lonSpan);

    Optional<GeoPoint> result = overlay.getCenterPoint();
    if (result.isPresent()) {
      controller.animateTo(result.get());
    }
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
