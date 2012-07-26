package co.gargoyle.supercab.android;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class JacksonTest {

  public static final String DK_PHONE_NUMBER = "+254727114825";

  //@Test
  //public void shouldConvertPhoneCorrectly() throws Exception {
  //  HashMap<String, String> params = new HashMap<String, String>();
  //  params.put("phone", DK_PHONE_NUMBER);

  //  Representation representation = ServerUtilities.convertMapToJsonRepresentation(params);

  //  String textDump = representation.getText();
  //  assertThat(textDump, equalTo("{phone=+254727114825}"));
  //  //    assertThat(preferenceUtils.getLastPhoneState() , is( TelephonyManager.CALL_STATE_IDLE));

  //}
  //
  //@Test
  //public void shouldConvertFareCorrectly() throws Exception {
  //  Fare fare = FixtureFactory.createExampleFare();
  //  Representation representation = ServerUtilities.convertFareToJsonRepresentation(fare);

  //  String textDump = representation.getText();
  //  assertThat(textDump, equalTo("{phone=+254727114825}"));

  //}
  
  @Test
  public void shouldConvertUserModelCorrectly() throws Exception {
    String expected = FixtureFactory.readFileAsString("../SuperCab-Test/fixtures/user.json");
//    String expected = FixtureFactory.readFileAsString("../SuperCab-Test/fixtures/user-simplified.json");
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    ObjectReader reader =  mapper.reader(UserModel.class);
    UserModel user = reader.readValue(expected);

    ObjectWriter writer = mapper.writer();
    String result = writer.writeValueAsString(user);
    
    assertFalse(StringUtils.stringIsEmpty(result));

    UserModel finalExpected = reader.readValue(expected);
    UserModel finalResult = reader.readValue(result);

    assertNotNull(finalResult.firstName);
    assertNotNull(finalResult.lastName);

    assertNotNull(finalResult.username);
//    assertNotNull(finalResult.password);
    
    assertNotNull(finalResult.phoneNumber);
    
    assertThat(finalExpected, is(equalTo(finalResult)));

  }
  
  
  
}
