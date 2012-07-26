package co.gargoyle.supercab.android.model;

public enum UserRole {

  driver("driver"),
  passenger("passenger");
  //DRIVER("driver"),
  //PASSENGER("passenger");
//  DRIVER,
//  PASSENGER;
  
  private String mString;

  UserRole(String string) {
    mString = string;
  }

  public String toString() {
    return mString;
  }
}
