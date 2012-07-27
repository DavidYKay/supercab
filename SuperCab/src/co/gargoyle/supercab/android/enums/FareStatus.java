package co.gargoyle.supercab.android.enums;

import android.os.Parcel;
import android.os.Parcelable;

public enum FareStatus implements Parcelable {

  waiting,
  accepted,
  active,
  complete,
  cancelled;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeInt(ordinal());
  }

  public static final Parcelable.Creator<FareStatus> CREATOR = new Parcelable.Creator<FareStatus>() {
    public FareStatus createFromParcel(Parcel in) {
      return FareStatus.values()[in.readInt()];
    }

    public FareStatus[] newArray(int size) {
      return new FareStatus[size];
    }
  };

}
