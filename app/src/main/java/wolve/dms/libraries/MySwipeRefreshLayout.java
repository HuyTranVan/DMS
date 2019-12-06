package wolve.dms.libraries;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.reflect.Field;

/**
 * Created by macos on 10/19/17.
 */

public class MySwipeRefreshLayout extends SwipeRefreshLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    private static float MAX_SWIPE_DISTANCE_FACTOR = 0.9f;
    private static int DEFAULT_REFRESH_TRIGGER_DISTANCE = 100;

    private int refreshTriggerDistance = DEFAULT_REFRESH_TRIGGER_DISTANCE;

    ViewTreeObserver vto;

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        // Calculate the trigger distance.
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        Float mDistanceToTriggerSync = Math.min(
                ((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                refreshTriggerDistance * metrics.density);

        try {
            // Set the internal trigger distance using reflection.
            Field field = SwipeRefreshLayout.class.getDeclaredField("mDistanceToTriggerSync");
            field.setAccessible(true);
            field.setFloat(this, mDistanceToTriggerSync);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // Only needs to be done once so remove listener.
        ViewTreeObserver obs = getViewTreeObserver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            obs.removeOnGlobalLayoutListener(this);
        } else {
            //noinspection deprecation
            obs.removeGlobalOnLayoutListener(this);
        }
    }

    private int getRefreshTriggerDistance() {
        return refreshTriggerDistance;
    }

    private void setRefreshTriggerDistance(int refreshTriggerDistance) {
        this.refreshTriggerDistance = refreshTriggerDistance;
    }
}

//public class MySwipeRefreshLayout extends SwipeRefreshLayout {
//
//    private int mTouchSlop;
//    private float mPrevX;
//    // Indicate if we've already declined the move event
//    private boolean mDeclined;
//
//    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mPrevX = MotionEvent.obtain(event).getX();
//                mDeclined = false; // New action
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                final float eventX = event.getX();
//                float xDiff = Math.abs(eventX - mPrevX);
//
//                if (mDeclined || xDiff > mTouchSlop) {
//                    mDeclined = true; // Memorize
//                    return false;
//                }
//        }
//
//        return super.onInterceptTouchEvent(event);
//    }
//
//}

