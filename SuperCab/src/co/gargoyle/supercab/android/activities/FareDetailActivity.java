package co.gargoyle.supercab.android.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.tasks.PutFareTask;
import co.gargoyle.supercab.android.tasks.listeners.PutFareListener;
import co.gargoyle.supercab.android.utilities.BroadcastUtils;
import co.gargoyle.supercab.android.utilities.Constants;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class FareDetailActivity extends RoboActivity {
  
  protected static final String TAG = "FareDetailActivity";
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    setContentView(R.layout.fare_detail);
    
    Intent i = getIntent();
    Fare fare = i.getParcelableExtra(Constants.KEY_FARE); 
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

          // Save that shit to disk
          saveFareToDb(fare.get());

          Toast.makeText(FareDetailActivity.this, "Fare Accepted!", Toast.LENGTH_SHORT).show();
          Intent i = new Intent(FareDetailActivity.this, DrivingActivity.class);
          i.putExtra(Constants.KEY_FARE_ID, fare.get().id);
          startActivity(i);
          finish();
        } else {
          // Something happened. better not risk it
          Log.e(TAG, "Failed to receive fare back!");
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
  // Database
  ////////////////////////////////////////////////////////////

  private int saveFareToDb(Fare fare) {
    RuntimeExceptionDao<Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);
    return dao.create(fare);
  }

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
