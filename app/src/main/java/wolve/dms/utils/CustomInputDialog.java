package wolve.dms.utils;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;

public class CustomInputDialog {
    private static DialogPlus dialog ;

    public static void dismissDialog(){
        if (dialog != null && dialog.isShowing() )
            dialog.dismiss();
    }

    public static void inputShopName(String title, final String shop_type, final CallbackString mListener){
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_shopname))
                .setGravity(Gravity.CENTER)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10, 10, 10, 10)
                .setPadding(20, 20, 20, 20)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();
//        LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.input_shopnname_parent);
        TextView tvShopType = (TextView) dialog.findViewById(R.id.input_shopnname_type);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.input_shopname_title);
        final EditText edName = (EditText) dialog.findViewById(R.id.input_shopnname_name);
        ImageView btnSubmit = (ImageView) dialog.findViewById(R.id.input_shopnname_submit);

        tvShopType.setText(Constants.getShopInfo(shop_type, null));
        if (title == null){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText("Tạo nhanh cửa hàng");
        }

        edName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mListener.Result(edName.getText().toString().trim());
//                    openCustomerScreen(shop_type, edName.getText().toString().trim(), null);
                    handled = true;
                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                if (!edName.getText().toString().trim().equals("")) {
                    mListener.Result(edName.getText().toString().trim());
//                    openCustomerScreen(shop_type, edName.getText().toString().trim(), null);
                }
            }
        });

        dialog.show();
        edName.requestFocus();
        Util.showKeyboard(edName);
    }

    public static void inputPhoneNumber(final CallbackString mListener){
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_phonenumber))
                .setGravity(Gravity.CENTER)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10, 10, 10, 10)
                .setPadding(20, 30, 20, 20)
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
        LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.input_phonenumber_parent);
        final EditText edPhone = (EditText) dialog.findViewById(R.id.input_phonenumber_phone);
        ImageView btnSubmit = (ImageView) dialog.findViewById(R.id.input_phonenumber_submit);

        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mListener.Result(edPhone.getText().toString().trim());
                    //openCustomerScreen(null, null, edPhone.getText().toString().trim());
                    handled = true;
                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                if (!edPhone.getText().toString().trim().equals("")) {
                    mListener.Result(edPhone.getText().toString().trim());
                    //openCustomerScreen(null, null, edPhone.getText().toString().trim());
                }
            }
        });

        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);
    }

}
