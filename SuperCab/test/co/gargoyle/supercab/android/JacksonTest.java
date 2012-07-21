package co.gargoyle.supercab.android;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.restlet.representation.Representation;

import co.gargoyle.supercab.android.utilities.ServerUtilities;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class JacksonTest {

  public static final String DK_PHONE_NUMBER = "+254727114825";

  @Test
  public void shouldConvertJSONCorrectly() throws Exception {
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("phone", DK_PHONE_NUMBER);

    Representation representation = ServerUtilities.convertMapToJsonRepresentation(params);

    String textDump = representation.getText();
    assertThat(textDump, equalTo("{phone=+254727114825}"));
    //    assertThat(preferenceUtils.getLastPhoneState() , is( TelephonyManager.CALL_STATE_IDLE));

  }
}
