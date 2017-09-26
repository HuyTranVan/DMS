package wolve.dms.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import wolve.dms.R;


public class CTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "TextView";

    public CTextView(Context context) {
        super(context);
    }

    public CTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        //setCustomTextColor(context, attrs);
    }

    public CTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        //setCustomTextColor(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CTextViewDefault);
        String customFont = a.getString(R.styleable.CTextViewDefault_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: "+e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }



//    private void setCustomTextColor(Context ctx, AttributeSet attrs) {
//        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CTextView);
//        String customText = a.getString(R.styleable.CTextView_textColorCustom);
//
//        setTextColor(a.getColor(R.styleable.CTextView_textColorCustom, 0));
//
//        a.recycle();
//    }

}
