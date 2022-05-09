package wolve.dms.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;

import wolve.dms.R;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;

//import com.hold1.keyboardheightprovider.KeyboardHeightProvider;

public class CustomInputDialog {
    private static DialogPlus dialog;

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public interface ShopNameListener {
        void onShopname(String shopname, int shoptype);
    }

    public interface ShopListener {
        void onShopname(String shopname, int shoptype, String phone);
    }

    public static void dialogNewCustomer(ShopListener mListener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_new_customer);
        LinearLayout lnParent = dialogResult.findViewById(R.id.dialog_newcustomer_parent);
        //TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_newcustomer_title);
        Spinner spShopType = dialogResult.findViewById(R.id.dialog_newcustomer_shoptype);
        final EditText edShopName = (EditText) dialogResult.findViewById(R.id.dialog_newcustomer_shopname);
        final CInputForm edPhone = (CInputForm) dialogResult.findViewById(R.id.dialog_newcustomer_phone);
        final TextView btnSubmit = (TextView) dialogResult.findViewById(R.id.dialog_newcustomer_submit);

        dialogResult.setCanceledOnTouchOutside(true);
        Util.textPhoneEvent(edPhone.getEdInput(), null);

        Util.showKeyboardDelay(edShopName);

        dialogResult.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Util.hideKeyboard(edPhone);
            }
        });

        final int[] shopType = {0};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_text, Constants.shopName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spShopType.setAdapter(dataAdapter);

        spShopType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                shopType[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isEmpty(edShopName)) {
                    if (Util.isPhoneFormat(edPhone.getText().toString()) != null){
                        mListener.onShopname(edShopName.getText().toString().trim(), shopType[0], Util.getPhoneValue(edPhone));
                        Util.hideKeyboard(v);
                        dialogResult.dismiss();

                    }else if (!Util.isEmpty(edPhone)){
                        Util.showToast("Sai định dạng số điện thoại");

                    }else {
                        mListener.onShopname(edShopName.getText().toString().trim(), shopType[0], "");
                        Util.hideKeyboard(v);
                        dialogResult.dismiss();

                    }



                } else {
                    if (Util.isPhoneFormat(edPhone.getText().toString()) != null){
                        mListener.onShopname("-", shopType[0], Util.getPhoneValue(edPhone));
                        Util.hideKeyboard(v);
                        dialogResult.dismiss();

                    }else {
                        Util.showToast("Sai định dạng thông tin");
                    }

                }


            }
        });


    }

    public static void inputShopName(View view, final ShopNameListener mListener) {
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_shopname))
                .setGravity(Gravity.BOTTOM)
                .setInAnimation(R.anim.slide_in_up)

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
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.input_shopnname_submit);

        final int[] shopType = {0};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_text, Constants.shopName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spShopType.setAdapter(dataAdapter);

        spShopType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                shopType[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!edName.getText().toString().trim().equals("")) {
                        mListener.onShopname(edName.getText().toString().trim(), shopType[0]);
                        handled = true;

                    } else {
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
                    mListener.onShopname(edName.getText().toString().trim(), shopType[0]);
                    Util.hideKeyboard(v);

                } else {
                    Util.showToast("Nhập chưa đủ thông tin");
                }
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout(){
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top) +  Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);
//                        - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._32sdp);
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

    public static void inputWarehouse(View view, final CallbackString mListener) {
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_warehouse))
                .setGravity(Gravity.BOTTOM)
                .setInAnimation(R.anim.slide_in_up)
                .setBackgroundColorResId(R.drawable.transparent)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();

        final CardView lnParent = (CardView) dialog.findViewById(R.id.input_warehouse_parent);
        final EditText edName = (EditText) dialog.findViewById(R.id.input_warehouse_name);
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.input_warehouse_submit);

        edName.requestFocus();
        Util.showKeyboardDelay(edName);

        edName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!edName.getText().toString().trim().equals("")) {
                        mListener.Result(edName.getText().toString().trim());
                        handled = true;

                    } else {
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
                    mListener.Result(edName.getText().toString().trim());
                    dismissDialog();
                    Util.hideKeyboard(v);

                } else {
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
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._32sdp);
                int margin = Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, 0, margin, margin);
                lnParent.setLayoutParams(params);


            }
        });

        dialog.show();


    }

    public static void inputPhoneNumber(View view, final CallbackString mListener) {
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_phonenumber))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.transparent)
                .setInAnimation(R.anim.slide_in_up)
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
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.input_phonenumber_submit);

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
                    if (Util.getPhoneValue(edPhone).matches(Util.DETECT_PHONE)) {
                        mListener.Result(Util.getPhoneValue(edPhone));
                        handled = true;

                    } else {
                        Util.showToast("Sai số điện thoại");
                    }


                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.getPhoneValue(edPhone).matches(Util.DETECT_PHONE)) {
                    mListener.Result(Util.getPhoneValue(edPhone));
                    Util.hideKeyboard(v);

                } else {
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
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._32sdp);
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

    public static void inputNumber(View view, final CallbackString mListener) {
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_number))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.transparent)
                .setInAnimation(R.anim.slide_in_up)
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
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.input_number_submit);

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
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._32sdp);
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

    public static void dialogDebtRange(int currentTime, CallbackInt listener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_debt_range);
        LinearLayout lnParent = dialogResult.findViewById(R.id.dialog_debtrange_parent);
        final CInputForm edTime = (CInputForm) dialogResult.findViewById(R.id.dialog_debtrange_time);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        dialogResult.setCanceledOnTouchOutside(true);

        edTime.textEvent();
        edTime.setText(String.valueOf(currentTime));

        btnSubmit.setText("cập nhật");
        Util.showKeyboardEditTextDelay(edTime.getEdInput());

        dialogResult.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Util.hideKeyboard(edTime);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isEmpty(edTime)){
                    Util.showToast("Nhập sai giá trị");
                }else {
                    listener.onResponse(Integer.parseInt(edTime.getText().toString()));
                    Util.hideKeyboard(edTime);
                    dialogResult.dismiss();
                }

//                if (!Util.isEmpty(edShopName)) {
//                    if (Util.isPhoneFormat(edPhone.getText().toString()) != null){
//                        mListener.onShopname(edShopName.getText().toString().trim(), shopType[0], Util.getPhoneValue(edPhone));
//                        Util.hideKeyboard(v);
//                        dialogResult.dismiss();
//
//                    }else if (!Util.isEmpty(edPhone)){
//                        Util.showToast("Sai định dạng số điện thoại");
//
//                    }else {
//                        mListener.onShopname(edShopName.getText().toString().trim(), shopType[0], "");
//                        Util.hideKeyboard(v);
//                        dialogResult.dismiss();
//
//                    }
//
//
//
//                } else {
//                    if (Util.isPhoneFormat(edPhone.getText().toString()) != null){
//                        mListener.onShopname("-", shopType[0], Util.getPhoneValue(edPhone));
//                        Util.hideKeyboard(v);
//                        dialogResult.dismiss();
//
//                    }else {
//                        Util.showToast("Sai định dạng thông tin");
//                    }
//
//                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        //dialogResult.show();
    }

}
