package co.gargoyle.supercab.android.enums;

import android.os.Parcel;
import android.os.Parcelable;

public enum FareType implements Parcelable {

  WAITING,
  PICKUP,
  UNKNOWN,
  DROPOFF;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeInt(ordinal());
  }

  public static final Parcelable.Creator<FareType> CREATOR = new Parcelable.Creator<FareType>() {
    public FareType createFromParcel(Parcel in) {
      return FareType.values()[in.readInt()];
    }

    public FareType[] newArray(int size) {
      return new FareType[size];
    }
  };

}
