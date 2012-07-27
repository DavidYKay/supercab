package co.gargoyle.supercab.android;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.model.UserRole;
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

    final UserModel sean = new UserModel();
    sean.role = UserRole.passenger;
    sean.firstName = "Sean";
    sean.lastName = "Smith";

    sean.username = "seansmith";
    sean.password = "seansmith";

    sean.phoneNumber = "+254727114825";

    //sean.phoneNumber = "0727114825";

    PostUserListener listener = new PostUserListener() {

      @Override
      public void handleError(Throwable exception) {
        fail("received error: " + exception.getMessage());
        signal.countDown();
      }

      @Override
      public void completed(Optional<UserModel> userOpt) {
        assertTrue(userOpt.isPresent());

        UserModel user = userOpt.get();
        assertThat(user.role, is(equalTo(sean.role)));
        assertThat(user.firstName, is(equalTo(sean.firstName)));
        assertThat(user.lastName, is(equalTo(sean.lastName)));

        assertThat(user.username, is(equalTo(sean.username)));
//        assertThat(user.password, is(equalTo(sean.password)));

        assertThat(user.phoneNumber, is(equalTo(sean.phoneNumber)));

        signal.countDown();
      }
    };

    RegisterTask task = new RegisterTask(listener);
    task.execute(sean);

    signal.await(10, TimeUnit.SECONDS);
  }

}
