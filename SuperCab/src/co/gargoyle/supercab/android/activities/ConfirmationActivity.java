package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import android.os.Bundle;
import co.gargoyle.supercab.android.R;

public class ConfirmationActivity extends RoboActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.confirmation);
  }

}
