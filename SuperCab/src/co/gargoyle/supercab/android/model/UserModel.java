package co.gargoyle.supercab.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;


public class UserModel {

  @JsonProperty("_id")
  public String superCabId;

  public UserRole role;

  public String firstName;
  public String lastName;

  public String username;
  public String password;
  
  public String phoneNumber;

  @Override
  public boolean equals(Object obj) {
    if (obj == null)  {
      return false;
    }
    if (getClass() != obj.getClass())  {
      return false;
    }
    final UserModel other = (UserModel) obj;

    return   
        Objects.equal(this.lastName, other.lastName)
        &&   Objects.equal(this.firstName, other.firstName)
        &&   Objects.equal(this.username, other.username)
        &&   Objects.equal(this.superCabId, other.superCabId)
        &&   Objects.equal(this.phoneNumber, other.phoneNumber)
        &&   Objects.equal(this.role, other.role);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(superCabId, role, firstName, username, password, phoneNumber);
  }

  @Override
  public String toString() {
     return Objects.toStringHelper(this)
                .addValue(superCabId)
                .addValue(role)
                .addValue(firstName)
                .addValue(lastName)
                .addValue(username)
                .addValue(password)
                .addValue(phoneNumber)
                .toString();
  }

}
