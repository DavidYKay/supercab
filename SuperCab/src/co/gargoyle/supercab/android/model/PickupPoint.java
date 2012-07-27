package co.gargoyle.supercab.android.model;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import co.gargoyle.supercab.android.enums.PointType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pickups")
public class PickupPoint extends SuperCabBaseModel implements Parcelable {

  @JsonIgnore
  @DatabaseField
  public PointType pointType;
  
  @JsonProperty("lat")
  @DatabaseField
  public double latitude;
  
  @JsonProperty("lon")
  @DatabaseField
  public double longitude;
  
  @DatabaseField
  public String address;
  	
  //@JsonSerialize(using = CustomAddressSerializer.class)
  //@JsonDeserialize(using = CustomAddressDeserializer.class)
//  @DatabaseField(persisterClass = CustomAddressPersister.class)
//  public Address address;
  
  public PickupPoint() {
    // required for ORMLite
  }

  public PickupPoint(PointType pointType, Address address) {
    this.pointType = pointType;

    this.address = address.getAddressLine(0);
    this.latitude  = address.getLatitude();
    this.longitude = address.getLongitude();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof PickupPoint)) {
      return false;
    }
    PickupPoint other = (PickupPoint) o;
    if (
        Objects.equal(this.superCabId, other.superCabId) &&
        Objects.equal(this.pointType, other.pointType) &&
        Objects.equal(this.latitude, other.latitude) &&
        Objects.equal(this.longitude, other.longitude) &&
        Objects.equal(this.address, other.address)
    ) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(pointType, address);
  }

  @Override
  public String toString() {
    return address;
    //return Objects.toStringHelper(this)
    //            .addValue(pointType)
    //            .addValue(address.getAddressLine(0))
    //            .toString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeParcelable(pointType, flags);
    out.writeString(address);
    out.writeDouble(latitude);
    out.writeDouble(longitude);
  }

  public static final Parcelable.Creator<PickupPoint> CREATOR = new Parcelable.Creator<PickupPoint>() {
    public PickupPoint createFromParcel(Parcel in) {
      PointType pointType = in.readParcelable(PickupPoint.class.getClassLoader());
      String address      = in.readString();
      double latitude     = in.readDouble();
      double longitude    = in.readDouble();

      PickupPoint point = new PickupPoint();
      point.pointType = pointType;
      point.address = address;
      point.latitude = latitude;
      point.longitude = longitude;

      return point;
    }

    public PickupPoint[] newArray(int size) {
      return new PickupPoint[size];
    }
  };

}
