package co.gargoyle.supercab.android.activities;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.restlet.resource.ResourceException;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.activities.parent.AbstractMapActivity;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.tasks.GetFareTask;
import co.gargoyle.supercab.android.tasks.PostFareTask;
import co.gargoyle.supercab.android.tasks.PutFareTask;
import co.gargoyle.supercab.android.tasks.listeners.GetFareListener;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;
import co.gargoyle.supercab.android.tasks.listeners.PutFareListener;
import co.gargoyle.supercab.android.utilities.Constants;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;

public class ConfirmationActivity extends AbstractMapActivity {

  private static final int PROGRESS_DIALOG = 1;

  @InjectView(R.id.time_value) private TextView mTimeLabel;
  @InjectView(R.id.from_address) private TextView mFromLabel;
  @InjectView(R.id.to_address) private TextView mToLabel;
  @InjectView(R.id.driver_status) private TextView mDriverLabel;

  private Fare mFare;
  private SCOrmLiteHelper databaseHelper;

  @SuppressWarnings("rawtypes")
  private HashSet<AsyncTask> mTasks = new HashSet<AsyncTask>();

  protected ProgressDialog mProgressDialog;

  @Inject protected PreferenceUtils mPreferenceUtils;

  ////////////////////////////////////////////////////////////
  // Activity Lifecycle
  ////////////////////////////////////////////////////////////
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    setContentView(R.layout.confirmation);

    Intent i = getIntent();
    mFare = i.getParcelableExtra(Constants.KEY_FARE);

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
  // Menus
  ////////////////////////////////////////////////////////////

  private static final int MENU_LOGOUT = Menu.FIRST;
  private static final int MENU_REFRESH = Menu.FIRST + 1;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    menu.add(0, MENU_LOGOUT, 0, getString(R.string.logout))
        .setIcon(android.R.drawable.ic_menu_delete);
    menu.add(0, MENU_REFRESH, 1, getString(R.string.refresh)).setIcon(
        R.drawable.ic_menu_refresh);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_LOGOUT:
        //logout();
        return true;
      case MENU_REFRESH:
        refresh();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onOptionsMenuClosed(Menu menu) {
    super.onOptionsMenuClosed(menu);
  }

  ////////////////////////////////////////////////////////////
  // Main Methods
  ////////////////////////////////////////////////////////////

  private void setMode(FareStatus status) {
    if (status == FareStatus.cancelled) {
      onFareCancelled();
    } else {
      updateFareStatus(status);

      // Update the GUI
      mDriverLabel.setText(getString(sTextForMode.get(status)));
    }
  }

  private void refresh() {
    GetFareTask task = new GetFareTask(this, new GetFareListener() {
      @Override
      public void completed(Optional<Fare> fare) {
        setProgressBarIndeterminateVisibility(false);
        if (!fare.isPresent()) {
          mDriverLabel.setText("Error! Try again.");
        } else {
          mFare = fare.get();
          updateFare(mFare);

          setMode(mFare.status);
          Toast.makeText(ConfirmationActivity.this, "Finished refreshing fare.", Toast.LENGTH_SHORT).show();
        }
      }

    @Override
    public void handleError(Throwable exception) {
      setProgressBarIndeterminateVisibility(false);
      handleThrowable(exception);
    }

    @Override
    public void unauthorized() {
      onUnauthorizedCrazyState();
//      logout();
    }

    });

    setProgressBarIndeterminateVisibility(true);
    task.execute(mFare.superCabId);
  }

  private void updateFareStatus(FareStatus status) {
    if (!mFare.status.equals(status)) {
      mFare.status = status;
      updateFare(mFare);
    }
  }

  private void updateFare(Fare fare) {
    RuntimeExceptionDao<Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);
    dao.update(fare);
  }

  // TODO: Unify this logic
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

  // TODO: Unify with DrivingActivity code
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
        handleThrowable(exception);
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
    PostFareTask task = new PostFareTask(this, new PostFareListener() {
      @Override
      public void completed(Optional<String> fareId) {
        setProgressBarIndeterminateVisibility(false);
        if (!fareId.isPresent()) {
          mDriverLabel.setText("Error! Try again.");
        } else {
          mFare.superCabId = fareId.get();
          updateFare(mFare);

          setMode(FareStatus.waiting);
          Toast.makeText(ConfirmationActivity.this, "Finished uploading fare.", Toast.LENGTH_SHORT).show();
        }
      }

    @Override
    public void handleError(Throwable exception) {
      setProgressBarIndeterminateVisibility(false);
      handleThrowable(exception);
    }

    });
    setProgressBarIndeterminateVisibility(true);
    task.execute(fare);
  }

  ////////////////////////////////////////////////////////////
  // Error Handling
  ////////////////////////////////////////////////////////////

  public void onUnauthorizedCrazyState() {
    Toast.makeText(getApplicationContext(),
                   "Your app is now in an invalid state! Please contact customer support.",
                   Toast.LENGTH_LONG);
    logout();
  }

  void handleThrowable(Throwable t) {
    if (t instanceof ResourceException) {
      ResourceException resEx = (ResourceException) t;
      if (resEx.getStatus().getCode() == 401) {
        // Something crazy happened
        onUnauthorizedCrazyState();
      } else {
        goBlooey(t);
      }
    } else {
      goBlooey(t);
    }

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
  // Nav
  ////////////////////////////////////////////////////////////
  
  private void onFareCancelled() {
    deleteFareFromDb(mFare);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    AlertDialog dialog = builder.setTitle("Cancelled!")
        .setMessage(R.string.driver_cancelled_fare)
        .setPositiveButton("OK", null)
        .create();
    dialog.setOnDismissListener(new OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialog) {
        Intent i = new Intent(ConfirmationActivity.this, HailActivity.class);
        startActivity(i);

        finish();
      }
    });
    dialog.show();

    //Toast.makeText(ConfirmationActivity.this, "Finished refreshing fare.", Toast.LENGTH_SHORT).show();
    //finish();
  }

  private void logout() {
    RuntimeExceptionDao <UserModel, Integer> dao = getHelper().getRuntimeDao(UserModel.class);
    DeleteBuilder<UserModel, Integer> builder = dao.deleteBuilder();
    PreparedDelete<UserModel> deleteAll;
    try {
      deleteAll = builder.prepare();
      dao.delete(deleteAll);
      mPreferenceUtils.clearUser();

      startActivity(new Intent(ConfirmationActivity.this, LoginActivity.class));
      finish();
    } catch (SQLException e) {
      e.printStackTrace();
      goBlooey(e);
    }
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


  private static final HashMap<FareStatus, Integer> sTextForMode = new HashMap<FareStatus, Integer>();

  static {
    sTextForMode.put(FareStatus.waiting, R.string.mode_passenger_waiting);
    sTextForMode.put(FareStatus.accepted, R.string.mode_passenger_accepted);
    sTextForMode.put(FareStatus.active, R.string.mode_passenger_active);
    sTextForMode.put(FareStatus.complete, R.string.mode_passenger_complete);
  }
}
