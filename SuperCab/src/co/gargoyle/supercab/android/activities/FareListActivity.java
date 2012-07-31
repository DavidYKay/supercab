package co.gargoyle.supercab.android.activities;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboListActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import co.gargoyle.supercab.android.R;
import co.gargoyle.supercab.android.adapters.FareListAdapter;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.tasks.GetFaresTask;
import co.gargoyle.supercab.android.tasks.listeners.GetFaresListener;
import co.gargoyle.supercab.android.utilities.BroadcastUtils;
import co.gargoyle.supercab.android.utilities.Constants;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class FareListActivity extends RoboListActivity {

  @SuppressWarnings("unused")
  @Inject private BroadcastUtils mBroadcastUtils;

  @Inject private PreferenceUtils mPreferenceUtils;

  ////////////////////////////////////////////////////////////
  // Activity Lifecycle
  ////////////////////////////////////////////////////////////

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Optional<Fare> activeFare = getFareFromDb();
    if (activeFare.isPresent()) {
      proceedToDriving(activeFare.get());
    } else {
      requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

      setContentView(R.layout.fare_list);

      //setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

      getData();
    }
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    ListAdapter adapter = getListAdapter();

    Fare fare = (Fare) adapter.getItem(position);

    Intent i = new Intent(FareListActivity.this, FareDetailActivity.class);
    i.putExtra(Constants.KEY_FARE, fare);
    startActivity(i);
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
        logout();
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

  private void logout() {
    RuntimeExceptionDao <UserModel, Integer> dao = getHelper().getRuntimeDao(UserModel.class);
    DeleteBuilder<UserModel, Integer> builder = dao.deleteBuilder();
    PreparedDelete<UserModel> deleteAll;
    try {
      deleteAll = builder.prepare();
      dao.delete(deleteAll);
      mPreferenceUtils.clearUser();

      startActivity(new Intent(FareListActivity.this, LoginActivity.class));
      finish();
    } catch (SQLException e) {
      e.printStackTrace();
      goBlooey(e);
    }
  }

  private void refresh() {
    getData();
  }

  @SuppressWarnings("unchecked")
  private void getData() {
    GetFaresTask task = new GetFaresTask(this, new GetFaresListener() {

      @Override
      public void completed(List<Fare> fares) {
        setListAdapter(new FareListAdapter(getLayoutInflater(), fares));
        setProgressBarIndeterminateVisibility(false);
      }

      @Override
      public void handleError(Throwable throwable) {
        setProgressBarIndeterminateVisibility(false);
        goBlooey(throwable);
      }

      @Override
      public void unauthorized() {
        setProgressBarIndeterminateVisibility(false);
        Toast.makeText(FareListActivity.this, "Login was bad!", Toast.LENGTH_LONG).show();
        logout();
      }

    });

    setProgressBarIndeterminateVisibility(true);
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("status", FareStatus.waiting);
    task.execute(params);
  }

  ////////////////////////////////////////////////////////////
  // Navigation
  ////////////////////////////////////////////////////////////

  private void proceedToDriving(Fare fare) {
    Intent i = new Intent(FareListActivity.this, DrivingActivity.class);
    i.putExtra(Constants.KEY_FARE_ID, fare.id);
    startActivity(i);

    finish();
  }

  ////////////////////////////////////////////////////////////
  // Fare
  ////////////////////////////////////////////////////////////

  private Optional<Fare> getFareFromDb() {
    RuntimeExceptionDao <Fare, Integer> dao = getHelper().getRuntimeDao(Fare.class);

    QueryBuilder<Fare, Integer> builder = dao.queryBuilder();

    Where<Fare, Integer> where = builder.where();
    try {
      where.in("status", FareStatus.accepted, FareStatus.active);
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
