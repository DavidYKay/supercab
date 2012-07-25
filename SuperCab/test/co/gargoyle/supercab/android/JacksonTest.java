package co.gargoyle.supercab.android;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.gargoyle.supercab.android.model.ApiResponse;
import co.gargoyle.supercab.android.model.UserModel;
import co.gargoyle.supercab.android.utilities.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
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
    //String expected = FixtureFactory.readFileAsString("../SuperCab-Test/fixtures/user.json");
    String expected = FixtureFactory.readFileAsString("../SuperCab-Test/fixtures/user-simplified.json");
    
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
    
    assertNotNull(finalResult.userProfile);
    
    assertThat(finalExpected, is(equalTo(finalResult)));

  }
  
  @Test
  public void shouldConvertApiUserModelCorrectly() throws Exception {
    String expected = FixtureFactory.readFileAsString("../SuperCab-Test/fixtures/user.json");
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
 
    TypeFactory factory = mapper.getTypeFactory();
    TypeReference<ApiResponse<UserModel>> ref = new TypeReference<ApiResponse<UserModel>>() { };
    JavaType responseType = factory.constructType(ref);

    ObjectReader reader =  mapper.reader(responseType);
    ApiResponse<UserModel> user = reader.readValue(expected);

    ObjectWriter writer = mapper.writer();
    String result = writer.writeValueAsString(user);
    
    assertFalse(StringUtils.stringIsEmpty(result));
    
    ApiResponse<UserModel> expectedResponse = reader.readValue(expected);
    ApiResponse<UserModel> resultResponse   = reader.readValue(result);
    
    UserModel finalResult   = (UserModel) resultResponse.objects[0];
    UserModel finalExpected = (UserModel) expectedResponse.objects[0];
    
    assertNotNull(finalResult.firstName);
    assertNotNull(finalResult.lastName);

    assertNotNull(finalResult.username);
    
    assertNotNull(finalResult.userProfile);

    assertThat(finalExpected, is(equalTo(finalResult)));
  }
  
}
