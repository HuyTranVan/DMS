package wolve.dms.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.ViewUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import wolve.dms.R;
import wolve.dms.utils.Util;

public class MaxHeightLinearLayout extends LinearLayout {

    private int maxHeightDp;

    public MaxHeightLinearLayout(Context context) {
        super(context);
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaxHeightLinearLayout, 0, 0);
        try {
            maxHeightDp = a.getInteger(R.styleable.MaxHeightLinearLayout_maxHeightDp, 0);
        } finally {
            a.recycle();
        }
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeightPx = (int) Util.convertDp2Px(maxHeightDp);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeightDp(int maxHeightDp) {
        this.maxHeightDp = maxHeightDp;
        invalidate();
    }

}
