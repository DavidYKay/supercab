package co.gargoyle.supercab.android.map;

import android.location.Address;
import co.gargoyle.supercab.android.enums.FareType;

import com.google.common.base.Objects;

public class PickupPoint {

  private FareType mFareType;
  private Address mAddress;

  public PickupPoint(FareType fareType, Address address) {
    mFareType = fareType;
    mAddress = address;
  }

  public Address getAddress() {
    return mAddress;
  }

  public FareType getFareType() {
    return mFareType;
  }


  @Override
  public boolean equals(Object o) {
    return Objects.equal(this, o);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(mFareType, mAddress);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
                .addValue(mFareType)
                .addValue(mAddress)
                .toString();
  }

}
