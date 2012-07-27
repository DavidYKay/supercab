package co.gargoyle.supercab.android;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.model.PickupPoint;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class OrmliteTest {

  private SCOrmLiteHelper mHelper;

  @Before 
  public void setUp() {
    if (mHelper == null) {
      mHelper = OpenHelperManager.getHelper(Robolectric.application, SCOrmLiteHelper.class);
    }
  }

  @After
  public void tearDown() {
    if (mHelper != null) {
      SQLiteDatabase db = mHelper.getWritableDatabase();
      mHelper.onUpgrade(db, 1, 1);
      
      OpenHelperManager.releaseHelper();
      mHelper = null;
    }
  }

  @Test
  public void shouldPersistPickupPoint() throws Exception {
    RuntimeExceptionDao<PickupPoint, Integer> dao = mHelper.getRuntimeDao(PickupPoint.class);

    Address iHub = new Address(Locale.getDefault());
    iHub.setLatitude(-1.29885); 
    iHub.setLongitude(36.79089);
    iHub.setAddressLine(0,"iHub");

    PickupPoint original = new PickupPoint(PointType.PICKUP, iHub);
    dao.create(original);

 	

    // get our query builder from the DAO
//    QueryBuilder<PickupPoint, Integer> queryBuilder = dao.queryBuilder();

    // the 'password' field must be equal to "qwerty"
    //queryBuilder.where().eq(Account.PASSWORD_FIELD_NAME, "qwerty");

    // prepare our sql statement
//    PreparedQuery<PickupPoint> preparedQuery = queryBuilder.prepare();
    // query for all accounts that have "qwerty" as a password
    //List<Account> accountList = accountDao.query(preparedQuery);

    List<PickupPoint> pickups = dao.queryForAll();

    //PickupPoint persisted = dao.queryForFirst(preparedQuery);
    PickupPoint persisted = pickups.get(0);

    assertThat(original, is(equalTo(persisted)));

    //Address airport = new Address(Locale.getDefault());
    //airport.setLatitude(-1.331);
    //airport.setLongitude(36.9251);
    //airport.setAddressLine(0, "Jomo Kenyatta Airport");
    //PickupPoint destination = new PickupPoint(FareType.DROPOFF, airport);
    //dao.create(destination);

  }

}
