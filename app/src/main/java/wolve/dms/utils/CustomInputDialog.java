package wolve.dms.utils;

import android.graphics.Rect;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

//import com.hold1.keyboardheightprovider.KeyboardHeightProvider;
import androidx.cardview.widget.CardView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;

public class CustomInputDialog {
    private static DialogPlus dialog ;

    public static void dismissDialog(){
        if (dialog != null && dialog.isShowing() )
            dialog.dismiss();
    }

    public interface ShopNameListener{
        void onShopname(String shopname, String shoptype);
    }

    public static void inputShopName(View view, final ShopNameListener mListener){
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_shopname))
                .setGravity(Gravity.BOTTOM)
                .setInAnimation(R.anim.slide_up)
                .setBackgroundColorResId(R.drawable.transparent)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();

        final CardView lnParent = (CardView) dialog.findViewById(R.id.input_shopnname_parent);
        final Spinner spShopType = (Spinner) dialog.findViewById(R.id.input_shopnname_shoptype);
        final EditText edName = (EditText) dialog.findViewById(R.id.input_shopnname_name);
        CTextIcon btnSubmit = (CTextIcon) dialog.findViewById(R.id.input_shopnname_submit);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_text, Constants.shopName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spShopType.setAdapter(dataAdapter);

        edName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!edName.getText().toString().trim().equals("")) {
                        mListener.onShopname(edName.getText().toString().trim(), Constants.getShopType(spShopType.getSelectedItem().toString()));
                        handled = true;

                    }else {
                        Util.showToast("Nhập chưa đủ thông tin");
                    }

                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edName.getText().toString().trim().equals("")) {
                    mListener.onShopname(edName.getText().toString().trim(), Constants.getShopType(spShopType.getSelectedItem().toString()));
                    Util.hideKeyboard(v);

                }else {
                    Util.showToast("Nhập chưa đủ thông tin");
                }
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._34sdp);
                int margin = Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, 0, margin, heightDifference);
                lnParent.setLayoutParams(params);


            }
        });

        dialog.show();
        edName.requestFocus();
        Util.showKeyboard(edName);
    }

    public static void inputPhoneNumber(View view, final CallbackString mListener){
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_phonenumber))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.transparent)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialog) {
                        //Util.hideKeyboard(btnNewCustomer);
                    }
                })
                .create();


        final CardView lnParent = (CardView) dialog.findViewById(R.id.input_phonenumber_parent);
        final EditText edPhone = (EditText) dialog.findViewById(R.id.input_phonenumber_phone);
        CTextIcon btnSubmit = (CTextIcon) dialog.findViewById(R.id.input_phonenumber_submit);

        Util.textPhoneEvent(edPhone, new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });

        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Util.getPhoneValue(edPhone).matches(Util.DETECT_PHONE)){
                        mListener.Result(Util.getPhoneValue(edPhone));
                        handled = true;

                    }else {
                        Util.showToast("Sai số điện thoại");
                    }


                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.getPhoneValue(edPhone).matches(Util.DETECT_PHONE)){
                    mListener.Result(Util.getPhoneValue(edPhone));
                    Util.hideKeyboard(v);

                }else {
                    Util.showToast("Sai số điện thoại");
                }

            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._34sdp);
                int margin = Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, 0, margin, heightDifference);
                lnParent.setLayoutParams(params);


            }
        });



        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);

    }

    public static void inputNumber(View view,final CallbackString mListener){
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_number))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.transparent)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialog) {
                        //Util.hideKeyboard(btnNewCustomer);
                    }
                })
                .create();


        final CardView lnParent = (CardView) dialog.findViewById(R.id.input_number_parent);
        final EditText edPhone = (EditText) dialog.findViewById(R.id.input_number_text);
        CTextIcon btnSubmit = (CTextIcon) dialog.findViewById(R.id.input_number_submit);

        Util.textPhoneEvent(edPhone, new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });

        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mListener.Result(edPhone.getText().toString());

                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.Result(edPhone.getText().toString());

            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._34sdp);
                int margin = Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, 0, margin, heightDifference);
                lnParent.setLayoutParams(params);


            }
        });



        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);

    }

}
