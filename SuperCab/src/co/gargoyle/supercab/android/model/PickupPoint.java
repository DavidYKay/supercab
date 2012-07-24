package co.gargoyle.supercab.android.model;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.json.CustomAddressSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;

public class PickupPoint implements Parcelable {

  private FareType mFareType;
  private Address mAddress;

  public PickupPoint(FareType fareType, Address address) {
    mFareType = fareType;
    mAddress = address;
  }

  @JsonSerialize(using = CustomAddressSerializer.class)
  public Address getAddress() {
    return mAddress;
  }

  @JsonIgnore()
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
                .addValue(mAddress.getAddressLine(0))
                .toString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeParcelable(mFareType, flags);
    out.writeParcelable(mAddress, flags);
  }

  public static final Parcelable.Creator<PickupPoint> CREATOR = new Parcelable.Creator<PickupPoint>() {
    public PickupPoint createFromParcel(Parcel in) {
      FareType fareType = in.readParcelable(PickupPoint.class.getClassLoader());
      Address address = in.readParcelable(PickupPoint.class.getClassLoader());
      return new PickupPoint(fareType, address);
    }

    public PickupPoint[] newArray(int size) {
      return new PickupPoint[size];
    }
  };


}
