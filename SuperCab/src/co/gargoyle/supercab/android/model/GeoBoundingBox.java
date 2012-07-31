package co.gargoyle.supercab.android.model;

import com.google.android.maps.GeoPoint;

public class GeoBoundingBox {

  //public GeoPoint topLeft;
  //public GeoPoint topRight;
  //public GeoPoint bottomLeft;
  //public GeoPoint bottomRight;
    
  public int minLat = 0;
  public int maxLat = 0;
  public int minLon = 0;
  public int maxLon = 0;

  public int getLatitudeSpan() {
    return maxLat - minLat;
  }
  
  public int getLongitudeSpan() {
    return maxLon - minLon;
  }
  
  public GeoPoint getMidPoint() {
    int latitudeMidpoint = (minLat + maxLat) / 2;
    int longitudeMidpoint = (minLon + maxLon) / 2;

    return new GeoPoint(latitudeMidpoint, longitudeMidpoint);
  }
  
}
