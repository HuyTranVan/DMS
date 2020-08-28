package wolve.dms.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.whinc.widget.ratingbar.RatingBar;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.R;
import wolve.dms.activities.BaseActivity;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.ListUserChangeAdapter;
import wolve.dms.adapter.PriceSuggestAdapter;
import wolve.dms.adapter.ProductCompareInventoryAdapter;
import wolve.dms.adapter.ProductQuantityAdapter;
import wolve.dms.adapter.ProductSelectAdapter;
import wolve.dms.adapter.WaitingListAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.libraries.DoubleTextWatcher;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.User;

import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/28/17.
 */

public class CustomCenterDialog {

    public interface ButtonCallback {
        void Submit(Boolean boolSubmit);

        void Cancel(Boolean boolCancel);
    }

    public interface CallbackRangeTime {
        void onSelected(long start, long end);
    }

    public static Dialog showCustomDialog(int resId) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Util.getInstance().getCurrentActivity());
        final Dialog d = adb.setView(new View(Util.getInstance().getCurrentActivity())).create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.bg_corner5_white);

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

    public static void showDialogEditProduct(final BaseModel product, List<BaseModel> listBillDetail, final CallbackClickProduct callbackClickProduct) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_edit_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_edit_product_title);
        EditText tvUnitPrice = dialogResult.findViewById(R.id.dialog_edit_product_unitprice);
        final EditText edNetPrice = dialogResult.findViewById(R.id.dialog_edit_product_netprice);
        final TextView tvTotal = dialogResult.findViewById(R.id.dialog_edit_product_total);
        final EditText edDiscount = dialogResult.findViewById(R.id.dialog_edit_product_discount);
        final EditText edQuantity = dialogResult.findViewById(R.id.dialog_edit_product_quantity);
        TextView tvClear = dialogResult.findViewById(R.id.dialog_edit_product_netprice_clear);
        RecyclerView rvPrice = dialogResult.findViewById(R.id.dialog_edit_product_rvprice);

        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");
        tvTitle.setText(product.getString("name"));
        tvUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));

        edDiscount.setFocusable(false);
        edDiscount.setText(product.getDouble("discount") == 0 ? "" : Util.FormatMoney((double) Math.round(product.getDouble("discount"))));

        edNetPrice.setText(Util.FormatMoney(product.getDouble("unitPrice") - product.getDouble("discount")));

        edQuantity.setText(String.valueOf(Math.round(product.getDouble("quantity"))));
        tvTotal.setText(Util.FormatMoney(product.getDouble("quantity") * (product.getDouble("unitPrice") - product.getDouble("discount"))));

        edDiscount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tvUnitPrice.getText().toString().trim().length())});

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
                tvClear.setVisibility(Util.isEmpty(edNetPrice) || edNetPrice.getText().toString().equals("0") ? View.GONE : View.VISIBLE);

                edDiscount.setText(Util.FormatMoney(product.getDouble("unitPrice") - Util.moneyValue(edNetPrice)));
                tvTotal.setText(Util.FormatMoney(d * Util.valueMoney(edQuantity)));
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
                if (!text.toString().equals("") && !text.equals("0")) {
                    tvTotal.setText(Util.FormatMoney(Double.parseDouble(text) * (product.getDouble("unitPrice") - Util.valueMoney(edDiscount))));

                } else if (text.toString().equals("")) {
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
            @Override
            public void onClick(View v) {
                if (Util.isEmptyValue(edQuantity) || Util.isEmptyValue(edNetPrice)) {
                    Util.showToast("Vui lòng nhập số lượng >0 ");

                } else if (Util.isEmptyValue(edNetPrice)) {
                    Util.showToast("Vui lòng nhập giá tiền  >0 ");

                } else if (Util.moneyValue(edNetPrice) > product.getDouble("unitPrice")) {
                    Util.showToast("Vui lòng nhập giá tiền nhỏ hơn giá đơn vị ");

                } else {
                    BaseModel newProduct = product;
                    newProduct.put("quantity", Util.valueMoney(edQuantity));
                    newProduct.put("discount", edDiscount.getText().toString().equals("") ? 0 : Util.valueMoney(edDiscount));
                    newProduct.put("totalMoney", tvTotal.getText().toString().replace(".", ""));

                    callbackClickProduct.ProductChoice(newProduct);
                    dialogResult.dismiss();


                }

            }
        });

    }

    public static void showReasonChoice(String title,
                                        String hint,
                                        String text,
                                        String cancel,
                                        String submit,
                                        boolean touchOutside,
                                        boolean showListReason,
                                        final CallbackString mListener) {
        //TYPE 0: NOT INTERESTED
        //TYPE 1: NORMAL

        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_status);
        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_choice_status_title);
        final TextView tvContent = dialogResult.findViewById(R.id.dialog_choice_status_text);
        final EditText edNote = dialogResult.findViewById(R.id.dialog_choice_status_content);
        TextView tvClose = dialogResult.findViewById(R.id.dialog_select_status_clear);
        final RecyclerView rvStatus = dialogResult.findViewById(R.id.dialog_choice_status_rvStatus);
        FrameLayout frParent = dialogResult.findViewById(R.id.dialog_choice_status_parent);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogResult.findViewById(R.id.btn_submit);

        dialogResult.setCanceledOnTouchOutside(touchOutside);

        btnCancel.setText("HỦY");
        btnConfirm.setText("HOÀN TẤT");
        tvTitle.setText(title);

        tvContent.setVisibility(View.GONE);
        Util.showKeyboardDelay(edNote);

        edNote.setHint(hint);
        edNote.setText(text);
        edNote.setSelection(text.length());

        List<BaseModel> listReason = CustomSQL.getListObject(Constants.STATUS);
        if (showListReason) {
            final CartCheckinReasonAdapter adapter = new CartCheckinReasonAdapter(listReason, new CartCheckinReasonAdapter.ReasonCallback() {
                @Override
                public void onResult(BaseModel result, int position) {
                    listReason.get(position).put("rate", listReason.get(position).getInt("rate") + 1);
                    mListener.Result(result.getString("content"));

                    dialogResult.dismiss();

                }

            });

            Util.createLinearRV(rvStatus, adapter);
        }

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edNote.setText("");
            }
        });
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
                if (Util.isEmpty(edNote)) {
//                    Util.showToast("Vui lòng nhập nội dung để tiếp tục");
                    mListener.Result(edNote.getText().toString().trim());
                    dialogResult.dismiss();

                } else {
                    if (showListReason) {
                        BaseModel baseModel = new BaseModel();
                        baseModel.put("type", 0);
                        baseModel.put("content", edNote.getText().toString().trim());
                        baseModel.put("rate", 1);

                        listReason.add(baseModel);

                        CustomSQL.setListBaseModel(Constants.STATUS, listReason);
                    }

                    mListener.Result(edNote.getText().toString().trim());
                    dialogResult.dismiss();
                }
            }
        });


    }

    public static void showCheckinDialog(String title, int customer_id, int status_id, int currentRating, CallbackObject listener) {
        //status_id 0: NEW
        //status_id 1: INTERESTED
        //status_id 2: NOT INTERESTED
        //status_id 3: ALREADY BUY

        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_checkin);
        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_choice_status_title);
        TextView tvStatus = dialogResult.findViewById(R.id.dialog_checkin_status);
        AutoCompleteTextView edProduct = dialogResult.findViewById(R.id.dialog_checkin_search);
        final EditText edNote = dialogResult.findViewById(R.id.dialog_checkin_text);
        EditText edNextDay = dialogResult.findViewById(R.id.dialog_checkin_nextday);
        TextView tvNoteClear = dialogResult.findViewById(R.id.dialog_select_status_clear);
        TextView tvSearchClear = dialogResult.findViewById(R.id.dialog_checkin_search_close);
        final RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_checkin_rvproduct);
        RatingBar ratingBar = dialogResult.findViewById(R.id.dialog_checkin_ratingBar);
        RadioGroup radioGroup = dialogResult.findViewById(R.id.dialog_checkin_rg);
        RadioButton radioNotInterest = dialogResult.findViewById(R.id.dialog_checkin_notinterest);
        RadioButton radioInterest = dialogResult.findViewById(R.id.dialog_checkin_interest);
        FrameLayout frParent = dialogResult.findViewById(R.id.dialog_choice_status_parent);
        LinearLayout lnProductParent = dialogResult.findViewById(R.id.dialog_checkin_product_parent);
        LinearLayout lnNextVisit = dialogResult.findViewById(R.id.dialog_checkin_nextvisit_parent);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogResult.findViewById(R.id.btn_submit);

        dialogResult.setCanceledOnTouchOutside(false);

        btnCancel.setText("QUAY LẠI");
        btnConfirm.setText("TIẾP TỤC");
        tvTitle.setText(title);
        ratingBar.setCount(currentRating);

        ProductSelectAdapter selectAdapter = new ProductSelectAdapter(new ArrayList<>());
        Util.createGridRV(rvProduct, selectAdapter, 2);

        List<String> products = Product.getProductListString();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), android.R.layout.select_dialog_item, products);
        edProduct.setThreshold(1);//will start working from first character
        edProduct.setAdapter(adapter);//set
        edProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectAdapter.addItem(edProduct.getText().toString());
                edProduct.setText("");
            }
        });
        switch (status_id) {
            case 0:
                radioNotInterest.setVisibility(View.VISIBLE);
                radioInterest.setVisibility(View.GONE);
                lnProductParent.setVisibility(View.VISIBLE);
                tvStatus.setBackground(Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.bg_round_white_border_pink));
                tvStatus.setTextColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.colorPink));
                tvStatus.setText("Khách hàng mới");

                break;
            case 1:
                radioNotInterest.setVisibility(View.VISIBLE);
                radioInterest.setVisibility(View.GONE);
                lnProductParent.setVisibility(View.VISIBLE);
                tvStatus.setBackground(Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.bg_round_white_border_orange));
                tvStatus.setTextColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.orange));
                tvStatus.setText("Khách hàng có thiện chí");

                break;
            case 2:
                radioNotInterest.setVisibility(View.GONE);
                radioInterest.setVisibility(View.VISIBLE);
                lnProductParent.setVisibility(View.VISIBLE);
                tvStatus.setBackground(Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.bg_round_white_border_grey));
                tvStatus.setTextColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.black_text_color_hint));
                tvStatus.setText("Khách hàng không quan tâm");

                break;
            case 3:
                radioNotInterest.setVisibility(View.GONE);
                radioInterest.setVisibility(View.GONE);
                lnProductParent.setVisibility(View.GONE);
                tvStatus.setBackground(Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.bg_round_white_border_blue));
                tvStatus.setTextColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.colorBlue));
                tvStatus.setText("Khách đã mua hàng");

                break;

        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.dialog_checkin_owner:
                        edNote.setHint("Nhập ghi chú khách hàng");
                        lnProductParent.setVisibility(status_id != 3 ? View.VISIBLE : View.GONE);
                        lnNextVisit.setVisibility(View.VISIBLE);
                        ratingBar.setClickRating(true);
                        ratingBar.setTouchRating(true);

                        break;
                    case R.id.dialog_checkin_notowner:
                        edNote.setHint("Nhập ghi chú khách hàng");
                        lnProductParent.setVisibility(View.GONE);
                        lnNextVisit.setVisibility(View.VISIBLE);
                        ratingBar.setClickRating(true);
                        ratingBar.setTouchRating(true);

                        break;
                    case R.id.dialog_checkin_notinterest:
                        edNote.setHint("Nhập lý do không quan tâm");
                        lnProductParent.setVisibility(View.GONE);
                        lnNextVisit.setVisibility(View.GONE);
                        ratingBar.setCount(1);
                        ratingBar.setClickRating(false);
                        ratingBar.setTouchRating(false);

                        break;

                }
            }
        });

        Util.textEvent(edNote, new CallbackString() {
            @Override
            public void Result(String s) {
                tvNoteClear.setVisibility(s.equals("") ? View.GONE : View.VISIBLE);

            }
        });
        tvNoteClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edNote.setText("");
            }
        });
        Util.textEvent(edProduct, new CallbackString() {
            @Override
            public void Result(String s) {
                tvSearchClear.setVisibility(s.equals("") ? View.GONE : View.VISIBLE);
            }
        });
        tvSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edProduct.setText("");
            }
        });
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
                String param;
                BaseModel object = new BaseModel();
                if (Util.isEmpty(edNextDay)) {
                    Util.showSnackbar("Nhập ngày ghé cửa hàng tiếp theo", null, null);

                } else if (radioGroup.getCheckedRadioButtonId() == R.id.dialog_checkin_notinterest) {
                    if (Util.isEmpty(edNote)) {
                        Util.showSnackbar("Nhập lý do khách hàng không quan tâm để tiếp tục", null, null);

                    } else {
                        param = String.format(ApiUtil.SCHECKIN_CREATE_PARAM,
                                customer_id,
                                ratingBar.getCount(),
                                Util.encodeString("Chuyển sang không quan tâm vì: " + edNote.getText().toString()),
                                User.getId(),
                                Util.CurrentTimeStamp() + 365 * 86400000,
                                1);

                        object.put("updateStatus", true);
                        object.put("status", 2);
                        object.put("param", param);
                        listener.onResponse(object);
                        dialogResult.dismiss();

                    }

                } else if (radioGroup.getCheckedRadioButtonId() == R.id.dialog_checkin_interest) {
                    if (Util.isEmpty(edNote)) {
                        Util.showSnackbar("Nhập lý do khách hàng quan tâm để tiếp tục", null, null);

                    } else if (selectAdapter.getData().size() < 1) {
                        Util.showSnackbar("Chọn sản phẩm giới thiệu để tiếp tục", null, null);

                    } else {
                        param = String.format(ApiUtil.SCHECKIN_CREATE_PARAM,
                                customer_id,
                                ratingBar.getCount(),
                                Util.encodeString(DataUtil.createCheckinNote(selectAdapter.getData(), "Chuyển sang quan tâm vì: " + edNote.getText().toString())),
                                User.getId(),
                                Util.CurrentTimeStamp() + Integer.parseInt(edNextDay.getText().toString()) * 86400000,
                                1);

                        object.put("updateStatus", true);
                        object.put("status", 1);
                        object.put("param", param);
                        listener.onResponse(object);
                        dialogResult.dismiss();

                    }

                } else if (radioGroup.getCheckedRadioButtonId() == R.id.dialog_checkin_owner) {
                    if (Util.isEmpty(edNote)) {
                        Util.showSnackbar("Nhập nội dung cuộc gặp để tiếp tục", null, null);

                    } else if (selectAdapter.getData().size() < 1 && lnProductParent.getVisibility() == View.VISIBLE) {
                        Util.showSnackbar("Chọn sản phẩm giới thiệu để tiếp tục", null, null);

                    } else {
                        param = String.format(ApiUtil.SCHECKIN_CREATE_PARAM,
                                customer_id,
                                ratingBar.getCount(),
                                Util.encodeString(DataUtil.createCheckinNote(selectAdapter.getData(), edNote.getText().toString())),
                                User.getId(),
                                Util.CurrentTimeStamp() + Integer.parseInt(edNextDay.getText().toString()) * 86400000,
                                1);

                        if (status_id == 0) {
                            object.put("updateStatus", true);
                            object.put("status", 1);
                        } else {
                            object.put("updateStatus", false);
                        }

                        object.put("param", param);
                        listener.onResponse(object);
                        dialogResult.dismiss();

                    }

                } else if (radioGroup.getCheckedRadioButtonId() == R.id.dialog_checkin_notowner) {
                    param = String.format(ApiUtil.SCHECKIN_CREATE_PARAM,
                            customer_id,
                            ratingBar.getCount(),
                            Util.encodeString(DataUtil.createCheckinNote(selectAdapter.getData(), edNote.getText().toString())),
                            User.getId(),
                            Util.CurrentTimeStamp() + Integer.parseInt(edNextDay.getText().toString()) * 86400000,
                            0);

                    object.put("updateStatus", false);
                    object.put("param", param);
                    listener.onResponse(object);
                    dialogResult.dismiss();

                }

            }
        });

    }

    public static Dialog showDialogPayment(String title, List<BaseModel> listDebts, double money, boolean changeAmount, final CallbackListCustom mListener) {
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
        final DebtAdapter debtAdapter = new DebtAdapter(listDebts == null ? new ArrayList<BaseModel>() : listDebts, swFastPay.isChecked(), true);
        Util.createLinearRV(rvDebt, debtAdapter);

        final Double totalDebt = debtAdapter.getTotalMoney();

        tvTitle.setText(title);
        tvRemain.setText(Util.FormatMoney(totalDebt));
        tvText.setText(totalDebt >= 0 ? "Số tiền khách trả" : "Số tiền trả lại khách");

        btnSubmit.setText("tiếp tục");
        swFastPay.setVisibility(listDebts.size() == 1 ? View.GONE : View.VISIBLE);

        if (changeAmount) {
            edPaid.setFocusable(true);
            edPaid.setFocusableInTouchMode(true);
            Util.showKeyboardDelay(edPaid);

        } else {
            edPaid.setFocusable(false);
        }

        edPaid.setText(money == 0.0 ? "" : Util.FormatMoney(money));

        Util.textMoneyEvent(edPaid, totalDebt, new CallbackDouble() {
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

    public static void showDialogDiscountPayment(String title, String text, Double limitMoney, final CallbackDouble mListener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_input_paid_discount);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_input_paid_discount_title);
        TextView tvText = dialogResult.findViewById(R.id.dialog_input_paid_discount_text);
        final EditText edPaid = (EditText) dialogResult.findViewById(R.id.dialog_input_paid_discount_money);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        dialogResult.setCanceledOnTouchOutside(true);
        tvTitle.setText(title);
        tvText.setText(text);

        Util.showKeyboardDelay(edPaid);
        Util.textMoneyEvent(edPaid, limitMoney, new CallbackDouble() {
            @Override
            public void Result(Double s) {

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
                if (Util.isEmpty(edPaid)) {
                    Util.showToast("Vui lòng nhập số tiền >0");

                } else {
                    Util.hideKeyboard(v);
                    mListener.Result(Util.moneyValue(edPaid));
                    dialogResult.dismiss();
                }


            }
        });


    }

    public static void showDialogInputQuantity(String title, String quantity,
                                               int currentQuantity,
                                               boolean emptyQuantityValue,
                                               final CallbackString mListener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_input_quantity);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.input_number_title);
        final EditText edQuantity = (EditText) dialogResult.findViewById(R.id.input_number_quantity);
        TextInputLayout currentQuantityParent = dialogResult.findViewById(R.id.input_number_current_quantity_parent);
        final EditText edCurrentQuantity = (EditText) dialogResult.findViewById(R.id.input_number_current_quantity);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        dialogResult.setCanceledOnTouchOutside(true);
        tvTitle.setText(title);
        currentQuantityParent.setVisibility(currentQuantity > 0 ? View.VISIBLE : View.GONE);
        edCurrentQuantity.setText(String.valueOf(currentQuantity));
        edQuantity.setText(quantity);
        btnSubmit.setText("đồng ý");
        btnCancel.setText("hủy");

        Util.showKeyboardEditTextDelay(edQuantity);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isEmpty(edQuantity) || edQuantity.getText().toString().equals("0")) {
                    if (emptyQuantityValue) {
                        Util.hideKeyboard(v);
                        mListener.Result("0");
                        dialogResult.dismiss();

                    } else {
                        Util.showToast("Vui lòng nhập số lượng >0");
                    }


                } else {
                    Util.hideKeyboard(v);
                    mListener.Result(edQuantity.getText().toString());
                    dialogResult.dismiss();
                }


            }
        });


    }


    public static void showListProduct(String title, List<BaseModel> listProduct) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_list_product);

        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_list_product_title);
        final RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_list_product_rv);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);

        dialogResult.setCanceledOnTouchOutside(true);

        btnCancel.setText("QUAY LẠI");
        tvTitle.setText(title);

        final ProductQuantityAdapter adapter = new ProductQuantityAdapter(listProduct);
        Util.createLinearRV(rvProduct, adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

    }

    public static void showListProductWithDifferenceQuantity(String title, List<BaseModel> listProduct, CallbackBoolean listener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_list);

        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_list_title);
        final RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_list_rv);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        dialogResult.setCanceledOnTouchOutside(true);
        btnCancel.setText("QUAY LẠI");
        btnSubmit.setText("NHẬP KHO");
        tvTitle.setText(title);

        final ProductCompareInventoryAdapter adapter = new ProductCompareInventoryAdapter(listProduct);
        Util.createLinearRV(rvProduct, adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRespone(true);
                dialogResult.dismiss();
            }
        });

    }

    public static void showDialogRelogin(String title, final BaseModel user, final CallbackBoolean mlistener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_relogin);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_relogin_title);
        final EditText edPhone = dialogResult.findViewById(R.id.dialog_relogin_phone);
        final EditText edPass = dialogResult.findViewById(R.id.dialog_relogin_password);

        btnCancel.setText("HỦY");
        btnSubmit.setText("ĐĂNG NHẬP");
        tvTitle.setText(title);

        edPhone.setText(user == null ? "" : user.getString("phone"));
        Util.showKeyboardDelay(user == null || user.getString("phone").equals("") ? edPhone : edPass);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(edPass);

                if (!edPhone.getText().toString().equals("") && !edPass.getText().toString().equals("")) {
                    dialogResult.dismiss();
                    String fcm_token = User.getFCMToken();
                    BaseActivity activity = (BaseActivity) Util.getInstance().getCurrentActivity();
                    activity.logout(new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                activity.login(edPhone.getText().toString().trim(),
                                        edPass.getText().toString().trim(),
                                        fcm_token,
                                        new CallbackBoolean() {
                                            @Override
                                            public void onRespone(Boolean result) {
                                                if (result){
                                                    if (result) {
                                                        CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, true);
                                                        Util.showToast("Đăng nhập thành công");
                                                        mlistener.onRespone(true);

                                                    } else {
                                                        mlistener.onRespone(false);
                                                    }

                                                }
                                            }
                                        });

                            }
                        }
                    });
//                    UserConnect.Logout(new CallbackCustom() {
//                        @Override
//                        public void onResponse(BaseModel result) {
//                            if (result.getBoolean("success")) {
//
//                                UserConnect.doLogin(edPhone.getText().toString().trim(),
//                                        edPass.getText().toString().trim(),
//                                        fcm_token,
//                                        new CallbackBoolean() {
//                                            @Override
//                                            public void onRespone(Boolean result) {
//                                                if (result) {
//                                                    CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, true);
//                                                    Util.showToast("Đăng nhập thành công");
//                                                    mlistener.onRespone(true);
//
//                                                } else {
//                                                    mlistener.onRespone(false);
//                                                }
//                                            }
//                                        });
//
//                            }
//                        }
//
//                        @Override
//                        public void onError(String error) {
//
//                        }
//                    }, true);

                } else {
                    Util.showToast("Nhập đầy đủ username và password");
                }
            }
        });


    }

    public static void showDialogChangePass(String title, final CallbackBoolean mListener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_change_password);

        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_changepass_title);
        final EditText edOldPass = (EditText) dialogResult.findViewById(R.id.dialog_changepass_old);
        final EditText edNewPass1 = (EditText) dialogResult.findViewById(R.id.dialog_changepass_new1);
        final EditText edNewPass2 = (EditText) dialogResult.findViewById(R.id.dialog_changepass_new2);

        btnCancel.setText("QUAY LẠI");
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
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(edOldPass);
                if (!Util.isEmpty(edOldPass) && !Util.isEmpty(edNewPass1) && !Util.isEmpty(edNewPass2)) {
                    if (edNewPass1.getText().toString().trim().equals(edNewPass2.getText().toString().trim())) {
                        BaseModel param = createPostParam(ApiUtil.USER_CHANGE_PASS(),
                                String.format(ApiUtil.USER_CHANGE_PASS_PARAM, User.getId(),
                                        edOldPass.getText().toString().trim(),
                                        edNewPass1.getText().toString().trim()),
                                false,
                                false);
                        new GetPostMethod(param, new NewCallbackCustom() {
                            @Override
                            public void onResponse(BaseModel result, List<BaseModel> list) {
                                mListener.onRespone(true);
                                dialogResult.dismiss();
                            }

                            @Override
                            public void onError(String error) {
                                mListener.onRespone(false);
                            }
                        }, true).execute();
//                        UserConnect.doChangePass(edOldPass.getText().toString().trim(),
//                                edNewPass1.getText().toString().trim(),
//                                new CallbackCustom() {
//                                    @Override
//                                    public void onResponse(BaseModel result) {
//                                        mListener.onRespone(true);
//                                        dialogResult.dismiss();
//                                    }
//
//                                    @Override
//                                    public void onError(String error) {
//                                        mListener.onRespone(false);
//                                    }
//
//                                }, true);

                    } else {
                        Util.showToast("Mật khẩu mới không khớp");

                    }

                } else {
                    Util.showToast("Nhập chưa đủ nội dung");
                }


            }
        });


    }

    public static void showWaitingList(String title, List<BaseModel> listCustomer, CallbackBoolean listener, CallbackInt listenerCount) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_list);

        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_list_title);
        final RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_list_rv);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);

        dialogResult.setCanceledOnTouchOutside(true);
        btnCancel.setText("QUAY LẠI");
        btnSubmit.setText("VẼ ĐƯỜNG");
        tvTitle.setText(title);

        final WaitingListAdapter adapter = new WaitingListAdapter(listCustomer, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                listenerCount.onResponse(value);
                if (value == 0) {
                    dialogResult.dismiss();
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
            @Override
            public void onClick(View view) {
                listener.onRespone(true);
                dialogResult.dismiss();
            }
        });

    }

    public static void dialogChangeUser(String title, List<BaseModel> users, CallbackObject mListener) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_list_user);
        final TextView tvTitle = dialogResult.findViewById(R.id.dialog_list_user_title);
        final RecyclerView rvUser = dialogResult.findViewById(R.id.dialog_list_user_rv);
        Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);

        btnCancel.setText("QUAY LẠI");
        tvTitle.setText(title);
        dialogResult.setCanceledOnTouchOutside(true);

        ListUserChangeAdapter adapter = new ListUserChangeAdapter(users, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                dialogResult.dismiss();
                mListener.onResponse(object);
            }
        });
        Util.createLinearRV(rvUser, adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

    }


}
