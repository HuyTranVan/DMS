package wolve.dms.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mukesh.DrawingView;

import java.util.ArrayList;
import java.util.List;


import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.R;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.PriceSuggestAdapter;
import wolve.dms.adapter.ProductReturnAdapter;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.DoubleTextWatcher;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Customer;
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

    public static void alertWithButtonCanceled(final String title, final String message, final String confirm, final boolean cancel, final CallbackBoolean callback) {
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
                dialog.setCanceledOnTouchOutside(cancel);
                dialog.setCancelable(cancel);
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
                                callback.onRespone(false);
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                callback.onRespone(true);
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

    public static void showDialogEditProduct(final BaseModel product, List<Double> listBillDetail, final CallbackClickProduct callbackClickProduct){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_edit_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_edit_product_title);
        EditText tvUnitPrice = dialogResult.findViewById(R.id.dialog_edit_product_unitprice);
        final EditText edNetPrice = dialogResult.findViewById(R.id.dialog_edit_product_netprice);
        final TextView tvTotal =  dialogResult.findViewById(R.id.dialog_edit_product_total);
        final EditText edDiscount =  dialogResult.findViewById(R.id.dialog_edit_product_discount);
        final EditText edQuantity = dialogResult.findViewById(R.id.dialog_edit_product_quantity);
        CTextIcon tvClear = dialogResult.findViewById(R.id.dialog_edit_product_netprice_clear);
        RecyclerView rvPrice = dialogResult.findViewById(R.id.dialog_edit_product_rvprice);

        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");
        tvTitle.setText(product.getString("name"));
        tvUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));

        edDiscount.setFocusable(false);
        edDiscount.setText(product.getDouble("discount") ==0 ? "" : Util.FormatMoney((double) Math.round(product.getDouble("discount"))));

        edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - product.getDouble("discount")));

        edQuantity.setText(String.valueOf(Math.round(product.getDouble("quantity"))));
        tvTotal.setText(Util.FormatMoney(product.getDouble("quantity") * (product.getDouble("unitPrice") - product.getDouble("discount"))));

        edDiscount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tvUnitPrice.getText().toString().trim().length() )});

        Util.showKeyboardEditTextDelay(edNetPrice);

        PriceSuggestAdapter adapter = new PriceSuggestAdapter(listBillDetail, new CallbackDouble() {
            @Override
            public void Result(Double d) {
                edNetPrice.setText(Util.FormatMoney(d));
            }
        });
        Util.createHorizontalRV(rvPrice, adapter);

        Util.textMoneyEvent(edNetPrice, product.getDouble("unitPrice"), new CallbackDouble() {
            @Override
            public void Result(Double d) {
                tvClear.setVisibility(Util.isEmpty(edNetPrice) || edNetPrice.getText().toString().equals("0")? View.GONE : View.VISIBLE);

                edDiscount.setText(Util.FormatMoney(product.getDouble("unitPrice") - Util.moneyValue(edNetPrice)));
                tvTotal.setText(Util.FormatMoney(d *  Util.valueMoney(edQuantity)));
                edNetPrice.setSelection(edNetPrice.getText().toString().length());

            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edNetPrice.setText("");
                tvTotal.setText("0");
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
                    BaseModel newProduct = product;
                    newProduct.put("quantity", Util.valueMoney(edQuantity));
                    newProduct.put("discount", edDiscount.getText().toString().equals("") ? 0 : Util.valueMoney(edDiscount));
                    newProduct.put("totalMoney", tvTotal.getText().toString().replace(".",""));

                    callbackClickProduct.ProductChoice(newProduct);
                    dialogResult.dismiss();


                }

            }
        });

//        Util.textMoneyEvent(edDiscount, product.getDouble("unitPrice"),new CallbackDouble() {
//            @Override
//            public void Result(Double text) {
//                edNetPrice.removeTextChangedListener();
//
//
//                if (!text.equals("")){
//                    if (text > product.getDouble("unitPrice")){
//                        Util.showToast("Vui lòng nhập giá tiền nhỏ hơn giá niêm yết");
//
//                    }else {
//                        if (text >0){
//                            edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - text));
//                            tvTotal.setText(Util.FormatMoney(Double.parseDouble(edQuantity.getText().toString()) * (product.getDouble("unitPrice") - text)));
//                        }
//                    }
//                }else {
//                    edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
//                    tvTotal.setText(Util.FormatMoney(Double.parseDouble(edQuantity.getText().toString()) * (product.getDouble("unitPrice"))));
//
//                }
//            }
//        });


//        edNetPrice.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                edNetPrice.setFocusable(true);
//                edNetPrice.setFocusableInTouchMode(true);
//                Util.showKeyboard(v);
//
//                edNetPrice.addTextChangedListener(new DoubleTextWatcher(edNetPrice, new CallbackString() {
//                    @Override
//                    public void Result(String text) {
//                        if (!text.toString().equals("") && !text.equals("0")){
//                            edDiscount.setText(Util.FormatMoney(product.getDouble("unitPrice") - Util.moneyValue(edNetPrice)));
//                            tvTotal.setText(Util.FormatMoney(Double.parseDouble(text) *  Util.valueMoney(edQuantity)));
//                            edNetPrice.setSelection(edNetPrice.getText().toString().length());
//                        }else if (text.toString().equals("")){
//                            tvTotal.setText("0");
//                        }
//                    }
//                }));
//
//                return true;
//            }
//        });



//        btnChangeToPromotion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BaseModel newProduct = product;
//                newProduct.put("quantity", Integer.parseInt(edQuantity.getText().toString()));
//                newProduct.put("discount", product.getDouble("unitPrice"));
//                newProduct.put("totalMoney", "0");
//
//                callbackClickProduct.ProductChoice(newProduct);
//                dialogResult.dismiss();
//
//
//            }
//        });
//
//        btnCopyToPromotion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Product newProduct = new Product();
//
//                newProduct.put("id", product.getString("id"));
//                newProduct.put("name", product.getString("name"));
//                newProduct.put("productGroup", product.getString("productGroup"));
//                newProduct.put("promotion", product.getBoolean("promotion"));
//                newProduct.put("unitPrice", product.getDouble("unitPrice"));
//                newProduct.put("purchasePrice", product.getDouble("purchasePrice"));
//                newProduct.put("volume", product.getLong("volume"));
//                newProduct.put("image", product.getString("image"));
//                newProduct.put("imageUrl", product.getString("imageUrl"));
//                newProduct.put("checked", product.getBoolean("checked"));
//                newProduct.put("isPromotion", product.getBoolean("isPromotion"));
//                newProduct.put("quantity", Integer.parseInt(edQuantity.getText().toString()));
//                newProduct.put("discount", product.getDouble("unitPrice"));
//                newProduct.put("totalMoney", "0");
//
//                callbackClickProduct.ProductAdded(newProduct);
//                dialogResult.dismiss();
//
//            }
//        });




    }

    public static void showDialogReturnProduct(List<BaseModel> listDebt, final BaseModel currentBill, final ProductReturnAdapter.CallbackReturn mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_return_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_return_product_title);
        RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_return_product_rv);
        final TextView tvSum = dialogResult.findViewById(R.id.dialog_return_product_sum);
        final TextView tvDebt = dialogResult.findViewById(R.id.dialog_return_product_debt);
        TextView tvNote = dialogResult.findViewById(R.id.dialog_return_product_note);

        double totalDebt = Util.getTotalDebt(listDebt);
        btnCancel.setText("HỦY");
        btnSubmit.setText("XÁC NHẬN");
        tvTitle.setText(String.format("TRẢ HÀNG HÓA ĐƠN %s", Util.DateString(currentBill.getLong("createAt"))));
        tvSum.setText("TẠM TÍNH TRẢ HÀNG:     0");
        tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt)));


        final ProductReturnAdapter adapter = new ProductReturnAdapter(currentBill, new CallbackDouble() {
            @Override
            public void Result(Double d) {
                tvSum.setText(String.format("TẠM TÍNH TRẢ HÀNG:     %s",Util.FormatMoney(d)));
                tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt - d)));

                if (d < 0.0){
                    dialogResult.dismiss();
                    Util.showToast("Hóa đơn đã trả hết hàng!");
                    tvNote.setVisibility(View.GONE);
                    tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt - d)));

                }else if (d <= totalDebt){
                    btnSubmit.setText("XÁC NHẬN");
                    tvNote.setVisibility(View.GONE);
                    tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt - d)));

                }else {
                    btnSubmit.setText("TIẾP TỤC");
                    tvNote.setVisibility(View.VISIBLE);
                    tvNote.setText(String.format("Tiền dư trả lại khách:     %s", Util.FormatMoney(d- totalDebt)));
                    tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(0.0)));

                }

            }
        });
        Util.createLinearRV(rvProduct, adapter);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                List<BaseModel> listProductSelected = new ArrayList<>(adapter.getListSelected());

                if (listProductSelected.size() >0){
                    if (adapter.sumReturnBill() > currentBill.getDouble("debt")){
                        dialogResult.dismiss();
                        mListener.returnMoreThanDebt(adapter.getListSelected(), adapter.sumReturnBill(), currentBill);

                    }else {
                        dialogResult.dismiss();
                        mListener.returnEqualLessDebt(adapter.getListSelected(), adapter.sumReturnBill(), currentBill);

                    }

                }else {
                    Util.showToast("Vui lòng chọn số lượng");

                }


            }
        });


    }

    public static void showReasonChoice(String title, String content, List<BaseModel> listStatus, final CallbackString mListener){
        //TYPE 0: NOT INTERESTED
        //TYPE 1: NORMAL

        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_status);
        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_choice_status_title);
        final TextView tvContent = dialogResult.findViewById(R.id.dialog_choice_status_text);
        final EditText edNote = dialogResult.findViewById(R.id.dialog_choice_status_content);
        final RecyclerView rvStatus = dialogResult.findViewById(R.id.dialog_choice_status_rvStatus);
        FrameLayout frParent = dialogResult.findViewById(R.id.dialog_choice_status_parent);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogResult.findViewById(R.id.btn_submit);

        dialogResult.setCanceledOnTouchOutside(true);

        btnCancel.setText("HỦY");
        btnConfirm.setText("HOÀN TẤT");
        tvTitle.setText(title);

        tvContent.setVisibility(View.GONE);

        edNote.setHint(content);


        List<BaseModel> listReason = CustomSQL.getListObject(Constants.STATUS);

        final CartCheckinReasonAdapter adapter = new CartCheckinReasonAdapter(listReason, new CartCheckinReasonAdapter.ReasonCallback() {
            @Override
            public void onResult(BaseModel result, int position) {
                listReason.get(position).put("rate", listReason.get(position).getInt("rate") + 1);
                mListener.Result(result.getString("content"));

                dialogResult.dismiss();

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
                    Util.showToast("Vui lòng nhập nội dung để tiếp tục");

                }else {
                    BaseModel baseModel = new BaseModel();
                    baseModel.put("type", 0);
                    baseModel.put("content",edNote.getText().toString().trim() );
                    baseModel.put("rate",1 );

                    listReason.add(baseModel);

                    CustomSQL.setListBaseModel(Constants.STATUS, listReason);

                    mListener.Result(edNote.getText().toString().trim() );

                    dialogResult.dismiss();
                }
            }
        });



    }

    public static Dialog showDialogPayment(String title, List<BaseModel> listDebts , double money, boolean changeAmount, final CallbackListCustom mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_input_paid);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_title);
        TextView tvText = dialogResult.findViewById(R.id.dialog_input_paid_text);
        //final TextView tvTotal = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total);
//        final TextView tvTotalTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_total_title);
        final EditText edPaid = (EditText) dialogResult.findViewById(R.id.dialog_input_paid_money);
        final TextView tvRemain = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_remain);
        final RecyclerView rvDebt = dialogResult.findViewById(R.id.dialog_input_paid_rvdebt);
        final Switch swFastPay = dialogResult.findViewById(R.id.dialog_input_paid_switch);
//        final TextView tvCurrentBillPaid = dialogResult.findViewById(R.id.dialog_input_paid_paid);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        dialogResult.setCanceledOnTouchOutside(true);
        final DebtAdapter debtAdapter = new DebtAdapter(listDebts == null? new ArrayList<BaseModel>() : listDebts, swFastPay.isChecked(), true);
        Util.createLinearRV(rvDebt, debtAdapter);

        final Double totalDebt = debtAdapter.getTotalMoney();

        tvTitle.setText(title);
        tvRemain.setText(Util.FormatMoney(totalDebt));
        tvText.setText(totalDebt >= 0 ? "Số tiền khách trả": "Số tiền trả lại khách");

        swFastPay.setVisibility(listDebts.size() ==1 ?View.GONE: View.VISIBLE);

        if (changeAmount){
            edPaid.setFocusable(true);
            edPaid.setFocusableInTouchMode(true);
            Util.showKeyboardDelay(edPaid);

        }else {
            edPaid.setFocusable(false);
        }

        edPaid.setText(money == 0.0? "" : Util.FormatMoney(money));

        Util.textMoneyEvent(edPaid,totalDebt, new CallbackDouble() {
            @Override
            public void Result(Double s) {
                debtAdapter.inputPaid(s, swFastPay.isChecked());
                tvRemain.setText(Util.FormatMoney(totalDebt - s));
            }

        });

        swFastPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                debtAdapter.inputPaid(Util.valueMoney(edPaid), swFastPay.isChecked());

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
                List<BaseModel> listPayment = debtAdapter.getListBillPayment();
                mListener.onResponse(listPayment);
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


        tvShopName.setText(Constants.getShopName(customer.getString("shopType")) + " "+ customer.getString("signBoard"));
        tvCustomerName.setText(customer.getString("name") == null? "" : customer.getString("name"));
        tvPhone.setText(customer.getString("phone") == null? "" : customer.getString("phone"));
        tvAddress.setText((customer.getString("address") == null? "" : customer.getString("address")) +" " +customer.getString("street") + " "+ customer.getString("district"));
        tvDate.setText(Util.CurrentMonthYearHour());
        tvSalesman.setText(User.getFullName());
        tvTotal.setText(total);


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

    public static void showDialogRelogin(String title, final BaseModel user , final Callback mlistener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_relogin);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_relogin_title);
        final EditText edPhone =  dialogResult.findViewById(R.id.dialog_relogin_phone);
        final EditText edPass =  dialogResult.findViewById(R.id.dialog_relogin_password);

        btnCancel.setText("HỦY");
        btnSubmit.setText("ĐĂNG NHẬP");
        tvTitle.setText(title);

        edPhone.setText(user.getString("phone"));

        Util.showKeyboardDelay(user.getString("phone").equals("")? edPhone : edPass);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Util.hideKeyboard(edPass);
                if (!edPhone.getText().toString().equals("") && !edPass.getText().toString().equals("")){
                    UserConnect.doLogin(edPhone.getText().toString().trim(),
                            edPass.getText().toString().trim(),
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result){
                                        Util.showToast("Đăng nhập thành công");
                                        mlistener.onResponse(user.BaseModelJSONObject());
                                        //Transaction.gotoHomeActivity(true);
                                    }else {
                                        mlistener.onError("");
                                    }
                                }
                            });
                }

                dialogResult.dismiss();


            }
        });


    }

    public static void showDialogChangePass(String title, final CallbackBoolean mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_change_password);

        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_changepass_title);
        final EditText edOldPass = (EditText) dialogResult.findViewById(R.id.dialog_changepass_old);
        final EditText edNewPass1 = (EditText) dialogResult.findViewById(R.id.dialog_changepass_new1);
        final EditText edNewPass2 = (EditText) dialogResult.findViewById(R.id.dialog_changepass_new2);

        btnCancel.setText("HỦY");
        btnSubmit.setText("TIẾP TỤC");
        tvTitle.setText(title);

        Util.showKeyboardDelay(edOldPass);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Util.hideKeyboard(edOldPass);

                if (!Util.isEmpty(edOldPass) && !Util.isEmpty(edNewPass1) && !Util.isEmpty(edNewPass2)){

                    if (edOldPass.getText().toString().trim().equals(CustomSQL.getString(Constants.USER_PASSWORD))){

                        if (edNewPass1.getText().toString().trim().equals(edNewPass2.getText().toString().trim())) {
                            dialogResult.dismiss();
                            UserConnect.doChangePass(edNewPass1.getText().toString().trim(), new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    mListener.onRespone(result);
                                }
                            });

                        }else {
                            Util.showToast("Mật khẩu mới không khớp");

                        }
                    }else {
                        Util.showToast("Nhập sai mật khẩu cũ");
                    }

                }else {
                    Util.showToast("Nhập chưa đủ nội dung");
                }




            }
        });


    }

    public static void showDialogEditAddress(BaseModel address, CallbackBaseModel listener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_edit_address);

        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_editadd_title);
        CInputForm edAddress = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_address);
        CInputForm edStreet = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_street);
        CInputForm edDistrict = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_district);
        CInputForm edCity = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_city);

        tvTitle.setText("SỬA ĐỊA CHỈ");
        edAddress.setText(address.getString("address"));
        edStreet.setText(address.getString("street"));
        edDistrict.setText(address.getString("district"));
        edCity.setText(address.getString("province"));


        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");

        edAddress.setSelection();
        Util.showKeyboardDelay(edAddress);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseModel objectAdress = new BaseModel();

                objectAdress.put("address", edAddress.getText().toString());
                objectAdress.put("street", edStreet.getText().toString());
                objectAdress.put("district",edDistrict.getText().toString());
                objectAdress.put("province", edCity.getText().toString());

                listener.onResponse(objectAdress);
                dialogResult.dismiss();

            }
        });


    }


}
