package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.utilities.BroadcastUtils;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.google.inject.Inject;

public class FareDetailActivity extends RoboActivity {
  
  @InjectView(R.id.time_value) private TextView mTimeLabel;
  @InjectView(R.id.from_address) private TextView mSourceText;
  @InjectView(R.id.to_address) private TextView mDestinationText;
  @InjectView(R.id.passenger_status) private TextView mPassengerLabel;

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
    mSourceText.setText(fare.source.toString());
    mDestinationText.setText(fare.destination.toString());

    mTimeLabel.setText(StringUtils.getNiceTime(fare.timeRequested));
    
//    mPassengerLabel.setText(fare.passenger.toString());

  }
  
  ////////////////////////////////////////////////////////////
  // Main Methods
  ////////////////////////////////////////////////////////////

  public void onAcceptButtonClick(View v) {

    finish();
  }

  ////////////////////////////////////////////////////////////
  // ORMLite
  ////////////////////////////////////////////////////////////

//  private SCOrmLiteHelper databaseHelper;
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    if (databaseHelper != null) {
//      OpenHelperManager.releaseHelper();
//      databaseHelper = null;
//    }
//  }
//
//  private SCOrmLiteHelper getHelper() {
//    if (databaseHelper == null) {
//      databaseHelper =
//          OpenHelperManager.getHelper(this, SCOrmLiteHelper.class);
//    }
//    return databaseHelper;
//  }

  ////////////////////////////////////////////////////////////
  // Utils
  ////////////////////////////////////////////////////////////

  void goBlooey(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Exception!").setMessage(t.toString()).setPositiveButton("OK", null).show();
  }

}
