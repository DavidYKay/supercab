package co.gargoyle.supercab.android.activities.parent;

import roboguice.activity.RoboMapActivity;
import android.os.Bundle;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.inject.Inject;

public abstract class AbstractMapActivity extends RoboMapActivity {
  
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
