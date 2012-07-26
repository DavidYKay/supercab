package co.gargoyle.supercab.android;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.gargoyle.supercab.android.model.UserCredentials;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.tasks.GetUserTask;
import co.gargoyle.supercab.android.tasks.listeners.GetUserListener;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.common.base.Optional;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GetUserTest {

  private PreferenceUtils mSettings;
  
  @Before
  public void setup() {
    mSettings = new PreferenceUtils(Robolectric.application);
  }

  @Test
  public void shouldGetUser() throws Exception {
    // create  a signal to let us know when our task is done.
    final CountDownLatch signal = new CountDownLatch(1);

    GetUserListener listener = new GetUserListener() {

      @Override
      public void handleError(Throwable exception) {
        
        fail("received error: " + exception.getMessage());
        signal.countDown();
      }

      @Override
      public void completed(Optional<UserModel> user) {
        assertTrue(user.isPresent());
        mSettings.saveToken(user.get().token);

        signal.countDown();
      }
    };
    GetUserTask task = new GetUserTask(listener);
    
    UserCredentials credentials = new UserCredentials(
        "passenger",
        "passenger"
    );
    task.execute(credentials);
    
    /* The testing thread will wait here until the UI thread releases it
     * above with the countDown() or 30 seconds passes and it times out.
     */        
    signal.await(30, TimeUnit.SECONDS);

    assertTrue(mSettings.hasToken());
    
  }

}
