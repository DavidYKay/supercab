package co.gargoyle.supercab.android.activities;

import java.sql.SQLException;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.utilities.BroadcastUtils;
import co.gargoyle.supercab.android.utilities.Constants;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class FareCompleteActivity extends RoboActivity {
  
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

    setContentView(R.layout.fare_complete);
    
    Intent i = getIntent();
    long fareId = i.getIntExtra(Constants.KEY_FARE_ID, -1);
    if (fareId == -1) {
      onCouldNotFindFare();
    } else {
      Optional<Fare> fare = getFareFromDb(fareId);
      if (!fare.isPresent()) {
        onCouldNotFindFare();
      } else {
        mFare = fare.get();
      }
    }

    populateUi(mFare);
  }

  private void populateUi(Fare fare) {
//    mSourceText.setText(fare.source.toString());
//    mDestinationText.setText(fare.destination.toString());
//
//    mTimeLabel.setText(StringUtils.getNiceTime(fare.timeRequested));
//    
//    mPassengerLabel.setText(fare.status.toString());
  }
  
  ////////////////////////////////////////////////////////////
  // Main Methods
  ////////////////////////////////////////////////////////////

  private void onCouldNotFindFare() {
    Toast.makeText(FareCompleteActivity.this, "Error! No fare found.", Toast.LENGTH_LONG).show();
    finish();
  }

  ////////////////////////////////////////////////////////////
  // Database
  ////////////////////////////////////////////////////////////

  private Optional<Fare> getFareFromDb(long fareId) {
    // get the fare from the DB.
    RuntimeExceptionDao<Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);

    QueryBuilder<Fare, Integer> builder = dao.queryBuilder();

    Where<Fare, Integer> where = builder.where();
    try {
      where.eq("id", fareId);
      builder.setWhere(where);

      // get all fares that are waiting
      List<Fare> fares = dao.query(builder.prepare());
      if (fares.size() > 0) {
        Fare fare = fares.get(0);
        dao.refresh(fare);
        return Optional.of(fare);
      } else {
        return Optional.absent();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Optional.absent();
  }
//  private int saveFareToDb(Fare fare) {
//    RuntimeExceptionDao<Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);
//    return dao.create(fare);
//  }

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
