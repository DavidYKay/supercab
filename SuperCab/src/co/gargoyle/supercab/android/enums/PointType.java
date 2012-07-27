package co.gargoyle.supercab.android.enums;

import android.os.Parcel;
import android.os.Parcelable;

public enum PointType implements Parcelable {

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

  public static final Parcelable.Creator<PointType> CREATOR = new Parcelable.Creator<PointType>() {
    public PointType createFromParcel(Parcel in) {
      return PointType.values()[in.readInt()];
    }

    public PointType[] newArray(int size) {
      return new PointType[size];
    }
  };

}
