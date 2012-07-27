package co.gargoyle.supercab.android.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class ObjectUtils {

  // TODO: Test this!!!
  public static Parcelable cloneParcelable(Parcelable original) {
    Parcel p = Parcel.obtain();
    p.writeValue(original);
    p.setDataPosition(0);
    Parcelable newParcelable = (Parcelable)p.readValue(Parcelable.class.getClassLoader());
    p.recycle();
    return newParcelable;
  }
  
  //public static T[] listToArray(List<T> list) {
  //  T[] array = (T[]) list.toArray(new T[list.size()]);
  //  return array;
  //}
}
