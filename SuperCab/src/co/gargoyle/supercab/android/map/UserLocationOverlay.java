package co.gargoyle.supercab.android.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class UserLocationOverlay extends MyLocationOverlay {

   
  private LocationListener mListener;

  public UserLocationOverlay(Context context, MapView mapView, LocationListener listener) {
    super(context, mapView);
    mListener = listener;
  }

  @Override
  public synchronized void onLocationChanged(Location location) {
    //super.onLocationChanged(location);
    mListener.onLocationChanged(location);
  }
  
  

}
