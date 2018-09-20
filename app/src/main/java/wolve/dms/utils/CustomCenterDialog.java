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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.R;
import wolve.dms.adapter.BluetoothListAdapter;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.adapter.ProductReturnAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackListProduct;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.DoubleTextWatcher;
import wolve.dms.models.BaseModel;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.Status;
import wolve.dms.models.User;

/**
 * Created by macos on 9/28/17.
 */

public class CustomCenterDialog {

    public interface ButtonCallback{
        void Submit(Boolean boolSubmit);
        void Cancel(Boolean boolCancel);
    }

    public static Dialog showCustomDialog(int resId) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Util.getInstance().getCurrentActivity());
        final Dialog d = adb.setView(new View(Util.getInstance().getCurrentActivity())).create();
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

    public static void alertWithCancelButton2(final String title, final String message, final String confirm, final String cancel, final ButtonCallback callback) {
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
                                callback.Cancel(true);
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                callback.Submit(true);
                            }
                        });
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();
            }
        });
    }

//    public static void showDialogChoiceProduct(String title, List<Product> listProduct,Boolean isPromotion, final CallbackListProduct callback){
//        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_product);
//        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_choice_product_title);
//        final RadioGroup radioGroup = (RadioGroup) dialogResult.findViewById(R.id.dialog_choice_product_radiogroup);
//        RecyclerView rvProduct = (RecyclerView) dialogResult.findViewById(R.id.dialog_choice_product_rvProduct);
//        Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
//        Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
//
//        btnCancel.setText("HỦY");
//        btnSubmit.setText("HOÀN TẤT");
//        tvTitle.setText(title);
//        int initialPosition = 0;
//        int initialGroupId = ProductGroup.getProductGroupList().get(initialPosition).getInt("id");
//
//        for (int i=0; i<ProductGroup.getProductGroupList().size(); i++){
//            RadioButton radioButton = new RadioButton(Util.getInstance().getCurrentActivity());
//            radioButton.setText(ProductGroup.getProductGroupList().get(i).getString("name"));
//            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//            radioButton.setId(i);
//
//            RadioGroup.LayoutParams llp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
//            llp.setMargins(0, 0, 10, 0); // llp.setMargins(left, top, right, bottom);
//            radioButton.setLayoutParams(llp);
//
//            radioButton.setChecked(i == initialPosition ? true : false);
//            radioGroup.addView(radioButton);
//        }
//
//        final CartProductDialogAdapter adapter = new CartProductDialogAdapter(listProduct, initialGroupId, isPromotion);
//        rvProduct.setAdapter(adapter);
//        rvProduct.setHasFixedSize(true);
//        rvProduct.setNestedScrollingEnabled(false);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
//        rvProduct.setLayoutManager(layoutManager);
//
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                int position = group.getCheckedRadioButtonId();
//                int groupId = ProductGroup.getProductGroupList().get(position).getInt("id");
//                adapter.reloadList(groupId);
//
//            }
//        });
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogResult.dismiss();
//            }
//        });
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                List<Product> listChecked = new ArrayList<Product>();
//                for (int i=0; i<adapter.getAllData().size(); i++){
//                    if (adapter.getAllData().get(i).getBoolean("checked")){
//                        listChecked.add(adapter.getAllData().get(i));
//                    }
//                }
//                callback.Products(listChecked);
//                dialogResult.dismiss();
//            }
//        });
//
//
//    }

    public static void showDialogEditProduct(final Product product, final CallbackClickProduct callbackClickProduct){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_edit_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_edit_product_title);
        EditText tvUnitPrice = dialogResult.findViewById(R.id.dialog_edit_product_unitprice);
        final EditText edNetPrice = dialogResult.findViewById(R.id.dialog_edit_product_netprice);
        final TextView tvTotal =  dialogResult.findViewById(R.id.dialog_edit_product_total);
        final EditText edDiscount =  dialogResult.findViewById(R.id.dialog_edit_product_discount);
        final EditText edQuantity = dialogResult.findViewById(R.id.dialog_edit_product_quantity);
        TextView btnChangeToPromotion = dialogResult.findViewById(R.id.dialog_edit_product_topromotion);
        TextView btnCopyToPromotion = dialogResult.findViewById(R.id.dialog_edit_product_copypromotion);

        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");
        tvTitle.setText(product.getString("name"));
        tvUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
        edDiscount.setText(product.getDouble("discount") ==0 ? "" : Util.FormatMoney((double) Math.round(product.getDouble("discount"))));
        edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - product.getDouble("discount")));
        edNetPrice.setFocusable(false);
        edQuantity.setText(String.valueOf(Math.round(product.getDouble("quantity"))));
        tvTotal.setText(Util.FormatMoney(product.getDouble("quantity") * (product.getDouble("unitPrice") - product.getDouble("discount"))));

        edDiscount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tvUnitPrice.getText().toString().trim().length() )});

        edQuantity.requestFocus();
        edQuantity.setSelection(edQuantity.getText().toString().length());
        Util.showKeyboard(edQuantity);

        Util.textMoneyEvent(edDiscount, new CallbackString() {
            @Override
            public void Result(String text) {
                if (!text.equals("")){
                    if (Util.valueMoney(text) > product.getDouble("unitPrice")){
                        Util.showToast("Vui lòng nhập giá tiền nhỏ hơn giá niêm yết");

                    }else {
                        if (text.length() >0){
                            edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - Util.valueMoney(text)));
                            tvTotal.setText(Util.FormatMoney(Double.parseDouble(edQuantity.getText().toString()) * (product.getDouble("unitPrice") - Double.parseDouble(text))));
                        }
                    }
                }else {
                    edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
                    tvTotal.setText(Util.FormatMoney(Double.parseDouble(edQuantity.getText().toString()) * (product.getDouble("unitPrice"))));

                }
            }
        });

        edNetPrice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edNetPrice.setFocusable(true);
                edNetPrice.setFocusableInTouchMode(true);
                Util.showKeyboard(v);

                edNetPrice.addTextChangedListener(new DoubleTextWatcher(edNetPrice, new CallbackString() {
                    @Override
                    public void Result(String text) {
                        if (!text.toString().equals("") && !text.equals("0")){
                            edDiscount.setText(Util.FormatMoney(product.getDouble("unitPrice") - Util.moneyValue(edNetPrice)));
                            tvTotal.setText(Util.FormatMoney(Double.parseDouble(text) *  Util.valueMoney(edQuantity)));
                            edNetPrice.setSelection(edNetPrice.getText().toString().length());
                        }else if (text.toString().equals("")){
                            tvTotal.setText("0");
                        }
                    }
                }));

                return true;
            }
        });

        edQuantity.addTextChangedListener(new DoubleTextWatcher(edQuantity, new CallbackString() {
            @Override
            public void Result(String text) {
                if (!text.toString().equals("") && !text.equals("0")){
                    tvTotal.setText(Util.FormatMoney(Double.parseDouble(text) * (product.getDouble("unitPrice") - Util.valueMoney(edDiscount))));

                }else if (text.toString().equals("")){
                    tvTotal.setText("0");
                }
            }
        }));

        btnChangeToPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Product newProduct = product;
                    newProduct.put("quantity", Integer.parseInt(edQuantity.getText().toString()));
                    newProduct.put("discount", product.getDouble("unitPrice"));
                    newProduct.put("totalMoney", "0");

                    callbackClickProduct.ProductChoice(newProduct);
                    dialogResult.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCopyToPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Product newProduct = new Product();

                    newProduct.put("id", product.getString("id"));
                    newProduct.put("name", product.getString("name"));
                    newProduct.put("productGroup", product.getString("productGroup"));
                    newProduct.put("promotion", product.getBoolean("promotion"));
                    newProduct.put("unitPrice", product.getDouble("unitPrice"));
                    newProduct.put("purchasePrice", product.getDouble("purchasePrice"));
                    newProduct.put("volume", product.getLong("volume"));
                    newProduct.put("image", product.getString("image"));
                    newProduct.put("imageUrl", product.getString("imageUrl"));
                    newProduct.put("checked", product.getBoolean("checked"));
                    newProduct.put("isPromotion", product.getBoolean("isPromotion"));
                    newProduct.put("quantity", Integer.parseInt(edQuantity.getText().toString()));
                    newProduct.put("discount", product.getDouble("unitPrice"));
                    newProduct.put("totalMoney", "0");

                    callbackClickProduct.ProductAdded(newProduct);
                    dialogResult.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();
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
            @Override public void onClick(View v) {
                if (Util.isEmptyValue(edQuantity) || Util.isEmptyValue(edNetPrice)){
                    Util.showToast("Vui lòng nhập số lượng >0 ");

                }else if (Util.isEmptyValue(edNetPrice)){
                    Util.showToast("Vui lòng nhập giá tiền  >0 ");

                }else if (Util.moneyValue(edNetPrice) > product.getDouble("unitPrice")){
                    Util.showToast("Vui lòng nhập giá tiền nhỏ hơn giá đơn vị ");

                }else{
                    try {
                        Product newProduct = product;
                        newProduct.put("quantity", Integer.parseInt(edQuantity.getText().toString()));
                        newProduct.put("discount", edDiscount.getText().toString().equals("") ? 0 : Util.valueMoney(edDiscount));
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

    public static void showDialogReturnProduct(final int customerId, List<BaseModel> listBill, final CallbackBoolean mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_return_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_return_product_title);
        RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_return_product_rv);
        final EditText edNote = dialogResult.findViewById(R.id.dialog_return_product_note);

        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");
        tvTitle.setText("DANH SÁCH SẢN PHẨM TRẢ");

        final ProductReturnAdapter adapter = new ProductReturnAdapter(listBill);
        rvProduct.setAdapter(adapter);
        rvProduct.setHasFixedSize(true);
        rvProduct.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(layoutManager);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                List<JSONObject> listProductSelected = new ArrayList<>();
                listProductSelected = adapter.getListSelected();

                if (listProductSelected.size() >0){
                    Double total =0.0;
                    try {
                        for (int i=0; i<listProductSelected.size(); i++){
                            total += (listProductSelected.get(i).getDouble("unitPrice") - listProductSelected.get(i).getDouble("discount")) *listProductSelected.get(i).getDouble("quantity") ;

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String params = DataUtil.createPostBillParam(customerId,total, total, adapter.getListSelected(), edNote.getText().toString().trim());

                    CustomerConnect.PostBill(params, new CallbackJSONObject() {
                        @Override
                        public void onResponse(JSONObject result) {
                            mListener.onRespone(true);
                            dialogResult.dismiss();
                        }

                        @Override
                        public void onError(String error) {
                            mListener.onRespone(false);
                            dialogResult.dismiss();

                        }
                    }, true);
                }else {
                    Util.showToast("Vui lòng chọn số lượng");

                }


            }
        });


    }

    public static void showCheckinReason(String title, List<Status> listStatus, final CartCheckinReasonAdapter.CallbackStatus callback){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_status);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_choice_status_title);
        RecyclerView rvStatus = dialogResult.findViewById(R.id.dialog_choice_status_rvStatus);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);

        btnCancel.setText("HỦY");
        tvTitle.setText(title);

        final CartCheckinReasonAdapter adapter = new CartCheckinReasonAdapter(listStatus, new CartCheckinReasonAdapter.CallbackStatus() {
            @Override
            public void Status(Status status) {
                callback.Status(status);
                dialogResult.dismiss();
            }

            @Override
            public void UpdateOnly() {
                callback.UpdateOnly();
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

        Util.textMoneyEvent(edPaid, new CallbackString() {
            @Override
            public void Result(String s) {
                if (!s.equals("")){
                    tvRemain.setText(Util.FormatMoney(Util.valueMoney(tvTotal) - Util.valueMoney(s)));
                }else {
                    tvRemain.setText(tvTotal.getText().toString());
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
                Util.hideKeyboard(v);
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

    public static void showDialogDatePicker(final RadioButton rdButton, final CallbackString mListener){
        final String result ="&billingFrom=%d&billingTo=%d";

        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_datepicker);
        dialogResult.setCancelable(true);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.title);
        Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final CalendarPickerView calendarView = dialogResult.findViewById(R.id.calendar_view);

        tvTitle.setText("Chọn thời gian");
        btnSubmit.setText("Chọn");
        btnCancel.setText("Hủy");

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 10);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -10);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);

//        calendarView.deactivateDates(list);
        ArrayList<Date> arrayList = new ArrayList<>();
        try {
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
            String strdate = "22-2-2018";
            String strdate2 = "26-2-2018";
            Date newdate = dateformat.parse(strdate);
            Date newdate2 = dateformat.parse(strdate2);
            arrayList.add(newdate);
            arrayList.add(newdate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendarView.init(lastYear.getTime(), nextYear.getTime(),new SimpleDateFormat("MMMM, YYYY", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date())
//                .withDeactivateDates(list)
                .withHighlightedDates(arrayList);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startDate = new Timestamp(calendarView.getSelectedDates().get(0).getTime()).getTime();
                long lastDate = new Timestamp(calendarView.getSelectedDates().get(calendarView.getSelectedDates().size()-1).getTime()).getTime() ;

                if (calendarView.getSelectedDates().size() ==1){
                    rdButton.setText(Util.DateString(startDate));
                }else {
                    rdButton.setText(String.format("%s\n%s", Util.DateString(startDate) ,Util.DateString(lastDate)));
                }


                mListener.Result(String.format(result, startDate, lastDate+ 86400000));
                dialogResult.dismiss();

            }
        });

    }


}
