package co.gargoyle.supercab.android;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.location.Address;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.tasks.PostFareTask;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;
import co.gargoyle.supercab.android.utilities.PreferenceUtils;

import com.google.common.base.Optional;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PostFareTest {

  @Before
  public void runBeforeEveryTest() {
    Context context = Robolectric.application;
    PreferenceUtils prefs = new PreferenceUtils(context);
    prefs.saveCredentials(
        "passenger",
        "passenger");
    //prefs.saveToken("passenger");
    prefs.saveToken("9e2b5e29-b37c-4df2-bee3-6290c14be56b");
  }

  @After
  public void runAfterEveryTest() {
    Context context = Robolectric.application;
    PreferenceUtils prefs = new PreferenceUtils(context);
    prefs.saveCredentials(null, null);
  }


  @Test
  public void shouldUploadFare() throws Exception {
    Context context = Robolectric.application;
        
    // create  a signal to let us know when our task is done.
    final CountDownLatch signal = new CountDownLatch(1);


    PostFareListener listener = new PostFareListener() {
      @Override
      public void completed(Optional<String> fareId) {
        assertTrue(fareId.isPresent());
        signal.countDown();
      }

      @Override
      public void handleError(Throwable exception) {
        
        fail("received error: " + exception.getMessage());
        signal.countDown();
      }
    };
    PostFareTask task = new PostFareTask(context, listener);

    Address pickupAddress  = Constants.ADDRESS_IHUB;
    Address dropoffAddress = Constants.ADDRESS_AIRPORT;

    PickupPoint pickup = new PickupPoint(FareType.PICKUP, pickupAddress);
    PickupPoint dropoff = new PickupPoint(FareType.DROPOFF, dropoffAddress);
    Date time = new Date(Constants.FEB_13_2009);
    final Fare fare = new Fare(pickup, dropoff, time);

    task.execute(fare);
    
    signal.await(30, TimeUnit.SECONDS);
    
  }

}
