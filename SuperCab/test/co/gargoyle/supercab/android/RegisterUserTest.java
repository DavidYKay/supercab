package co.gargoyle.supercab.android;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import co.gargoyle.supercab.android.model.UserProfile;
import co.gargoyle.supercab.android.tasks.PostUserTask;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RegisterUserTest {
  
  @Before
  public void runBeforeEveryTest() {
    Context context = Robolectric.application;
    PreferenceUtils prefs = new PreferenceUtils(context);
    prefs.saveCredentials(
        "admin",
        "secret");

  }

  @After
  public void runAfterEveryTest() {
    Context context = Robolectric.application;
    PreferenceUtils prefs = new PreferenceUtils(context);
    prefs.saveCredentials(null, null);
  }


  @Test
  public void shouldUploadUser() throws Exception {
//    Context context = Robolectric.application;

    // create a signal to let us know when our task is done.
    final CountDownLatch signal = new CountDownLatch(1);

    PostUserListener listener = new PostUserListener() {

      @Override
      public void handleError(Throwable exception) {
        fail("received error: " + exception.getMessage());
        signal.countDown();
      }

      @Override
      public void completed(Boolean success) {
        assertTrue(success);
        signal.countDown();
      }
    };

    PostUserTask task = new PostUserTask(listener);
    
    UserProfile userProfile = new UserProfile();
    
    task.execute(userProfile);

    signal.await(10, TimeUnit.SECONDS);
  }

}
