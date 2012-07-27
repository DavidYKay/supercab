package co.gargoyle.supercab.android.test;

import android.content.Context;
import android.test.InstrumentationTestCase;
import co.gargoyle.supercab.android.database.SCOrmLiteHelper;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DbSaveTest extends InstrumentationTestCase {

  private SCOrmLiteHelper mDatabaseHelper;

  @Override
  protected void tearDown() throws Exception {
    if (mDatabaseHelper != null) {
      OpenHelperManager.releaseHelper();
      mDatabaseHelper = null;
    }
    super.tearDown();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    if (mDatabaseHelper == null) {
      Context context = getInstrumentation().getContext();
      mDatabaseHelper = OpenHelperManager.getHelper(context, SCOrmLiteHelper.class);
    }
  }

  public void testDb() {
    
    assertTrue(1 == 1);
  }
}
