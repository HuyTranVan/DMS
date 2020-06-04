package wolve.dms.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import wolve.dms.R;

/**
 * Created by macos on 9/17/17.
 */

public class CButtonVertical extends RelativeLayout {
    private TextView tvText;
    private TextView tvIcon;
    private Context mContext;
    private View mLayout;
    private ArrayList<String> listDropdown = new ArrayList<>();

    public CButtonVertical(Context context) {
        this(context, null);
    }

    public CButtonVertical(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CButtonVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    public void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.viewcustom_button_vertical, this, true);

        mLayout = findViewById(R.id.button_vertical_parent);
        tvText = (TextView) mLayout.findViewById(R.id.button_vertical_content);
        tvIcon = (TextView) mLayout.findViewById(R.id.button_vertical_icon);

    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CInputForm, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.CInputForm_iconColor)) {
                setIconColor(a.getColor(R.styleable.CInputForm_iconColor, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_iconSize)) {
                setIconSize(a.getDimension(R.styleable.CInputForm_iconSize, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_iconText)) {
                setIconText(a.getString(R.styleable.CInputForm_iconText));
            }

            if (a.hasValue(R.styleable.CInputForm_textColor)) {
                setTextColor(a.getColor(R.styleable.CInputForm_textColor, 0));
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

            a.recycle();
        }
    }

    public void setIconBackground(Drawable background) {
        //edInput.setBackgroundColor(color);
        tvIcon.setBackgroundDrawable(background);

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
