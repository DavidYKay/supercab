package co.gargoyle.supercab.android;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.RoboGuice;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import co.gargoyle.supercab.android.activities.DrivingActivity;
import co.gargoyle.supercab.android.activities.FareDetailActivity;
import co.gargoyle.supercab.android.inject.SupercabTestModule;
import co.gargoyle.supercab.android.model.Fare;

import com.google.inject.util.Modules;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.matchers.StartedMatcher;

@RunWith(RobolectricTestRunner.class)
public class DriverNavTest {

  private FareDetailActivity activity;
  private Button pressMeButton;

  @Before
  public void setUp() throws Exception {
    RoboGuice.setBaseApplicationInjector(
        Robolectric.application,
        RoboGuice.DEFAULT_STAGE,
        Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(
            new SupercabTestModule()));

    Context context = Robolectric.application;
//    Injector injector = RoboGuice.getInjector(context);

    activity = new FareDetailActivity();

    Fare fare = FixtureFactory.createExampleFare();
    Intent intent = new Intent();
    intent.putExtra(co.gargoyle.supercab.android.utilities.Constants.KEY_FARE, fare);
    Robolectric.shadowOf(activity).setIntent(intent);
    activity.onCreate(null);
    pressMeButton = (Button) activity.findViewById(R.id.accept_fare);
  }

//  @Test
//  public void shouldStartNextActivityWhenButtonIsClicked() {
//
//    assertNotNull(activity);
//    assertNotNull(pressMeButton);
//
//    pressMeButton.performClick();
//    assertThat(activity, new StartedMatcher(DrivingActivity.class));
//  }  

}
