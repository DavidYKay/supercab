package co.gargoyle.supercab.android.test;


import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.location.Address;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.IsolatedContext;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;
import co.gargoyle.supercab.android.enums.PointType;
import co.gargoyle.supercab.android.model.PickupPoint;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class DbSaveTest extends AndroidTestCase {

  private SCOrmLiteHelper mHelper;

  @Override
  protected void tearDown() throws Exception {
    if (mHelper != null) {
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

  public void testDb() {
    RuntimeExceptionDao<PickupPoint, Integer> dao = mHelper.getRuntimeDao(PickupPoint.class);

    Address iHub = new Address(Locale.getDefault());
    iHub.setLatitude(-1.29885);
    iHub.setLongitude(36.79089);
    iHub.setAddressLine(0,"iHub");

    PickupPoint original = new PickupPoint(PointType.PICKUP, iHub);
    dao.create(original);

    List<PickupPoint> pickups = dao.queryForAll();

    //PickupPoint persisted = dao.queryForFirst(preparedQuery);
    PickupPoint persisted = pickups.get(0);

    assertEquals(original, persisted);
  }
}
