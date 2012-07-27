package co.gargoyle.supercab.android.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import co.gargoyle.supercab.android.model.json.CustomDateSerializer;
import co.gargoyle.supercab.android.model.json.CustomPickupPointSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fares")
public class Fare extends SuperCabBaseModel implements Parcelable {

  @JsonProperty("from")
  @JsonSerialize(using = CustomPickupPointSerializer.class)
  @DatabaseField(foreign = true)
  public PickupPoint source;
  
  @JsonProperty("to")
  @JsonSerialize(using = CustomPickupPointSerializer.class)
  @DatabaseField(foreign = true)
  public PickupPoint destination;
  
  @JsonProperty("requestTime")
  @JsonSerialize(using = CustomDateSerializer.class)
  @DatabaseField()
  public Date timeRequested;
  
  public Fare() {
    super();
    // Required for ORMLite
  }
  
  public Fare(PickupPoint source, PickupPoint destination, Date timeRequested) {
    super();
    this.source = (source);
    this.destination = (destination);
    this.timeRequested = (timeRequested);
  }
  
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeParcelable(source, flags);
    out.writeParcelable(destination, flags);
    out.writeSerializable(timeRequested);
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
    source        = in.readParcelable(Fare.class.getClassLoader()); 
    destination   = in.readParcelable(Fare.class.getClassLoader()); 
    timeRequested = (Date) in.readSerializable(); 
  }

}
