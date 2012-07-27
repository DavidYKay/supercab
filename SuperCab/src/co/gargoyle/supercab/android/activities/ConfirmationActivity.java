package co.gargoyle.supercab.android.activities;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.activities.parent.AbstractMapActivity;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.tasks.PostFareTask;
import co.gargoyle.supercab.android.tasks.PutFareTask;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;
import co.gargoyle.supercab.android.tasks.listeners.PutFareListener;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.google.common.base.Optional;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

public class ConfirmationActivity extends AbstractMapActivity implements PostFareListener {

  private static final int PROGRESS_DIALOG = 1;

  @InjectView(R.id.time_value) private TextView mTimeLabel;
  @InjectView(R.id.from_address) private TextView mFromLabel;
  @InjectView(R.id.to_address) private TextView mToLabel;
  @InjectView(R.id.driver_status) private TextView mDriverLabel;

  private Fare mFare;
  private SCOrmLiteHelper databaseHelper;

  private HashSet<AsyncTask> mTasks = new HashSet<AsyncTask>();

  protected ProgressDialog mProgressDialog;

  ////////////////////////////////////////////////////////////
  // Activity Lifecycle
  ////////////////////////////////////////////////////////////
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.confirmation);

    Intent i = getIntent();
    mFare = i.getParcelableExtra(HailActivity.KEY_FARE);

    Date time = mFare.timeRequested;
    CharSequence timeString = StringUtils.getNiceTime(time);

    mTimeLabel.setText(timeString);

    mFromLabel.setText(getAddressLine(mFare.source));
    mToLabel.setText(getAddressLine(mFare.destination));

    mDriverLabel.setText("Uploading...");

    uploadFare(mFare);
  }

  ////////////////////////////////////////////////////////////
  // UI Callbacks
  ////////////////////////////////////////////////////////////

  public void onCancelButtonClick(View v) {
    cancelFare();
  }

  ////////////////////////////////////////////////////////////
  // Main Methods
  ////////////////////////////////////////////////////////////

  private void deleteFareFromDb(Fare fare) {
    RuntimeExceptionDao<Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);

    // delete all fares with the matching id
    //dao.deleteById(fare.id);

    DeleteBuilder<Fare, Integer> builder = dao.deleteBuilder();
    try {
      dao.delete(builder.prepare());
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void cancelFare() {
    // Tell the API we're done
    mFare.status = FareStatus.cancelled;

    // PUT fare to server, letting people know that we're cancelling it
    final PutFareTask task = new PutFareTask(this, new PutFareListener() {
      @Override
      public void completed(Optional<Fare> fare) {
        if (fare.isPresent() && fare.get().status == FareStatus.cancelled) {
          Toast.makeText(ConfirmationActivity.this, "Fare Cancelled!", Toast.LENGTH_SHORT).show();
          // clear out the fare from the DB
          deleteFareFromDb(fare.get());

          // back to the main screen
          startActivity(new Intent(ConfirmationActivity.this, HailActivity.class));
          finish();
        } else {
          // Something happened. better not risk it
        }
        mTasks.remove(this);
        mProgressDialog.dismiss();
      }

      @Override
      public void handleError(Throwable exception) {
        goBlooey(exception);
      }
    });

    mTasks.add(task);
    task.execute(mFare);
    showDialog(PROGRESS_DIALOG);
  }

  private String getAddressLine(PickupPoint point) {
    if (point != null && point.address != null) {
      return point.address;
    } else {
      return "Unknown";
    }
  }

  private void uploadFare(Fare fare) {
    PostFareTask task = new PostFareTask(this, this);
    task.execute(fare);
  }

  @Override
  public void completed(Optional<String> fareId) {
    if (!fareId.isPresent()) {
      mDriverLabel.setText("Error! Try again.");
    } else {
      mFare.superCabId = fareId.get();
      Toast.makeText(this,
                     "Finished uploading fare",
                     Toast.LENGTH_SHORT).show();
      mDriverLabel.setText("Awaiting driver...");
    }
  }

  ////////////////////////////////////////////////////////////
  // Error Handling
  ////////////////////////////////////////////////////////////

  @Override
  public void handleError(Throwable exception) {
    goBlooey(exception);

  }

  void goBlooey(Throwable t) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Exception!").setMessage(t.toString()).setPositiveButton("OK", null).show();
  }

  ////////////////////////////////////////////////////////////
  // Overrides
  ////////////////////////////////////////////////////////////

  @Override
  protected boolean isRouteDisplayed() {
    // TODO Auto-generated method stub
    return false;
  }

  ////////////////////////////////////////////////////////////
  // ORMLite
  ////////////////////////////////////////////////////////////

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
  // AlertDialogs
  ////////////////////////////////////////////////////////////

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
    case PROGRESS_DIALOG:
      mProgressDialog = new ProgressDialog(this);
      DialogInterface.OnClickListener loadingButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          // End the tasks
        }
      };
      mProgressDialog.setTitle(getString(R.string.uploading_data));
      mProgressDialog.setMessage(getString(R.string.cancelling_fare));
      mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
      mProgressDialog.setIndeterminate(true);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setButton(getString(R.string.cancel),
          loadingButtonListener);
      return mProgressDialog;
    }
    return null;
  }

}
