package wolve.dms.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mukesh.DrawingView;
import com.savvi.rangedatepicker.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;


import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.R;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.ProductReturnAdapter;
import wolve.dms.adapter.StatisticalCheckinsAdapter;
import wolve.dms.adapter.StatisticalDebtAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackList;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.DoubleTextWatcher;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Product;
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

    public interface CallbackRangeTime{
        void onSelected(long start, long end);
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
                                callback.onRespone(true);
                            }
                        });
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
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

        Util.textMoneyEvent(edDiscount, null,new CallbackDouble() {
            @Override
            public void Result(Double text) {
                if (!text.equals("")){
                    if (text > product.getDouble("unitPrice")){
                        Util.showToast("Vui lòng nhập giá tiền nhỏ hơn giá niêm yết");

                    }else {
                        if (text >0){
                            edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - text));
                            tvTotal.setText(Util.FormatMoney(Double.parseDouble(edQuantity.getText().toString()) * (product.getDouble("unitPrice") - text)));
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
                        newProduct.put("quantity", Util.valueMoney(edQuantity));
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

    public static void showDialogReturnProduct(final int billId, Double total , final int customerId, List<BaseModel> listBill, final CallbackBoolean mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_return_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_return_product_title);
        RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_return_product_rv);
        final EditText edPay = dialogResult.findViewById(R.id.dialog_return_product_note);

        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");
        tvTitle.setText("DANH SÁCH SẢN PHẨM TRẢ");

        final ProductReturnAdapter adapter = new ProductReturnAdapter(listBill);
        Util.createLinearRV(rvProduct, adapter);

        Util.textMoneyEvent(edPay, total, new CallbackDouble() {
            @Override
            public void Result(Double d) {

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

//                    Double textPaid = Util.moneyValue(edPay);

                    final String params = DataUtil.createPostBillParam(customerId,total, 0.0, adapter.getListSelected(), String.valueOf(billId));

                    CustomerConnect.PostBill(params, new CallbackJSONObject() {
                        @Override
                        public void onResponse(JSONObject result) {

                            if (Util.moneyValue(edPay) ==0){
                                mListener.onRespone(true);
                                dialogResult.dismiss();
                            }else {
                                try {
                                    String param = String.format(Api_link.PAY_PARAM,
                                            customerId,
                                            String.valueOf(Math.round(Util.moneyValue(edPay) *-1)),
                                            result.getInt("id"),
                                            User.getId(),
                                            "");

                                    CustomerConnect.PostPay(param, new CallbackJSONObject() {
                                        @Override
                                        public void onResponse(JSONObject result) {
                                            mListener.onRespone(true);
                                            dialogResult.dismiss();
                                        }

                                        @Override
                                        public void onError(String error) {
                                            mListener.onRespone(false);
                                            dialogResult.dismiss();
                                        }//
                                    }, true);



                                } catch (JSONException e) {
                                    mListener.onRespone(false);
                                    dialogResult.dismiss();
                                }
                            }


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

    public static void showCheckinReason(String title, List<Status> listStatus, final CallbackString mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_status);
        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_choice_status_title);
        final CTextIcon btnMore = dialogResult.findViewById(R.id.dialog_choice_status_more);
        final EditText edNote = dialogResult.findViewById(R.id.dialog_choice_status_content);
        final RecyclerView rvStatus = dialogResult.findViewById(R.id.dialog_choice_status_rvStatus);
        FrameLayout frParent = dialogResult.findViewById(R.id.dialog_choice_status_parent);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogResult.findViewById(R.id.btn_submit);

        btnCancel.setText("HỦY");
        btnConfirm.setText("HOÀN TẤT");
        tvTitle.setText(title);
        rvStatus.setVisibility(View.GONE);
        dialogResult.setCanceledOnTouchOutside(true);

        final List<String> list = new ArrayList<>();
        for (int i=0; i<listStatus.size(); i++){
            if (!listStatus.get(i).getBoolean("defaultStatus")){
                list.add(listStatus.get(i).getString("name"));
            }
        }

        final CartCheckinReasonAdapter adapter = new CartCheckinReasonAdapter(list, new CallbackString() {
            @Override
            public void Result(String s) {
                edNote.setText(edNote.getText().toString().trim().equals("")? s+"\n" : edNote.getText().toString() +s+  "\n" );
                edNote.setSelection(edNote.length());
            }
        });

        Util.createLinearRV(rvStatus, adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                if (Util.isEmpty(edNote)){
                    Util.showToast("Vui lòng nhập tình trạng cửa hàng");
                }else {
                    dialogResult.dismiss();
                    mListener.Result(edNote.getText().toString().trim());
                }
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvStatus.getVisibility() == View.VISIBLE){
                    rvStatus.setVisibility(View.GONE);
                    btnMore.setRotation(315);
                }else {
                    rvStatus.setVisibility(View.VISIBLE);
                    btnMore.setRotation(135);
                }

            }
        });


    }

    public static void showDialogInputPaid(String title, String totalTitle, Double total, final CallbackPayBill mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_input_paid);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_title);
        final TextView tvTotal = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total);
        final TextView tvTotalTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total_title);
        final EditText edPaid = (EditText) dialogResult.findViewById(R.id.dialog_input_paid_money);
        final TextView tvRemain = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_remain);
        final RecyclerView rvDebt = dialogResult.findViewById(R.id.dialog_input_paid_rvdebt);
        Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        tvTotal.setText(Util.FormatMoney(total));
        edPaid.setText("");
        tvRemain.setText(Util.FormatMoney(total));
        Util.showKeyboard(edPaid);

        Util.textMoneyEvent(edPaid, null,new CallbackDouble() {
            @Override
            public void Result(Double s) {
                if (!s.equals("")){
                    tvRemain.setText(Util.FormatMoney(Util.valueMoney(tvTotal) - s));
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

    public static Dialog showDialogPayment(String title, final Double currentDebt, final int currentBillId, final List<JSONObject> listDebts ,Double paid ,final CallbackList mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_input_paid);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_title);
        final TextView tvTotal = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total);
        final TextView tvTotalTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total_title);
        final EditText edPaid = (EditText) dialogResult.findViewById(R.id.dialog_input_paid_money);
        final TextView tvRemain = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_remain);
        final RecyclerView rvDebt = dialogResult.findViewById(R.id.dialog_input_paid_rvdebt);
        final Switch swFastPay = dialogResult.findViewById(R.id.dialog_input_paid_switch);
        final TextView tvCurrentBillPaid = dialogResult.findViewById(R.id.dialog_input_paid_paid);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        final DebtAdapter debtAdapter = new DebtAdapter(listDebts == null? new ArrayList<JSONObject>() : listDebts, true);
        Util.createLinearRV(rvDebt, debtAdapter);

        final Double lastDebt = debtAdapter.getTotalMoney();
        final Double totalDebt =  currentDebt + lastDebt ;

        tvTitle.setText(title);
        tvRemain.setText(Util.FormatMoney(totalDebt));
        tvTotal.setText(Util.FormatMoney(currentDebt));
        //edPaid.setText("");
        swFastPay.setVisibility(listDebts == null?View.GONE: View.VISIBLE);

        Util.textMoneyEvent(edPaid,totalDebt, new CallbackDouble() {
            @Override
            public void Result(Double s) {
                if (swFastPay.isChecked()){
                    if (s< currentDebt){
                        tvCurrentBillPaid.setText(Util.FormatMoney(s));
                        debtAdapter.inputPaid(null);
                    }else {
                        tvCurrentBillPaid.setText(Util.FormatMoney(currentDebt));
                        debtAdapter.inputPaid(s-currentDebt);
                    }

                }else {
                    if (s < lastDebt){
                        tvCurrentBillPaid.setText("");
                        debtAdapter.inputPaid(s);

                    }else {
                        tvCurrentBillPaid.setText(Util.FormatMoney(s - lastDebt));
                        debtAdapter.inputPaid(lastDebt);
                    }
                }
                tvTotal.setText(Util.FormatMoney(currentDebt - Util.valueMoney(tvCurrentBillPaid)));
                tvRemain.setText(Util.FormatMoney(totalDebt - s));

            }
        });

        if (paid !=0){
            edPaid.setText(Util.FormatMoney(paid));
        }

        swFastPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Double s =Util.valueMoney(edPaid);
                if (isChecked){
                    if (s< currentDebt){
                        tvCurrentBillPaid.setText(Util.FormatMoney(s));
                        debtAdapter.inputPaid(null);
                    }else {
                        tvCurrentBillPaid.setText(Util.FormatMoney(currentDebt));
                        debtAdapter.inputPaid(s-currentDebt);
                    }

                }else {
                    if (s < lastDebt){
                        tvCurrentBillPaid.setText("");
                        debtAdapter.inputPaid(s);

                    }else {
                        tvCurrentBillPaid.setText(Util.FormatMoney(s - lastDebt));
                        debtAdapter.inputPaid(lastDebt);
                    }
                }
                tvTotal.setText(Util.FormatMoney(currentDebt - Util.valueMoney(tvCurrentBillPaid)));
                tvRemain.setText(Util.FormatMoney(totalDebt - s));
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
                List<JSONObject> listPayment = debtAdapter.getListBillPayment();
                try {
                    JSONObject object = new JSONObject();
                    object.put("billId", currentBillId);
                    object.put("paid", Util.valueMoney(tvCurrentBillPaid));

                    if (Util.valueMoney(tvCurrentBillPaid) !=0){
                        listPayment.add(0,object);
                    }

                    mListener.onResponse(listPayment);

                } catch (JSONException e) {
                    Util.showToast("Lỗi ");
                }
                dialogResult.dismiss();

            }
        });

        return dialogResult;
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

    public static void showDialogDatePicker(final RadioButton rdButton, final CallbackRangeTime mListener){
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
//        ArrayList<Date> arrayList = new ArrayList<>();
//        try {
//            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
//            String strdate = "22-2-2018";
//            String strdate2 = "26-2-2018";
//            Date newdate = dateformat.parse(strdate);
//            Date newdate2 = dateformat.parse(strdate2);
//            arrayList.add(newdate);
//            arrayList.add(newdate2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        calendarView.init(lastYear.getTime(), nextYear.getTime(),new SimpleDateFormat("MMMM, YYYY", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date());
//                .withDeactivateDates(list)
//                .withHighlightedDates(arrayList);

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
                    mListener.onSelected(startDate, startDate+86400000);
                }else {
                    rdButton.setText(String.format("%s\n%s", Util.DateString(startDate) ,Util.DateString(lastDate)));
                    mListener.onSelected(startDate,lastDate +86400000);
                }

//                mListener.Result(String.format(result, startDate, lastDate+ 86400000));
                dialogResult.dismiss();

            }
        });

    }

    public static Dialog showDialogSignature(){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_signature);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        final DrawingView drawingView = (DrawingView) dialogResult.findViewById(R.id.scratch_pad);
        drawingView.setPenSize(10);


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

                dialogResult.dismiss();

            }
        });

        return dialogResult;
    }

    public static void showDialogAllDebt(final String userName, List<BaseModel> list){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_all_debt);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        final RecyclerView rvDebts = (RecyclerView) dialogResult.findViewById(R.id.statistical_debt_rvbill);
        final TextView tvCount = dialogResult.findViewById(R.id.statistical_debt_count);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_all_debt_title);
        final CTextIcon tvSort = dialogResult.findViewById(R.id.statistical_debt_sort);

        tvTitle.setText(String.format("CÔNG NỢ %s", userName));

        List<BaseModel> listDebt = new ArrayList<>();

        Double debt  = 0.0;
        for (int i=0; i<list.size(); i++){
            User user = new User(list.get(i).getJsonObject("user"));

            if (userName.equals(Constants.ALL_FILTER)){
                listDebt.add(list.get(i));
                debt += list.get(i).getDouble("debt");

            }else if (userName.equals(user.getString("displayName"))){
                listDebt.add(list.get(i));
                debt += list.get(i).getDouble("debt");
            }
        }
        tvCount.setText(String.format("Tổng nợ: %s", Util.FormatMoney(debt)));

        final StatisticalDebtAdapter adapter = new StatisticalDebtAdapter(DataUtil.groupDebtByCustomer(listDebt), new CallbackString() {
            @Override
            public void Result(String s) {

                CustomerConnect.GetCustomerDetail(s, new CallbackJSONObject() {
                    @Override
                    public void onResponse(JSONObject result) {
                        Transaction.gotoCustomerActivity(result.toString(), false);

                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true);
            }
        });
        Util.createLinearRV(rvDebts, adapter);

        tvSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSort.getRotation() == 180){
                    tvSort.setRotation(0);

                }else {
                    tvSort.setRotation(180);
                }
                adapter.sortUp();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listDebt = new ArrayList<>();
                dialogResult.dismiss();
            }
        });

    }

}
