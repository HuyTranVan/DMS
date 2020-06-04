package wolve.dms.customviews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wolve.dms.R;


public class CVerticalView extends RelativeLayout {
    private TextView tvTitle, tvValue, tvIcon;
    private Context mContext;
    private View mLayout;

    public CVerticalView(Context context) {
        this(context, null);
    }

    public CVerticalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CVerticalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    public void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.viewcustom_vertical, this, true);

        mLayout = findViewById(R.id.button_parent);
        tvTitle = (TextView) mLayout.findViewById(R.id.button_title);
        tvValue = (TextView) mLayout.findViewById(R.id.button_value);
        tvIcon = (TextView) mLayout.findViewById(R.id.button_icon);

    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CInputForm, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.CInputForm_iconColor)) {
                setIconColor(a.getColor(R.styleable.CInputForm_iconColor, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_iconSize)) {
                setIconSize(convertPixelsToDp(a.getDimensionPixelSize(R.styleable.CInputForm_iconSize, 0), mContext));
            }

            if (a.hasValue(R.styleable.CInputForm_iconText)) {
                setIconText(a.getString(R.styleable.CInputForm_iconText));
            }

            if (a.hasValue(R.styleable.CInputForm_titleColor)) {
                setTitleColor(a.getColor(R.styleable.CInputForm_titleColor, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_titleSize)) {
                setTitleSize(convertPixelsToDp(a.getDimensionPixelSize(R.styleable.CInputForm_titleSize, 0), mContext));
            }

            if (a.hasValue(R.styleable.CInputForm_titleText)) {
                setTitleText(a.getString(R.styleable.CInputForm_titleText));
            }

            if (a.hasValue(R.styleable.CInputForm_textColor)) {
                setTextColor(a.getColor(R.styleable.CInputForm_textColor, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_textSize)) {
                setTextSize(convertPixelsToDp(a.getDimensionPixelSize(R.styleable.CInputForm_textSize, 0), mContext));
            }

            if (a.hasValue(R.styleable.CInputForm_text)) {
                setText(a.getString(R.styleable.CInputForm_text));
            }

//            if (a.hasValue(R.styleable.CInputForm_backgroundIcon)) {
//                setIconBackground(a.getDrawable(R.styleable.CInputForm_backgroundIcon));
//            }
            if (a.hasValue(R.styleable.CInputForm_backgroundLayout)) {
                setBackground(a.getDrawable(R.styleable.CInputForm_backgroundLayout));
            }

            a.recycle();
        }
    }

//    public void setIconBackground(Drawable background) {
    //edInput.setBackgroundColor(color);
//        tvIcon.setBackgroundDrawable(background);

//    }

    public void setBackground(Drawable background) {
        mLayout.setBackground(background);

    }

    public String getText() {
        return tvValue.getText().toString();
    }

    public void setTextColor(int color) {
        tvValue.setTextColor(color);
    }

    public void setTextSize(float dimension) {
        tvValue.setTextSize(dimension);
    }

    public void setText(String string) {
        tvValue.setText(string);

    }

    public void setIconColor(int color) {
        tvIcon.setTextColor(color);
    }

    public void setIconSize(float dimension) {
        tvIcon.setTextSize(dimension);
    }

    public void setIconText(String string) {
        tvIcon.setText(string);
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setTitleSize(float dimension) {
        tvTitle.setTextSize(dimension);
    }

    public void setTitleText(String string) {
        tvTitle.setText(string);

    }

}
