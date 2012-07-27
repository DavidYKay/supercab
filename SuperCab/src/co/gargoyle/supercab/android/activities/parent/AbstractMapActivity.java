package co.gargoyle.supercab.android.activities.parent;

import roboguice.activity.RoboMapActivity;
import android.os.Bundle;
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

}
