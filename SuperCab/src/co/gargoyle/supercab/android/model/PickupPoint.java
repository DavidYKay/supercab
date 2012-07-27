package co.gargoyle.supercab.android.model;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.json.CustomAddressSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pickups")
public class PickupPoint extends SuperCabBaseModel implements Parcelable {

  @DatabaseField
  public FareType fareType;
  	
  @JsonSerialize(using = CustomAddressSerializer.class)
//  @Databas
//  @DatabaseField(persisterClass = CustomAddressPersister.class)
  public Address address;
  
  public PickupPoint() {
    // required for ORMLite
  }

  public PickupPoint(FareType fareType, Address address) {
    this.fareType = fareType;
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    return Objects.equal(this, o);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(fareType, address);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
                .addValue(fareType)
                .addValue(address.getAddressLine(0))
                .toString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeParcelable(fareType, flags);
    out.writeParcelable(address, flags);
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
