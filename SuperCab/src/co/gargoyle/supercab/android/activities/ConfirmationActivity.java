package co.gargoyle.supercab.android.activities;

import java.util.Date;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;

public class ConfirmationActivity extends RoboActivity {

  @InjectView(R.id.time_value) private TextView mTimeLabel;
  @InjectView(R.id.from_address) private TextView mFromLabel;
  @InjectView(R.id.to_address) private TextView mToLabel;
  @InjectView(R.id.fare_status) private TextView mFareLabel;
  @InjectView(R.id.driver_status) private TextView mDriverLabel;
  
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
    
  }
  
  private String getAddressLine(PickupPoint point) {
    return point.getAddress().getAddressLine(0);
  }
  

}
