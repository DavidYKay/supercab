package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboMapActivity;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import co.gargoyle.supercab.android.R;

public class HailActivity extends RoboMapActivity {

  private static final String t = "HailActivity";

  @InjectView(R.id.hail_button) private Button mHailButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // must be at the beginning of any activity that can be called from an external intent
    Log.i(t, "Starting up, creating directories");

    setContentView(R.layout.hail);

    //setTitle(getString(R.string.app_name) + " > " + getString(R.string.main_menu));
  }

////////////////////////////////////////////////////////////
// Button Callbacks
////////////////////////////////////////////////////////////

  public void onHailButtonClicked(View view) {

  }
  
  public void onLocateButtonClicked(View view) {

  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

}
