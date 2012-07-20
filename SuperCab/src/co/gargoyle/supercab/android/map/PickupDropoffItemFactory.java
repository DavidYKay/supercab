package co.gargoyle.supercab.android.map;

import android.graphics.drawable.Drawable;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.PickupPoint;

public class PickupDropoffItemFactory {
  private Drawable mPickupMarker;
  private Drawable mDropoffMarker;

  public PickupDropoffItemFactory(Drawable pickupMarker, Drawable dropOffMarker) {
    mPickupMarker  = pickupMarker;
    mDropoffMarker = dropOffMarker;
  }

  public PickupDropoffItem itemFromPickup(PickupPoint pickup) {
    Drawable marker = (pickup.getFareType() == FareType.PICKUP) ? mPickupMarker : mDropoffMarker ;
    return new PickupDropoffItem(pickup, marker);
//    return itemFromStuff(
//        pickup.getAddress(),
//        pickup.getFareType(),
//        marker
//        );
  }

//  public static PickupDropoffItem itemFromStuff(Address address, FareType type, Drawable marker) {
//    GeoPoint geoPoint = GeoUtils.addressToGeoPoint(address);
//    PickupDropoffItem item = new PickupDropoffItem(geoPoint, type.toString(), address.getAddressLine(0), type, marker);
//    return item;
//  }
}
