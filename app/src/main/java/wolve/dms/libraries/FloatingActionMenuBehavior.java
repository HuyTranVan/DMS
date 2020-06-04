package wolve.dms.libraries;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior {
    private float mTranslationY;

    public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
        super();
    }

    public FloatingActionMenuBehavior(Context context) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

//    @Override
//    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        if (child instanceof FloatingActionMenu && dependency instanceof Snackbar.SnackbarLayout) {
//            this.updateTranslation(parent, child, dependency);
//        }
//
//        return false;
//    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if ((child instanceof FloatingActionMenu || child instanceof FloatingActionButton)
                && dependency instanceof Snackbar.SnackbarLayout) {
            this.updateTranslation(parent, child, dependency);
        }

        return false;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        if ((child instanceof FloatingActionMenu || child instanceof FloatingActionButton)
                && dependency instanceof Snackbar.SnackbarLayout) {
            this.updateTranslation(parent, child, dependency);
        }
    }

    private void updateTranslation(CoordinatorLayout parent, View child, View dependency) {
        float translationY = this.getTranslationY(parent, child);
        if (translationY != this.mTranslationY) {
            ViewCompat.animate(child)
                    .cancel();
            if (Math.abs(translationY - this.mTranslationY) == (float) dependency.getHeight()) {
                ViewCompat.animate(child)
                        .translationY(translationY)
                        .setListener((ViewPropertyAnimatorListener) null);
            } else {
                ViewCompat.setTranslationY(child, translationY);
            }

            this.mTranslationY = translationY;
        }

    }

    private float getTranslationY(CoordinatorLayout parent, View child) {
        float minOffset = 0.0F;
        List dependencies = parent.getDependencies(child);
        int i = 0;

        for (int z = dependencies.size(); i < z; ++i) {
            View view = (View) dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(child, view)) {
                minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - (float) view.getHeight());
            }
        }

        return minOffset;
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed) {
        if (child instanceof FloatingActionMenu) {
            FloatingActionMenu fabMenu = (FloatingActionMenu) child;
            if (velocityY > 0) {
                fabMenu.hideMenuButton(true);
            } else if (velocityY < 0) {
                fabMenu.showMenuButton(true);
            }
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

}
