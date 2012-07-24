package co.gargoyle.supercab.android.activities;

import java.util.Date;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.tasks.PostFareTask;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;

import com.google.common.base.Optional;

public class ConfirmationActivity extends RoboActivity implements PostFareListener {

  @InjectView(R.id.time_value) private TextView mTimeLabel;
  @InjectView(R.id.from_address) private TextView mFromLabel;
  @InjectView(R.id.to_address) private TextView mToLabel;
//  @InjectView(R.id.fare_status) private TextView mFareLabel;
  @InjectView(R.id.driver_status) private TextView mDriverLabel;

  private Fare mFare;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.confirmation);

    Intent i = getIntent();

    Fare fare = i.getParcelableExtra(HailActivity.KEY_FARE); 

    //DateFormat.getTimeFormat
    Date time = fare.getTimeRequested();
    CharSequence timeString = DateUtils.getRelativeTimeSpanString(
        time.getTime(),
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
        );

    mTimeLabel.setText(timeString);

    mFromLabel.setText(getAddressLine(fare.getSource()));
    mToLabel.setText(getAddressLine(fare.getDestination()));

    mFare = fare;
    
    mDriverLabel.setText("Uploading...");
    
    uploadFare(fare);
  }
  
  private String getAddressLine(PickupPoint point) {
    return point.getAddress().getAddressLine(0);
  }
  

  private void uploadFare(Fare fare) {
    PostFareTask task = new PostFareTask(this, this);
    task.execute(fare);
  }

  @Override
  public void completed(Optional<Long> fareId) {
    if (!fareId.isPresent()) {
      mDriverLabel.setText("Error! Try again.");
    } else {
      mFare.setSuperCabId(fareId.get());
      Toast.makeText(this,
                     "Finished uploading fare",
                     Toast.LENGTH_SHORT).show();
      mDriverLabel.setText("Awaiting driver...");
    }
  }

  @Override
  public void handleError(Throwable exception) {
    goBlooey(exception);
    
  }
  
  void goBlooey(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
  
    builder.setTitle("Exception!").setMessage(t.toString()).setPositiveButton("OK", null).show();
  }

}
