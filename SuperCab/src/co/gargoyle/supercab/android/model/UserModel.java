package co.gargoyle.supercab.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;


public class UserModel {

  @JsonProperty("id")
  public long superCabId;

  public UserType type;

  public String firstName;
  public String lastName;

  public String username;
  public String password;

  public UserProfile userProfile;

  @Override
  public boolean equals(Object obj) {
    if (obj == null)  {
      return false;
    }
    if (getClass() != obj.getClass())  {
      return false;
    }
    final UserModel other = (UserModel) obj;

    return   Objects.equal(this.lastName, other.lastName)
        &&   Objects.equal(this.firstName, other.firstName)
        &&   Objects.equal(this.username, other.username)
        &&   Objects.equal(this.superCabId, other.superCabId)
        &&   Objects.equal(this.userProfile, other.userProfile)
        &&   Objects.equal(this.type, other.type);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(superCabId, type, firstName, username, password, userProfile);
  }

  @Override
  public String toString() {
     return Objects.toStringHelper(this)
                .addValue(superCabId)
                .addValue(type)
                .addValue(firstName)
                .addValue(lastName)
                .addValue(username)
                .addValue(password)
                .addValue(userProfile)
                .toString();
  }

}
