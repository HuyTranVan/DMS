package wolve.dms.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import wolve.dms.R;

public class CRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {

    private TextView tvText, tvIcon;
    private Context mContext;
    private LinearLayout mLayout;

    public CRadioButton(Context context) {
        this(context, null);
    }

    public CRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    public void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_radiobutton, null);
        mLayout = findViewById(R.id.view_radiobutton_parent);
        tvText = (TextView) mLayout.findViewById(R.id.view_radiobutton_text);
        tvIcon = (TextView) mLayout.findViewById(R.id.view_radiobutton_icon);

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

    public void setBackground(Drawable background) {
        mLayout.setBackground(background);

    }

    public String getText() {
        return tvText.getText().toString();
    }

    public void setTextColor(int color) {
        tvText.setTextColor(color);
    }

    public void setTextSize(float dimension) {
        tvText.setTextSize(dimension);
    }

    public void setText(String string) {
        tvText.setText(string);

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



}
