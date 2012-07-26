package co.gargoyle.supercab.android.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import co.gargoyle.supercab.android.model.json.CustomDateSerializer;
import co.gargoyle.supercab.android.model.json.CustomPickupPointSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Fare extends SuperCabBaseModel implements Parcelable {

  private PickupPoint mSource;
  private PickupPoint mDestination;
  private Date mTimeRequested;
  
  public Fare(PickupPoint source, PickupPoint destination, Date timeRequested) {
    super();
    mSource = (source);
    mDestination = (destination);
    mTimeRequested = (timeRequested);
  }

  @JsonProperty("requestTime")
  @JsonSerialize(using = CustomDateSerializer.class)
  public Date getTimeRequested() {
    return mTimeRequested;
  }

  @JsonProperty("to")
  @JsonSerialize(using = CustomPickupPointSerializer.class)
  public PickupPoint getDestination() {
    return mDestination;
  }

  @JsonProperty("from")
  @JsonSerialize(using = CustomPickupPointSerializer.class)
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
