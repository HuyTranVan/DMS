package wolve.dms.customviews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import wolve.dms.R;


public class CButton extends LinearLayout {
    private TextView tvText;
    private CTextIcon tvIcon;
    private Context mContext;
    private View mLayout;
    private ArrayList<String> listDropdown = new ArrayList<>();

    public CButton(Context context) {
        this(context, null);
    }

    public CButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    public void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.viewcustom_button, this, true);

        mLayout = findViewById(R.id.button_parent);
        tvText = (TextView) mLayout.findViewById(R.id.button_text);
        tvIcon = (CTextIcon) mLayout.findViewById(R.id.button_icon);

    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CInputForm, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.CInputForm_iconColor)) {
                setIconColor(a.getColor(R.styleable.CInputForm_iconColor,0));
            }

            if (a.hasValue(R.styleable.CInputForm_icon_Size)) {
                setIconSize(a.getDimension(R.styleable.CInputForm_icon_Size, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_iconText)) {
                setIconText(a.getString(R.styleable.CInputForm_iconText));
            }

            if (a.hasValue(R.styleable.CInputForm_textColor)) {
                setTextColor(a.getColor(R.styleable.CInputForm_textColor,0));
            }

            if (a.hasValue(R.styleable.CInputForm_textSize)) {
                setTextSize(a.getDimension(R.styleable.CInputForm_textSize, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_text)) {
                setText(a.getString(R.styleable.CInputForm_text));
            }

            if (a.hasValue(R.styleable.CInputForm_backgroundIcon)) {
                setIconBackground(a.getDrawable(R.styleable.CInputForm_backgroundIcon));
            }
            if (a.hasValue(R.styleable.CInputForm_backgroundLayout)) {
                setBackground(a.getDrawable(R.styleable.CInputForm_backgroundLayout));
            }

            a.recycle();
        }
    }

    public void setIconBackground(Drawable background) {
        //edInput.setBackgroundColor(color);
        tvIcon.setBackgroundDrawable(background);

    }

    public void setBackground(Drawable background) {
        //edInput.setBackgroundColor(color);
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
