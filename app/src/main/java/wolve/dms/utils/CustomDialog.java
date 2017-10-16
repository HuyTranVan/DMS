package wolve.dms.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.R;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.callback.CallbackListProduct;
import wolve.dms.callback.CallbackStatus;
import wolve.dms.controls.CTextView;
import wolve.dms.models.Product;
import wolve.dms.models.Status;

/**
 * Created by macos on 9/28/17.
 */

public class CustomDialog {

    public static Dialog showCustomDialog(int resId) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Util.getInstance().getCurrentActivity());
        final Dialog d = adb.setView(new View(Util.getInstance().getCurrentActivity())).create();
        //create corner edge for dialog
//        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.getWindow().setBackgroundDrawableResource(R.drawable.colorwhite_corner_large);

        d.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        d.getWindow().setAttributes(lp);
        d.show();
        d.setContentView(resId);

        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        return d;
    }

    public static Dialog showCustomDialogNotCancel(int resId) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Util.getInstance().getCurrentActivity());
        final Dialog d = adb.setView(new View(Util.getInstance().getCurrentActivity())).create();
        //create corner edge for dialog
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        d.setCanceledOnTouchOutside(false);
        d.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        d.getWindow().setAttributes(lp);
        d.show();
        d.setContentView(resId);

        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        return d;
    }

    public static Dialog showCustomDialog(int resId, Boolean showLater) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Util.getInstance().getCurrentActivity());
        final Dialog d = adb.setView(new View(Util.getInstance().getCurrentActivity())).create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        d.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        d.getWindow().setAttributes(lp);
        if (!showLater) {
            d.show();
        }
        d.setContentView(resId);
        return d;
    }

    public static void alert(final String title, final String message, final String confirm) {
        Util.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {
                new SweetAlertDialog(Util.getInstance().getCurrentActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(title)
                        .setContentText(message)
                        .setConfirmText(confirm)
                        .show();
            }
        });
    }

    public static void alertWithButton(final String title, final String message, final String confirm, final CallbackBoolean callback) {
        Util.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {
                SweetAlertDialog dialog = new SweetAlertDialog(Util.getInstance().getCurrentActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(title)
                        .setContentText(message)
                        .setConfirmText(confirm)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                callback.onRespone(null);
                            }
                        });
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    public static void alertWithCancelButton(final String title, final String message, final String confirm, final String cancel, final CallbackBoolean callback) {
        Util.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {
                SweetAlertDialog dialog = new SweetAlertDialog(Util.getInstance().getCurrentActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(title)
                        .setContentText(message)
                        .setConfirmText(confirm)
                        .setCancelText(cancel)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                callback.onRespone(null);
                            }
                        });
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    public static void showDialogChoiceProduct(String title, List<Product> listProduct,Boolean isPromotion, final CallbackListProduct callback){
        final Dialog dialogResult = CustomDialog.showCustomDialog(R.layout.view_dialog_select_product);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_choice_product_title);
        final RadioGroup radioGroup = (RadioGroup) dialogResult.findViewById(R.id.dialog_choice_product_radiogroup);
        RecyclerView rvProduct = (RecyclerView) dialogResult.findViewById(R.id.dialog_choice_product_rvProduct);
        Button btnCancel = (Button) dialogResult.findViewById(R.id.dialog_choice_product_cancel);
        Button btnSubmit = (Button) dialogResult.findViewById(R.id.dialog_choice_product_submit);

        tvTitle.setText(title);
        int initialPosition = 0;
        int initialGroupId = Util.mListProductGroup.get(initialPosition).getInt("id");

        for (int i=0; i<Util.mListProductGroup.size(); i++){
            RadioButton radioButton = new RadioButton(Util.getInstance().getCurrentActivity());
            radioButton.setText(Util.mListProductGroup.get(i).getString("name"));
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            radioButton.setId(i);

            RadioGroup.LayoutParams llp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 10, 0); // llp.setMargins(left, top, right, bottom);
            radioButton.setLayoutParams(llp);

            radioButton.setChecked(i == initialPosition ? true : false);
            radioGroup.addView(radioButton);
        }

        final CartProductDialogAdapter adapter = new CartProductDialogAdapter(listProduct, initialGroupId, isPromotion);
        rvProduct.setAdapter(adapter);
        rvProduct.setHasFixedSize(true);
        rvProduct.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(layoutManager);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = group.getCheckedRadioButtonId();
                int groupId = Util.mListProductGroup.get(position).getInt("id");
                adapter.reloadList(groupId);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Product> listChecked = new ArrayList<Product>();
                for (int i=0; i<adapter.getAllData().size(); i++){
                    if (adapter.getAllData().get(i).getBoolean("checked")){
                        listChecked.add(adapter.getAllData().get(i));
                    }
                }
                callback.Products(listChecked);
                dialogResult.dismiss();
            }
        });


    }

    public static void showDialogEditProduct(final Product product, final CallbackClickProduct callbackClickProduct){
        final Dialog dialogResult = CustomDialog.showCustomDialog(R.layout.view_dialog_edit_product);

        final Button btnCancel = (Button) dialogResult.findViewById(R.id.dialog_edit_product_cancel);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.dialog_edit_product_submit);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_edit_product_title);
        TextView tvUnitPrice = (TextView) dialogResult.findViewById(R.id.dialog_edit_product_unitprice);
        final TextView tvNetPrice = (TextView) dialogResult.findViewById(R.id.dialog_edit_product_netprice);
        final TextView tvTotal = (TextView) dialogResult.findViewById(R.id.dialog_edit_product_total);
        final EditText edDiscount = (EditText) dialogResult.findViewById(R.id.dialog_edit_product_discount);
        final EditText edQuantity = (EditText) dialogResult.findViewById(R.id.dialog_edit_product_quantity);
        CTextView tvSub = (CTextView) dialogResult.findViewById(R.id.dialog_edit_product_sub);
        CTextView tvPlus = (CTextView) dialogResult.findViewById(R.id.dialog_edit_product_plus);

        tvTitle.setText(product.getString("name"));
        tvUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
        edDiscount.setText(String.valueOf(Math.round(product.getDouble("discount"))));
        tvNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - product.getDouble("discount")  ));
        edQuantity.setText(String.valueOf(Math.round(product.getDouble("quantity"))));
        tvTotal.setText(Util.FormatMoney(product.getDouble("quantity") * (product.getDouble("unitPrice") - product.getDouble("discount"))));

        edDiscount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(product.getString("unitPrice").length() -1)});


        edQuantity.requestFocus();
        edQuantity.setSelection(edQuantity.getText().toString().length());
        Util.showKeyboard(edQuantity);

        edDiscount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.equals("")){
                    if (Double.parseDouble(text) > product.getDouble("unitPrice")){
                        Toast.makeText(Util.getInstance().getCurrentActivity(), "Vui lòng nhập giá tiền nhỏ hơn giá niêm yết",Toast.LENGTH_SHORT).show();

                    }else {
                        if (text.length() >0){
                            tvNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - Double.parseDouble(text)));
                            tvTotal.setText(Util.FormatMoney(Double.parseDouble(edQuantity.getText().toString()) * (product.getDouble("unitPrice") - Double.parseDouble(text))));
                        }
                    }
                }

            }
        });

        edQuantity.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.equals("") && !text.equals("0")){
                    tvTotal.setText(Util.FormatMoney(Double.parseDouble(text) * (product.getDouble("unitPrice") - Double.parseDouble(edDiscount.getText().toString().equals("") ? "0" : edDiscount.getText().toString()))));
                }
            }

        });

        tvSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(edQuantity.getText().toString()) > 0 ){
                    edQuantity.setText(String.valueOf(Integer.parseInt(edQuantity.getText().toString()) - 1));
                }
            }
        });

        tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edQuantity.setText(String.valueOf(Integer.parseInt(edQuantity.getText().toString()) + 1));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (edQuantity.getText().toString().equals("") || edQuantity.getText().toString().equals("0")){
                    Toast.makeText(Util.getInstance().getCurrentActivity(), "Vui lòng nhập số lượng >0 ",Toast.LENGTH_SHORT).show();

                }
                else {
                    try {
                        Product newProduct = product;
                        newProduct.put("quantity", Integer.parseInt(edQuantity.getText().toString()));
                        newProduct.put("discount", edDiscount.getText().toString().equals("") ? 0 : Double.parseDouble(edDiscount.getText().toString()));
                        newProduct.put("totalMoney", tvTotal.getText().toString().replace(".",""));

                        callbackClickProduct.ProductChoice(newProduct);
                        dialogResult.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    }

    public static void showCheckinReason(String title, List<Status> listStatus, final CallbackStatus callback){
        final Dialog dialogResult = CustomDialog.showCustomDialog(R.layout.view_dialog_select_status);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_choice_status_title);
        RecyclerView rvStatus = (RecyclerView) dialogResult.findViewById(R.id.dialog_choice_status_rvStatus);
        Button btnCancel = (Button) dialogResult.findViewById(R.id.dialog_choice_status_cancel);
        Button btnSubmit = (Button) dialogResult.findViewById(R.id.dialog_choice_status_submit);

        tvTitle.setText(title);

        final CartCheckinReasonAdapter adapter = new CartCheckinReasonAdapter(listStatus, new CallbackStatus() {
            @Override
            public void Status(Status status) {
                callback.Status(status);
                dialogResult.dismiss();
            }
        });
        rvStatus.setAdapter(adapter);
        rvStatus.setHasFixedSize(true);
        rvStatus.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvStatus.setLayoutManager(layoutManager);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.Status(null);
                dialogResult.dismiss();
            }
        });


    }



}
