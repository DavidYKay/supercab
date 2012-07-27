package co.gargoyle.supercab.android.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import co.gargoyle.supercab.android.enums.FareStatus;
import co.gargoyle.supercab.android.model.json.CustomPickupPointDeserializer;
import co.gargoyle.supercab.android.model.json.CustomPickupPointSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fares")
public class Fare extends SuperCabBaseModel implements Parcelable {

  @JsonProperty("from")
  @JsonDeserialize(using = CustomPickupPointDeserializer.class)
  @JsonSerialize(using = CustomPickupPointSerializer.class)
  @DatabaseField(foreign = true)
  public PickupPoint source;

  @JsonProperty("to")
  @JsonDeserialize(using = CustomPickupPointDeserializer.class)
  @JsonSerialize(using = CustomPickupPointSerializer.class)
  @DatabaseField(foreign = true)
  public PickupPoint destination;

  @JsonProperty("requestTime")
//  @JsonSerialize(using = CustomDateSerializer.class)
  @DatabaseField
  public Date timeRequested;

  @DatabaseField
  public FareStatus status;

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
    out.writeString(superCabId);
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
    superCabId    = in.readString();
    source        = in.readParcelable(Fare.class.getClassLoader());
    destination   = in.readParcelable(Fare.class.getClassLoader());
    timeRequested = (Date) in.readSerializable();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof Fare)) {
      return false;
    }
    Fare other = (Fare) o;
    if (
        Objects.equal(this.status, other.status) &&
        Objects.equal(this.timeRequested, other.timeRequested) &&
        Objects.equal(this.superCabId, other.superCabId) &&
        Objects.equal(this.source, other.source) &&
        Objects.equal(this.destination, other.destination)
    ){
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        source        ,
        destination   ,
        status       ,
        timeRequested ,
        superCabId
        );
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .addValue(superCabId)
        .addValue(source)
        .addValue(destination)
        .addValue(status)
        .addValue(timeRequested)
        .toString();
  }


}
