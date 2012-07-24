package co.gargoyle.supercab.android.model;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.json.CustomAddressSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;

public class UserProfile {

  public UserType type;

  public String firstName;
  public String lastName;

  public String email;
  
  public String username;
  public String password;

}
