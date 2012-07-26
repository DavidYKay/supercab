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
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.tasks.RegisterTask;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.common.base.Optional;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RegisterTest {
  
  @Before
  public void runBeforeEveryTest() {
    Context context = Robolectric.application;
    PreferenceUtils prefs = new PreferenceUtils(context);
    prefs.saveCredentials("admin", "secret");
    prefs.saveToken("admin");
  }

  @After
  public void runAfterEveryTest() {
    Context context = Robolectric.application;
    PreferenceUtils prefs = new PreferenceUtils(context);
    prefs.saveCredentials(null, null);
  }


  @Test
  public void shouldRegisterUser() throws Exception {
    // create a signal to let us know when our task is done.
    final CountDownLatch signal = new CountDownLatch(1);

    PostUserListener listener = new PostUserListener() {

      @Override
      public void handleError(Throwable exception) {
        fail("received error: " + exception.getMessage());
        signal.countDown();
      }

      @Override
      public void completed(Optional<UserModel> user) {
        assertTrue(user.isPresent());
        signal.countDown();
      }
    };

    RegisterTask task = new RegisterTask(listener);
    
    UserModel userModel = new UserModel();
    
    task.execute(userModel);

    signal.await(10, TimeUnit.SECONDS);
  }

}
