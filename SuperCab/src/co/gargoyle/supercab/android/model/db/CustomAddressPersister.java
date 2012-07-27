package co.gargoyle.supercab.android.model.db;

import java.io.IOException;
import java.sql.SQLException;

import android.location.Address;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.support.DatabaseResults;

public class CustomAddressPersister extends StringType {

  private static final CustomAddressPersister gSingleton = new CustomAddressPersister();

  private ObjectMapper mObjectMapper;
  private ObjectReader mObjectReader;
  @SuppressWarnings("unused")
  private ObjectWriter mObjectWriter;

  //@SuppressWarnings("deprecation")
  //private static final Timestamp ZERO_TIMESTAMP = new Timestamp(1970, 0, 0, 0, 0, 0, 0);

  private CustomAddressPersister() {
    super(SqlType.STRING, new Class<?>[] { Address.class });

    mObjectMapper = new ObjectMapper();
    //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    mapper.configure(DeserializationFeature., false);

    mObjectReader = mObjectMapper.reader(Address.class);
    mObjectWriter = mObjectMapper.writer();
  }

  public static CustomAddressPersister getSingleton() {
    return gSingleton;
  }
  
  @Override
  public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {

    String jsonString = results.getString(columnPos);
    Address address;
    try {
      address = mObjectReader.readValue(jsonString);
      return address;
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException { 
    Address address = (Address) javaObject;
    
    try {
      String jsonString =  mObjectWriter.writeValueAsString(address);
      return jsonString;
    } catch (JsonGenerationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return null;
    
//    return super.javaToSqlArg(fieldType, javaObject);
  }

  @Override
  public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
    // sqlArgToJava - Converts the SQL argument to the associated Java class.
    String result = results.getString(columnPos);
    return result;
  }

  @Override
  public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
    if (sqlArg == null) {
      return null;
    } else {
      String jsonString = (String) sqlArg;

      Address address;
      try {
        address = mObjectReader.readValue(jsonString);
        return address;
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return null;
    }
  }

}
