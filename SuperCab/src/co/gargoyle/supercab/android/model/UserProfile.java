package co.gargoyle.supercab.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;


public class UserProfile {

  @JsonProperty("phoneNumber")
  public String phoneNumber;

  @Override
  public boolean equals(Object obj) {
    if (obj == null)  {
      return false;
    }
    if (getClass() != obj.getClass())  {
      return false;
    }
    final UserProfile other = (UserProfile) obj;

    return   Objects.equal(this.phoneNumber, other.phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(phoneNumber);
  }

  @Override
  public String toString() {
     return Objects.toStringHelper(this)
                .addValue(this.phoneNumber)
                .toString();
  }

}
