package wolve.dms.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class CustomTabLayout extends TabLayout {

    private static final String TAG = CustomTabLayout.class.getSimpleName();

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getTabCount() == 0)
            return;
        try {
            ViewGroup tabLayout = (ViewGroup) getChildAt(0);
            int widthOfAllTabs = 0;
            for (int i = 0; i < tabLayout.getChildCount(); i++) {
                widthOfAllTabs += tabLayout.getChildAt(i).getMeasuredWidth();
            }
            setTabMode(widthOfAllTabs <= getMeasuredWidth() ? MODE_FIXED : MODE_SCROLLABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
