package co.gargoyle.supercab.android.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Fare implements Parcelable {

  private PickupPoint mSource;
  private PickupPoint mDestination;
  private Date mTimeRequested;
  
  public Fare(PickupPoint source, PickupPoint destination, Date timeRequested) {
    super();
    mSource = (source);
    mDestination = (destination);
    mTimeRequested = (timeRequested);
  }

  public Date getTimeRequested() {
    return mTimeRequested;
  }

  public PickupPoint getDestination() {
    return mDestination;
  }

  public PickupPoint getSource() {
    return mSource;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeParcelable(mSource, flags);
    out.writeParcelable(mDestination, flags);
    out.writeSerializable(mTimeRequested);
  }

  public static final Parcelable.Creator<Fare> CREATOR = new Parcelable.Creator<Fare>() {
    public Fare createFromParcel(Parcel in) {
      return new Fare(in);
    }

    public Fare[] newArray(int size) {
      return new Fare[size];
    }
  };

  private Fare(Parcel in) {
    mSource        = in.readParcelable(Fare.class.getClassLoader()); 
    mDestination   = in.readParcelable(Fare.class.getClassLoader()); 
    mTimeRequested = (Date) in.readSerializable(); 
  }

}
