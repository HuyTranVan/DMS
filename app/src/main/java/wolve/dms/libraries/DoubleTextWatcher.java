package wolve.dms.libraries;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import wolve.dms.callback.CallbackString;
import wolve.dms.utils.Util;


/**
 * Created by engine on 6/4/17.
 */

public class DoubleTextWatcher implements TextWatcher {
    EditText edText;
    CallbackString mListener;

    public DoubleTextWatcher(EditText editText, CallbackString callback) {
        this.edText = editText;
        this.mListener = callback;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            edText.removeTextChangedListener(this);
            //Store current selection and string length
            int currentSelection = edText.getSelectionStart();
            int prevStringLength = edText.getText().length();

            String valueInString = edText.getText().toString();
            if (!TextUtils.isEmpty(valueInString)) {
                String str = edText.getText().toString().trim().replaceAll(",|\\s|\\.", "");
                String newString = Util.CurrencyUtil.convertDecimalToString(new BigDecimal(str));
                edText.setText(newString);
                //Set new selection
                int selection = currentSelection + (newString.length() - prevStringLength);
                edText.setSelection(selection);


            }
//                    edText.setSelection(selection);
            mListener.Result(edText.getText().toString().trim().replaceAll(",|\\s|\\.", ""));
            edText.addTextChangedListener(this);

            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            edText.addTextChangedListener(this);
        }


//            try {
//                editText.removeTextChangedListener(this);
//                //Store current selection and string length
//                int currentSelection = editText.getSelectionStart();
//                int prevStringLength = editText.getText().length();
//
//                String valueInString = editText.getText().toString();
//                if (!TextUtils.isEmpty(valueInString)) {
//                    String str = editText.getText().toString().trim().replaceAll(",|\\s|\\.", "");
//                    String newString = Util.CurrencyUtil.convertDecimalToString(new BigDecimal(str));
//                    editText.setText(newString);
//                    //Set new selection
//                    int selection = currentSelection + (newString.length() - prevStringLength);
//
//                    editText.setSelection(selection);
//                }
//                mListener.Result(editText.getText().toString().trim().replaceAll(",|\\s|\\.", ""));
//                editText.addTextChangedListener(this);
//                return;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                editText.addTextChangedListener(this);
//            }

    }

    public static String getDecimalFormattedString(String value) {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt(-1 + str1.length()) == '.') {
            j--;
            str3 = ".";
        }
        for (int k = j; ; k--) {
            if (k < 0) {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3) {
                str3 = "," + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }

    }

    public static String trimCommaOfString(String string) {
//        String returnString;
        if (string.contains(",")) {
            return string.replace(",", "");
        } else {
            return string;
        }

    }
}