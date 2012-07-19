package co.gargoyle.supercab.android.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import co.gargoyle.supercab.android.R;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class XOverlay extends Overlay {

  private Bitmap mXBitmap;

  public XOverlay(Bitmap xBitmap) {
    mXBitmap = xBitmap;
  }

  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    super.draw(canvas, mapView, shadow);

    int canvasHeight = canvas.getHeight();
    int canvasWidth = canvas.getWidth();

    Bitmap xBitmap = mXBitmap;

    int bitmapHeight = xBitmap.getHeight();
    int bitmapWidth  = xBitmap.getWidth();

    float left = (canvasWidth  - bitmapWidth) / 2;

    float top  = (canvasHeight / 2) - (2 * bitmapHeight);

    Paint paint = new Paint();
    canvas.drawBitmap(xBitmap, left, top, paint);

  }


}
