package co.gargoyle.supercab.android;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import co.gargoyle.supercab.android.model.UserProfile;
import co.gargoyle.supercab.android.tasks.PostUserTask;
import co.gargoyle.supercab.android.tasks.listeners.PostUserListener;

import com.google.common.base.Optional;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RegisterUserTest {

  @Test
  public void shouldUploadUser() throws Exception {
    Context context = Robolectric.application;

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
        // TODO Auto-generated method stub

      }
    };

    PostUserTask task = new PostUserTask(listener);

    
    UserProfile userProfile = new UserProfile();
    
    
    task.execute(userProfile);

    /*
     * The testing thread will wait here until the UI thread releases it above
     * with the countDown() or 30 seconds passes and it times out.
     */
    signal.await(30, TimeUnit.SECONDS);

  }

}
