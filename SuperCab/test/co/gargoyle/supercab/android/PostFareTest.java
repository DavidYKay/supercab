package co.gargoyle.supercab.android;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.location.Address;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.tasks.PostFareTask;
import co.gargoyle.supercab.android.tasks.listeners.PostFareListener;

import com.google.common.base.Optional;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PostFareTest {

  @Test
  public void shouldUploadFare() throws Exception {
    Context context = Robolectric.application;
        
    // create  a signal to let us know when our task is done.
    final CountDownLatch signal = new CountDownLatch(1);


//    final Optional<Long> resultFareId;
    PostFareListener listener = new PostFareListener() {
      @Override
      public void completed(Optional<Long> fareId) {
        assertTrue(fareId.isPresent());
        //if (fareId.isPresent()) {
        //  // Sweet victory
        //  //resultFareId = fareId;
        //} else {
        //  fail("did not receive a fareId back!");
        //}

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

    // Execute the async task on the UI thread! THIS IS KEY!
    //runTestOnUiThread(new Runnable() {
    //  @Override
    //  public void run() {
    //    task.execute(fare);
    //  }
    //});       
    
    task.execute(fare);
    
    /* The testing thread will wait here until the UI thread releases it
     * above with the countDown() or 30 seconds passes and it times out.
     */        
    signal.await(30, TimeUnit.SECONDS);
    
  }

}
