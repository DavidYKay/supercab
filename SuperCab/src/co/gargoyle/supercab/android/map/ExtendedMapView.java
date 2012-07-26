package co.gargoyle.supercab.android.map;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class ExtendedMapView extends MapView {
    private static final long STOP_TIMER_DELAY = 1500; // 1.5 seconds
    private ScheduledThreadPoolExecutor mExecutor;
    private OnMoveListener mOnMoveListener;
    @SuppressWarnings("rawtypes")
    private Future mStoppedMovingFuture;

    /**
     * Creates a new extended map view.
     * Make sure to override the other constructors if you plan to use them.
     */
    public ExtendedMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mExecutor = new ScheduledThreadPoolExecutor(1);
    }

    public interface OnMoveListener {
        /**
         * Notifies that the map has moved. 
         * If the map is moving, this will be called frequently, so don't spend 
         * too much time in this function. If the stopped variable is true, 
         * then the map has stopped moving. This may be useful if you want to
         * refresh the map when the map moves, but not with every little movement.
         * 
         * @param mapView the map that moved
         * @param center the new center of the map
         * @param stopped true if the map is no longer moving
         */
        public void onMove(MapView mapView, GeoPoint center, boolean stopped);
    }

    public void setOnMoveListener(OnMoveListener l) {
      mOnMoveListener = l;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mOnMoveListener != null) {
            // Inform the listener that the map has moved.
            mOnMoveListener.onMove(this, getMapCenter(), false);

            // We also want to notify the listener when the map stops moving.
            // Every time the map moves, reset the timer. If the timer ever completes, 
            // then we know that the map has stopped moving.
            if (mStoppedMovingFuture != null) {
                mStoppedMovingFuture.cancel(false);
            }
            mStoppedMovingFuture = mExecutor.schedule(onMoveStop, STOP_TIMER_DELAY,
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * This is run when we have stopped moving the map. 
     */
    private Runnable onMoveStop = new Runnable() {
        public void run() {
            if (mOnMoveListener != null) {
                mOnMoveListener.onMove(ExtendedMapView.this, getMapCenter(), true);
            }
        }
    };
}
