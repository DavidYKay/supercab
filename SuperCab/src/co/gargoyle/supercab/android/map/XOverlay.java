package co.gargoyle.supercab.android.map;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import co.gargoyle.supercab.android.enums.FareType;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.common.base.Optional;

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

    Optional<Bitmap> bitmap = getCurrentBitmap();
    if (!bitmap.isPresent()) {
      return;
    }

    Bitmap xBitmap = bitmap.get();

    int canvasHeight = canvas.getHeight();
    int canvasWidth = canvas.getWidth();

    int bitmapHeight = xBitmap.getHeight();
    int bitmapWidth  = xBitmap.getWidth();

    float left = (canvasWidth  - bitmapWidth) / 2;

    //float top  = (canvasHeight / 2) - (4 * bitmapHeight);
    //float top  = (canvasHeight / 2) - (2 * bitmapHeight);
    float top  = (canvasHeight / 2) - (bitmapHeight / 2);

    Paint paint = new Paint();
    canvas.drawBitmap(xBitmap, left, top, paint);
  }

  private Optional<Bitmap> getCurrentBitmap() {
    Bitmap bitmap = mBitmaps.get(mCurrentMode);
    if (bitmap == null) {
      return Optional.absent();
    } else {
      return Optional.of(bitmap);
    }
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
