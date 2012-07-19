package co.gargoyle.supercab.android.map;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import co.gargoyle.supercab.android.enums.FareType;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class XOverlay extends Overlay {

  private FareType mCurrentMode;
  //private Bitmap mCurrentBitmap;

  private HashMap<FareType, Bitmap> mBitmaps;

  public XOverlay(HashMap<FareType, Bitmap> bitmaps, FareType defaultKey) {
    mBitmaps = bitmaps;
    //mCurrentBitmap = mBitmaps.get(defaultKey);
    mCurrentMode = defaultKey;
  }

  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    super.draw(canvas, mapView, shadow);

    int canvasHeight = canvas.getHeight();
    int canvasWidth = canvas.getWidth();

    Bitmap xBitmap = getCurrentBitmap();

    int bitmapHeight = xBitmap.getHeight();
    int bitmapWidth  = xBitmap.getWidth();
    //int bitmapHeight = xBitmap.getIntrinsicHeight();
    //int bitmapWidth  = xBitmap.getIntrinsicWidth();

    float left = (canvasWidth  - bitmapWidth) / 2;

    //float top  = (canvasHeight / 2) - (2 * bitmapHeight);
    float top  = (canvasHeight / 2) - (4 * bitmapHeight);

    Paint paint = new Paint();
    canvas.drawBitmap(xBitmap, left, top, paint);
  }

  private Bitmap getCurrentBitmap() {
    return mBitmaps.get(mCurrentMode);
  }

  public void setMode(FareType mode) {
    if (mode == mCurrentMode) {
      return;
    } else {
      mCurrentMode = mode;
      //Bitmap newBitmap = mBitmaps.get(mode);
      //mCurrentMode
    }
  }

}
