package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.tasks.PutFareTask;
import co.gargoyle.supercab.android.tasks.listeners.PutFareListener;
import co.gargoyle.supercab.android.utilities.BroadcastUtils;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class FareDetailActivity extends RoboActivity {
  
  @InjectView(R.id.time_value) private TextView mTimeLabel;
  @InjectView(R.id.from_address) private TextView mSourceText;
  @InjectView(R.id.to_address) private TextView mDestinationText;
  @InjectView(R.id.passenger_status) private TextView mPassengerLabel;

  @SuppressWarnings("unused")
  @Inject private BroadcastUtils mBroadcastUtils;

//  @Inject private PreferenceUtils mPreferenceUtils;
  
  private Fare mFare;

  ////////////////////////////////////////////////////////////
  // Activity Lifecycle
  ////////////////////////////////////////////////////////////

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    setContentView(R.layout.fare_detail);
    
    Intent i = getIntent();
    Fare fare = i.getParcelableExtra(HailActivity.KEY_FARE); 
    mFare = fare;

    populateUi(mFare);
  }

  private void populateUi(Fare fare) {
    mSourceText.setText(fare.source.toString());
    mDestinationText.setText(fare.destination.toString());

    mTimeLabel.setText(StringUtils.getNiceTime(fare.timeRequested));
    
    mPassengerLabel.setText(fare.status.toString());
  }
  
  ////////////////////////////////////////////////////////////
  // Main Methods
  ////////////////////////////////////////////////////////////

  public void onAcceptButtonClick(View v) {
    mFare.status = FareStatus.accepted;

    // TODO: PUT fare to server, letting people know that we're accepting it
    PutFareTask task = new PutFareTask(this, new PutFareListener(){
      @Override
      public void completed(Optional<Fare> fare) {
        setProgressBarIndeterminateVisibility(false);
        if (fare.isPresent()) {
          Toast.makeText(FareDetailActivity.this, "Fare Accepted!", Toast.LENGTH_SHORT).show();
          Intent i = new Intent(FareDetailActivity.this, DrivingActivity.class);
          i.putExtra(HailActivity.KEY_FARE, fare.get());
          startActivity(i);
          finish();
        } else {
          // Something happened. better not risk it
        }
      }

      @Override
      public void handleError(Throwable exception) {
        setProgressBarIndeterminateVisibility(false);
        goBlooey(exception);
      }
    });
    setProgressBarIndeterminateVisibility(true);
    task.execute(mFare);
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
