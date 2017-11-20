package wolve.dms.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.R;
import wolve.dms.adapter.BluetoothListAdapter;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.callback.CallbackListProduct;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.callback.CallbackStatus;
import wolve.dms.controls.CTextIcon;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.Status;
import wolve.dms.models.User;

/**
 * Created by macos on 9/28/17.
 */

public class CustomCenterDialog {

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
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_product);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_choice_product_title);
        final RadioGroup radioGroup = (RadioGroup) dialogResult.findViewById(R.id.dialog_choice_product_radiogroup);
        RecyclerView rvProduct = (RecyclerView) dialogResult.findViewById(R.id.dialog_choice_product_rvProduct);
        Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);

        btnCancel.setText("HỦY");
        btnSubmit.setText("HOÀN TẤT");
        tvTitle.setText(title);
        int initialPosition = 0;
        int initialGroupId = ProductGroup.getProductGroupList().get(initialPosition).getInt("id");

        for (int i=0; i<ProductGroup.getProductGroupList().size(); i++){
            RadioButton radioButton = new RadioButton(Util.getInstance().getCurrentActivity());
            radioButton.setText(ProductGroup.getProductGroupList().get(i).getString("name"));
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
                int groupId = ProductGroup.getProductGroupList().get(position).getInt("id");
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
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_edit_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_edit_product_title);
        TextView tvUnitPrice = dialogResult.findViewById(R.id.dialog_edit_product_unitprice);
        final TextView tvNetPrice = dialogResult.findViewById(R.id.dialog_edit_product_netprice);
        final TextView tvTotal =  dialogResult.findViewById(R.id.dialog_edit_product_total);
        final EditText edDiscount =  dialogResult.findViewById(R.id.dialog_edit_product_discount);
        final EditText edQuantity = dialogResult.findViewById(R.id.dialog_edit_product_quantity);
        CTextIcon tvSub = dialogResult.findViewById(R.id.dialog_edit_product_sub);
        CTextIcon tvPlus =  dialogResult.findViewById(R.id.dialog_edit_product_plus);

        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");
        tvTitle.setText(product.getString("name"));
        tvUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
        edDiscount.setText(String.valueOf(Math.round(product.getDouble("discount"))));
        tvNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - product.getDouble("discount")));
        edQuantity.setText(String.valueOf(Math.round(product.getDouble("quantity"))));
        tvTotal.setText(Util.FormatMoney(product.getDouble("quantity") * (product.getDouble("unitPrice") - product.getDouble("discount"))));

        edDiscount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tvUnitPrice.getText().toString().trim().replace(".","").length() )});

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
                    if (Util.valueMoney(text) > product.getDouble("unitPrice")){
                        Toast.makeText(Util.getInstance().getCurrentActivity(), "Vui lòng nhập giá tiền nhỏ hơn giá niêm yết",Toast.LENGTH_SHORT).show();

                    }else {
                        if (text.length() >0){
                            tvNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - Util.valueMoney(text)));
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

                } else {
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
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_status);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_choice_status_title);
        RecyclerView rvStatus = dialogResult.findViewById(R.id.dialog_choice_status_rvStatus);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);

        btnCancel.setText("HỦY");
        btnSubmit.setText("CHỈ CẬP NHẬT");
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

    public static void showDialogInputPaid(String title, String totalTitle, Double total, final CallbackPayBill mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_input_paid);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_title);
        final TextView tvTotal = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total);
        final TextView tvTotalTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total_title);
        final EditText edPaid = (EditText) dialogResult.findViewById(R.id.dialog_input_paid_paid);
        final TextView tvRemain = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_remain);
        Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        tvTotal.setText(Util.FormatMoney(total));
        edPaid.setText("");
        tvRemain.setText(Util.FormatMoney(total));
        Util.showKeyboard(edPaid);

        edPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                if (!text.equals("")){
                    tvRemain.setText(Util.FormatMoney(Util.valueMoney(tvTotal) - Util.valueMoney(text)));
                }else {
                    tvRemain.setText(tvTotal.getText().toString());
                }

                try {
                    edPaid.removeTextChangedListener(this);
                    //Store current selection and string length
                    int currentSelection = edPaid.getSelectionStart();
                    int prevStringLength = edPaid.getText().length();

                    String valueInString = edPaid.getText().toString();
                    if (!TextUtils.isEmpty(valueInString)) {
                        String str = edPaid.getText().toString().trim().replaceAll(",|\\s|\\.", "");
                        String newString = Util.CurrencyUtil.convertDecimalToString(new BigDecimal(str));
                        edPaid.setText(newString);
                        //Set new selection
                        int selection = currentSelection + (newString.length() - prevStringLength);
                        edPaid.setSelection(selection);
                    }
                    edPaid.addTextChangedListener(this);
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    edPaid.addTextChangedListener(this);
                }

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
                if (edPaid.getText().toString().equals("") || edPaid.getText().toString().equals("0") ){
                    dialogResult.dismiss();
                    mListener.OnRespone(Util.valueMoney(tvTotal) , 0.0);

                }else if (!edPaid.getText().toString().equals("") && !edPaid.getText().toString().equals("0") ){
                    if (Util.valueMoney(edPaid) > Util.valueMoney(tvTotal)){
                        Util.showToast("Số tiền nhập nhỏ hơn số tiền nợ!");
                    }else {
                        dialogResult.dismiss();
                        mListener.OnRespone(Util.valueMoney(tvTotal) , Util.valueMoney(edPaid));
                    }


                }

            }
        });
    }

    public static void showDialogBillImage(Customer customer,String total,  List<Product> listProduct, final CallbackPayBill mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_bill);
        dialogResult.setCancelable(true);
        TextView tvShopName = (TextView) dialogResult.findViewById(R.id.dialog_bill_shopname);
        TextView tvCustomerName = (TextView) dialogResult.findViewById(R.id.dialog_bill_customer);
        TextView tvPhone = (TextView) dialogResult.findViewById(R.id.dialog_bill_phone);
        TextView tvAddress = (TextView) dialogResult.findViewById(R.id.dialog_bill_address);
        TextView tvDate = (TextView) dialogResult.findViewById(R.id.dialog_bill_time);
        TextView tvSalesman = (TextView) dialogResult.findViewById(R.id.dialog_bill_sale);
        TextView tvTotal = (TextView) dialogResult.findViewById(R.id.dialog_bill_total);
        RecyclerView rvBill = (RecyclerView) dialogResult.findViewById(R.id.dialog_bill_rvbill);


        tvShopName.setText(Constants.getShopInfo(customer.getString("shopType") , null) + " "+ customer.getString("signBoard"));
        tvCustomerName.setText(customer.getString("name") == null? "" : customer.getString("name"));
        tvPhone.setText(customer.getString("phone") == null? "" : customer.getString("phone"));
        tvAddress.setText((customer.getString("address") == null? "" : customer.getString("address")) +" " +customer.getString("street") + " "+ customer.getString("district"));
        tvDate.setText(Util.CurrentMonthYearHour());
        tvSalesman.setText(User.getFullName());
        tvTotal.setText(total);


    }

    public static void showBluetoothDevices(List<BluetoothDevice> listDevice, final BluetoothListAdapter.CallbackBluetooth mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_bluetooth_device);
        dialogResult.setCancelable(true);
        dialogResult.setCanceledOnTouchOutside(true);
        TextView tvTitle =  dialogResult.findViewById(R.id.dialog_select_bluetooth_title);
        RecyclerView rvBluetooth =  dialogResult.findViewById(R.id.dialog_select_bluetooth_rvdevice);

        BluetoothListAdapter adapter = new BluetoothListAdapter(listDevice, new BluetoothListAdapter.CallbackBluetooth() {
            @Override
            public void OnDevice(BluetoothDevice device) {
                mListener.OnDevice(device);
                dialogResult.dismiss();
            }
        });
        rvBluetooth.setAdapter(adapter);
        rvBluetooth.setHasFixedSize(true);
        rvBluetooth.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvBluetooth.setLayoutManager(layoutManager);


    }

}
