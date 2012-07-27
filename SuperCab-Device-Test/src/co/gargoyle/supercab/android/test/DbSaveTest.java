package co.gargoyle.supercab.android.test;


import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.test.AndroidTestCase;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class DbSaveTest extends AndroidTestCase {

  private SCOrmLiteHelper mHelper;

  @Override
  protected void tearDown() throws Exception {
    if (mHelper != null) {
      SQLiteDatabase db = mHelper.getWritableDatabase();
      mHelper.onUpgrade(db, 1, 1);
      
      OpenHelperManager.releaseHelper();
      mHelper = null;
    }
    super.tearDown();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    if (mHelper == null) {

//      Context context = getInstrumentation().getContext();
//      ContentResolver resolver = context.getContentResolver();
//      IsolatedContext isoContext = new IsolatedContext(resolver, context);

      mHelper = OpenHelperManager.getHelper(getContext(), SCOrmLiteHelper.class);
    }
  }

  public void testPointSave() {
    RuntimeExceptionDao<PickupPoint, Integer> dao = mHelper.getRuntimeDao(PickupPoint.class);

    Address iHub = new Address(Locale.getDefault());
    iHub.setLatitude(-1.29885);
    iHub.setLongitude(36.79089);
    iHub.setAddressLine(0,"iHub");

    PickupPoint original = new PickupPoint(PointType.PICKUP, iHub);
    dao.create(original);

    List<PickupPoint> pickups = dao.queryForAll();

    PickupPoint persisted = pickups.get(0);

    assertEquals(original, persisted);
  }
  
  public void testFareSave() {
    RuntimeExceptionDao<Fare, Integer> dao = mHelper.getRuntimeDao(Fare.class);

    Fare original = new Fare();
    original.source = new PickupPoint(PointType.PICKUP, Constants.ADDRESS_IHUB);
    original.destination = new PickupPoint(PointType.DROPOFF, Constants.ADDRESS_AIRPORT);
    original.timeRequested = new Date(Constants.FEB_13_2009);
    original.status = FareStatus.waiting;

    dao.create(original);

    List<Fare> fares = dao.queryForAll();

    assertTrue(fares.size() == 1);
    //Fare persisted = dao.queryForFirst(preparedQuery);
    Fare persisted = fares.get(0);

    RuntimeExceptionDao<PickupPoint, Integer> pointDao = mHelper.getRuntimeDao(PickupPoint.class);
    List<PickupPoint> points = pointDao.queryForAll();
    assertTrue(points.size() == 2);

    pointDao.refresh(persisted.source);
    pointDao.refresh(persisted.destination);

    assertEquals(original, persisted);
  }
}
