
package co.gargoyle.supercab.android;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import co.gargoyle.supercab.android.activities.HailActivity;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GeoUtilsTest {

  public static final String DK_PHONE_NUMBER = "+254727114825";

  @Test
  public void shouldChangeStatesCorrectly() throws Exception {
    Context context = Robolectric.application;
    String hello = new HailActivity().getResources().getString(R.string.hello);
    assertThat(hello, equalTo("Hello World, MyActivity!"));
    //    assertThat(preferenceUtils.getLastPhoneState() , is( TelephonyManager.CALL_STATE_IDLE));

  }
}
