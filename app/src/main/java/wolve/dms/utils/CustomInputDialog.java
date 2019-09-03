package wolve.dms.utils;

import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

//import com.hold1.keyboardheightprovider.KeyboardHeightProvider;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;

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
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(5, 0, 5, 5)
//                .setPadding(20, 20, 20, 20)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();
        final LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.input_shopnname_parent);
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
                        mListener.onShopname(edName.getText().toString().trim(), Constants.getShopName(spShopType.getSelectedItem().toString()));
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
                    mListener.onShopname(edName.getText().toString().trim(), Constants.getShopName(spShopType.getSelectedItem().toString()));
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
                int heightDifference = screenHeight - (r.bottom - r.top);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, heightDifference +5);
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
                .setMargin(5, 0, 5, 5)
//                .setPadding(20, 30, 20, 20)
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


        final LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.input_phonenumber_parent);
        final EditText edPhone = (EditText) dialog.findViewById(R.id.input_phonenumber_phone);
        CTextIcon btnSubmit = (CTextIcon) dialog.findViewById(R.id.input_phonenumber_submit);


        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (edPhone.getText().toString().trim().matches(Util.DETECT_PHONE)){
                        mListener.Result(edPhone.getText().toString().trim());
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

                if (edPhone.getText().toString().trim().matches(Util.DETECT_PHONE)){
                    mListener.Result(edPhone.getText().toString().trim());
                    Util.hideKeyboard(v);
//                    handled = true;

                }else {
                    Util.showToast("Sai số điện thoại");
                }
//                if (!edPhone.getText().toString().trim().equals("")) {
//                    mListener.Result(edPhone.getText().toString().trim());
//
//                }
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, heightDifference);
                lnParent.setLayoutParams(params);


                //boolean visible = heightDiff > screenHeight / 3;
            }
        });

//        Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_input_phonenumber);
//        LinearLayout lnParent = (LinearLayout) dialogResult.findViewById(R.id.input_phonenumber_parent);
//        final EditText edPhone = (EditText) dialogResult.findViewById(R.id.input_phonenumber_phone);
//        CTextIcon btnSubmit = (CTextIcon) dialogResult.findViewById(R.id.input_phonenumber_submit);
//
//        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    mListener.Result(edPhone.getText().toString().trim());
//                    //openCustomerScreen(null, null, edPhone.getText().toString().trim());
//                    handled = true;
//                }
//                return handled;
//            }
//        });
//
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//                                         @Override
//                                         public void onClick(View v) {
//                                             Util.hideKeyboard(v);
//                                             if (!edPhone.getText().toString().trim().equals("")) {
//                                                 mListener.Result(edPhone.getText().toString().trim());
//                                                 //openCustomerScreen(null, null, edPhone.getText().toString().trim());
//                                             }
//                                         }
//                                     });
//
//        dialogResult.show();
//        dialogResult.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        dialogResult.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        dialog.show();
//        edPhone.requestFocus();
//        Util.showKeyboard(edPhone);

//        final KeyboardHeightProvider keyboardHeightProvider = new KeyboardHeightProvider(Util.getInstance().getCurrentActivity());
//        view.post(new Runnable() {
//            @Override
//            public void run() {
//                keyboardHeightProvider.start();
//            }
//        });
//        keyboardHeightProvider.addKeyboardListener(new KeyboardHeightProvider.KeyboardListener() {
//            @Override
//            public void onHeightChanged(int i) {
//                Util.showToast(String.valueOf(i));
//            }
//        });

        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);

    }

}
