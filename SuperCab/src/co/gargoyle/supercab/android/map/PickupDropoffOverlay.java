package co.gargoyle.supercab.android.map;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.util.Log;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.utilities.GeoUtils;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.common.base.Optional;

public class PickupDropoffOverlay extends ItemizedOverlay<PickupDropoffItem> {

  private static final String TAG = "PickupDropoffOverlay";

  private ArrayList<PickupPoint> mPickups = new ArrayList<PickupPoint>();

  private PickupDropoffItemFactory mItemFactory;

  private PickupDropoffOverlayTapListener mTapListener;

  //public static class Factory {
  //  public static PickupDropoffOverlay createOverlay() {
  //    PickupDropoffOverlay overlay = new PickupDropoffOverlay(
  //        pickupMarkerDrawable
  //    );
  //    return overlay;
  //  }
  //}

  public PickupDropoffOverlay(Drawable pickupMarker, Drawable dropOffMarker) {
    super(pickupMarker);

    mItemFactory = new PickupDropoffItemFactory(
      boundCenterBottom(pickupMarker),
      boundCenterBottom(dropOffMarker));

    populate();
  }

  @Override
  protected PickupDropoffItem createItem(int i) {
    synchronized(mPickups) {
      PickupPoint pickup = mPickups.get(i);

      PickupDropoffItem item = mItemFactory.itemFromPickup(pickup);

      return item;
    }
  }

  @Override
  public int size() {
    synchronized(mPickups) {
      return mPickups.size();
    }
  }

  protected boolean onTap(int i) {
    if (getTapListener() != null) {
      getTapListener().itemWasTapped(createItem(i));
    }

    return(true);
  }

  ////////////////////////////////////////////////////////////
  // Add / Remove
  ////////////////////////////////////////////////////////////

  public PickupPoint get(int i) {
    synchronized(mPickups) {
      return mPickups.get(i);
    }
  }

  public void addPickup(PickupPoint pickup) {
    synchronized(mPickups) {
      mPickups.add(pickup);
      populate();

      Log.d(TAG, "Total items: " + size());
    }
  }

  public void removePickup(PickupPoint pickup) {
    synchronized(mPickups) {
      mPickups.remove(pickup);
      populate();
    }
  }

  public void clear() {
    synchronized(mPickups) {
      mPickups.clear();
      populate();
    }
  }

  public PickupDropoffOverlayTapListener getTapListener() {
    return mTapListener;
  }

  public void setTapListener(PickupDropoffOverlayTapListener tapListener) {
    this.mTapListener = tapListener;
  }

  ////////////////////////////////////////////////////////////
  // Add / Remove
  ////////////////////////////////////////////////////////////

  public Optional<GeoPoint> getCenterPoint() {

    if (mPickups.size() == 0) {
      return Optional.absent();
    }

    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
    for (PickupPoint pickup : mPickups) {
      GeoPoint point = GeoUtils.addressToGeoPoint(pickup.getAddress());
      points.add(point);
    }

    return GeoUtils.getCenterPoint(points);
  }
}
