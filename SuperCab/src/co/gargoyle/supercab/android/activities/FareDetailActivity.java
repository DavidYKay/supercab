package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.utilities.BroadcastUtils;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class FareDetailActivity extends RoboActivity {
  
  @InjectView(R.id.call_from) private TextView mCallFrom;

  @SuppressWarnings("unused")
  @Inject private BroadcastUtils mBroadcastUtils;

  @Inject private PreferenceUtils mPreferenceUtils;

  ////////////////////////////////////////////////////////////
  // Activity Lifecycle
  ////////////////////////////////////////////////////////////

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fare_detail);
    
    Intent i = getIntent();
    Fare fare = i.getParcelableExtra(HailActivity.KEY_FARE); 

    populateUi(fare);
  }

  private void populateUi(Fare fare) {
    mCallFrom.setText(fare.source.toString());
  }
  
  ////////////////////////////////////////////////////////////
  // Main Methods
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

}
