package wolve.dms.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import wolve.dms.R;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackString;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/17/17.
 */

public class CInputForm extends FrameLayout {
    private EditText edInput;
    private CTextIcon tvIcon, tvMore;
    private Context mContext;
    private View mLayout, mLine;
    private ArrayList<String> listDropdown = new ArrayList<>();

    public CInputForm(Context context) {
        this(context, null);
    }

    public CInputForm(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CInputForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    public void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.viewcustom_input_data, this, true);
        mLayout = findViewById(R.id.input_data_parent);

        edInput = (EditText) mLayout.findViewById(R.id.input_data_content);
        tvIcon = (CTextIcon) mLayout.findViewById(R.id.input_data_icon);
        tvMore = (CTextIcon) mLayout.findViewById(R.id.input_data_dropdown);
        mLine = (View) mLayout.findViewById(R.id.input_data_underline);

    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CInputForm, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.CInputForm_multiLine)) {
                setMultiLine(a.getBoolean(R.styleable.CInputForm_multiLine,false));
            }

            if (a.hasValue(R.styleable.CInputForm_iconColor)) {
                setIconColor(a.getColor(R.styleable.CInputForm_iconColor,0));
            }

            if (a.hasValue(R.styleable.CInputForm_iconSize)) {
                setIconSize(a.getDimension(R.styleable.CInputForm_iconSize, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_iconText)) {
                setIconText(a.getString(R.styleable.CInputForm_iconText));
            }

            if (a.hasValue(R.styleable.CInputForm_textColor)) {
                setTextColor(a.getColor(R.styleable.CInputForm_textColor,0));
            }

            if (a.hasValue(R.styleable.CInputForm_text)) {
                setText(a.getString(R.styleable.CInputForm_text));
            }

            if (a.hasValue(R.styleable.CInputForm_textColorHint)) {
                setColorHint(a.getColor(R.styleable.CInputForm_textColorHint, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_textSize)) {
                setTextSize(a.getDimension(R.styleable.CInputForm_textSize, 0));
            }

            if (a.hasValue(R.styleable.CInputForm_textHint)) {
                setTextHint(a.getString(R.styleable.CInputForm_textHint));
            }

            if (a.hasValue(R.styleable.CInputForm_android_inputType)) {
                setInputType(a.getInt(R.styleable.CInputForm_android_inputType, EditorInfo.TYPE_NULL));
            }

            if (a.hasValue(R.styleable.CInputForm_isDropdown)) {
//                setDropdown(a.getBoolean(R.styleable.CInputForm_isDropdown, false));
            }

            if (a.hasValue(R.styleable.CInputForm_isShowBottomLine)) {
                setVisibilityBottomLine(a.getBoolean(R.styleable.CInputForm_isShowBottomLine, false));
            }

            a.recycle();
        }
    }

    public void setIconMoreText(String text){
        tvMore.setVisibility(VISIBLE);
        tvMore.setText(text);
    }

    private void setVisibilityBottomLine(boolean value){
        mLine.setVisibility(value ? GONE : VISIBLE);
    }

    public void setDropdown(boolean aBoolean, final ClickListener mListener) {
        if (!aBoolean){
            tvMore.setVisibility(GONE);
            edInput.setOnClickListener(null);
        }else {
            tvMore.setVisibility(VISIBLE);
            tvMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.hideKeyboard(v);
                    mListener.onClick(v);
                }
            });

            edInput.setFocusable(false);
            edInput.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.hideKeyboard(v);
                    mListener.onClick(v);
                }
            });
        }
    }

    public void setDropdownList(ArrayList<String> list){
        listDropdown = list;
    }

    public void setInputType(int type) {
        edInput.setInputType(type);

    }

    public void setFocusable(boolean value){
        if (value){
            edInput.setFocusable(true);
            edInput.setFocusableInTouchMode(true);

        }else {
            edInput.setFocusable(false);
        }
    }

    public void setMultiLine(boolean value){
        edInput.setMaxLines(3);
        edInput.setSingleLine(false);
    }

    public void setSelection() {
        edInput.requestFocus();
        edInput.setSelection(edInput.getText().toString().trim().length());

    }

    public void setTextHint(String string) {
        edInput.setHint(string);
    }

    public void setTextSize(float dimension) {
        edInput.setTextSize(dimension);

    }

    public void addTextChangeListenter(final OnQueryTextListener listener){
        edInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listener.textChanged(s.toString());
            }
        });
    }

    public void addTextChangePhone(CallbackString listener){
        edInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edInput.removeTextChangedListener(this);
                listener.Result(edInput.getText().toString().replace(".", ""));
                try {
                    edInput.setText(Util.FormatPhone(edInput.getText().toString().replace(".", "")));
                    edInput.setSelection(edInput.getText().toString().length());

                    //listener.Result(edInput.getText().toString().replace(".", ""));
                    edInput.addTextChangedListener(this);





                } catch (Exception ex) {
//                    ex.printStackTrace();
                    edInput.addTextChangedListener(this);
                }

            }
        });
    }

    public void addTextChangeName(CallbackString listener){
        final int[] countText = {0};
        edInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                countText[0] = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edInput.removeTextChangedListener(this);
                listener.Result(s.toString());
                if (countText[0] < s.toString().length() && s.toString().length() == 1){
                    if (s.toString().equals("a") || s.toString().equals("A")){
                        edInput.setText("Anh ");
                        edInput.setSelection(edInput.getText().toString().length());

                    }else if (s.toString().equals("c") || s.toString().equals("C")){
                        edInput.setText("Chị ");
                        edInput.setSelection(edInput.getText().toString().length());
                    }



                }

                //listener.Result(edInput.getText().toString().trim());
                edInput.addTextChangedListener(this);







            }
        });
    }

    public void textMoneyEvent(final CallbackDouble mlistener) {
        edInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edInput.removeTextChangedListener(this);
                try {
                    edInput.setText(Util.FormatMoney(Util.valueMoney(edInput)));
                    edInput.setSelection(edInput.getText().toString().length());

                    mlistener.Result(Util.valueMoney(edInput));
                    edInput.addTextChangedListener(this);





                } catch (Exception ex) {
//                    ex.printStackTrace();
                    edInput.addTextChangedListener(this);
                }

            }
        });


    }


    public void setColorHint(int color) {
        edInput.setHintTextColor(color);
    }

    public void setText(String string) {
        edInput.setText(string);
    }

    public String getText() {
        return edInput.getText().toString();
    }

    public void setTextColor(int color) {
        edInput.setTextColor(color);
    }

    public void setBackgroundColor(int color) {
        //edInput.setBackgroundColor(color);
        mLayout.setBackgroundColor(color);

    }


    public void setIconText(String string) {
        tvIcon.setText(string);
    }

    public void setIconSize(float dimension) {
        tvIcon.setTextSize(dimension);
    }

    public void setIconColor(int color) {
        tvIcon.setTextColor(color);
    }

    private void createDropdown(ArrayList<String> list){
        final PopupWindow popup = new PopupWindow(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), android.R.layout.select_dialog_item){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String text = getItem(position);
                TextView listItem = new TextView(Util.getInstance().getCurrentActivity());
                listItem.setText(text);
                listItem.setTag(position);
                listItem.setTextSize(16);
                listItem.setPadding(30, 20, 30, 20);
                listItem.setTextColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.white));
                return listItem;
            }
        };

        for (int i = 0; i < list.size(); i++) {
            adapter.add(list.get(i));

        }

        ListView listViewDogs = new ListView(Util.getInstance().getCurrentActivity());
        listViewDogs.setAdapter(adapter);

        listViewDogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edInput.setText(parent.getItemAtPosition(position).toString());
                popup.dismiss();
            }
        });
        popup.setFocusable(true);

        popup.setBackgroundDrawable(getResources().getDrawable(R.color.orange));
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(300);
        popup.setContentView(listViewDogs);
        popup.showAsDropDown(tvMore, 0, 0);
    }

    public void setOnMoreClickView(OnClickListener listener){
        tvMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mLayout.getRootView());
            }
        });

        edInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mLayout.getRootView());
            }
        });
    }

    public interface OnQueryTextListener {
        boolean textChanged(String query);

    }

    public interface ClickListener {
        void onClick(View view);

    }


}
