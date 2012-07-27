package co.gargoyle.supercab.android.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Objects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@JsonInclude(value=Include.NON_NULL) 
@DatabaseTable(tableName = "users")
public class UserModel extends SuperCabBaseModel {

  @DatabaseField()
  public UserRole role;

  @DatabaseField
  public String firstName;
  @DatabaseField
  public String lastName;

  @DatabaseField
  public String username;
  @DatabaseField
  public String password;
  
  @DatabaseField
  public String phoneNumber;
  
  @DatabaseField
  public String token;
  
  public UserModel() {
    // required for ORMLite
  }

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
