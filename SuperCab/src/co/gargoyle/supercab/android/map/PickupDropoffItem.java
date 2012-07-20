package co.gargoyle.supercab.android.map;

import android.graphics.drawable.Drawable;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.android.maps.OverlayItem;
import com.google.common.base.Objects;

public class PickupDropoffItem extends OverlayItem {

  private PickupPoint mPickup;

  public PickupDropoffItem(PickupPoint pickup, Drawable marker) {
    super(
        GeoUtils.addressToGeoPoint(pickup.getAddress()),
        pickup.getFareType().toString(), 
        pickup.getAddress().getAddressLine(0));

    mPickup = pickup;
    mMarker = marker;
  }

  public Drawable getMarker(int stateBitset)
  {
    return mMarker;
  }

  public FareType getFareType() {
    return mPickup.getFareType();
  }
  
  public PickupPoint getPickup() {
    return mPickup;
  }

  @Override
  public boolean equals(Object o) {
    return Objects.equal(this, o);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        mPickup, 
        mMarker);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
                .addValue(mPickup)
                .addValue(mMarker)
                .toString();
  }
  
  

}
