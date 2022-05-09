package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.ProductReturnAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostListMethod;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerReturnFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private View view;
    private RecyclerView rvReturn, rvDebt;
    private TextView tvTitle, tvSum, tvCashReturn;
    private Switch swPayDebt;
    private Button btnSubmit;
    LinearLayout lnOption;

    private ProductReturnAdapter adapterReturn;
    private DebtAdapter adapterDebt;
    private CustomerActivity mActivity;
    private String sum = "TẠM TÍNH TRẢ HÀNG:     %s đ";
    private String cash = "TRẢ LẠI KHÁCH TIỀN MẶT:     %s đ";
    private double totalReturn = 0.0;
    private List<BaseModel> listDebt = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_return, container, false);

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        tvTitle.setText(String.format("Trả hàng hóa đơn %s", Util.DateHourString(mActivity.currentBill.getLong("createAt"))));
        tvSum.setText(String.format(sum, "0"));
        listDebt = remakeDebtBill(mActivity.listDebtBill, mActivity.currentBill);

        createRVReturn(mActivity.currentBill);
        createRVDebt(listDebt);


    }

    private void addEvent() {
        swPayDebt.setOnCheckedChangeListener(this);
        btnSubmit.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        rvReturn = (RecyclerView) view.findViewById(R.id.customer_return_rvreturn);
        rvDebt = (RecyclerView) view.findViewById(R.id.customer_return_rvdebt);
        tvTitle = view.findViewById(R.id.customer_return_title);
        tvSum = view.findViewById(R.id.customer_return_sum);
        swPayDebt = view.findViewById(R.id.customer_return_paydebt);
        btnSubmit = view.findViewById(R.id.customer_return_submit);
        lnOption = view.findViewById(R.id.customer_return_option);
        tvCashReturn = view.findViewById(R.id.customer_return_cashreturn);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customer_return_submit:
                submitEvent();
                break;


        }
    }

    private void finish() {
        mActivity.reloadCustomer(mActivity.currentCustomer.getString("id"));
        mActivity.getSupportFragmentManager().popBackStack();


    }

    private void submitEvent() {
        //listselected >0
        if (swPayDebt.isChecked()) {
            if (totalReturn > 0.0) {
                if (totalReturn <= mActivity.currentDebt) {
                    postBillReturnUpdatePayment(adapterReturn.getListSelected(), totalReturn, mActivity.currentBill);

                } else {
                    postBillReturnUpdatePaymentAndCash(adapterReturn.getListSelected(), adapterDebt.getListBillPayment(), totalReturn, mActivity.currentBill);
                }

            } else {
                onlyPostUpdateBillReturn(adapterReturn.getListSelected(), 0.0, mActivity.currentBill);
            }

        }

//        else {
//
//            //extent option not complete
//            if (totalReturn > 0.0 ){
//                List<BaseModel> listP = new ArrayList<>();
//
//                BaseModel object = new BaseModel();
//                object.put("billId", mActivity.currentBill.getInt("id"));
//                object.put("paid", totalReturn);
//                listP.add(object);
//
//                postBillReturnUpdatePaymentAndCash(adapterReturn.getListSelected(),listP, totalReturn, mActivity.currentBill);
//
//            }else {
//                onlyPostUpdateBillReturn(adapterReturn.getListSelected(), 0.0, mActivity.currentBill);
//
//            }

//        }


    }

    private void createRVReturn(BaseModel bill) {
        adapterReturn = new ProductReturnAdapter(bill, new CallbackDouble() {
            @Override
            public void Result(Double d) {
                if (d < 0.0) {
                    tvSum.setText("HÓA ĐƠN ĐÃ TRẢ HẾT HÀNG");

                } else {
                    totalReturn = d;
                    tvSum.setText(String.format(sum, Util.FormatMoney(d)));

                    if (adapterReturn.getListSelected().size() > 0) {
                        btnSubmit.setVisibility(View.VISIBLE);
                        if (d > 0.0) {
                            rvDebt.setVisibility(View.VISIBLE);
//                            lnOption.setVisibility(View.VISIBLE);
                            lnOption.setVisibility(View.GONE);
                            if (swPayDebt.isChecked()) {
                                rvDebt.setVisibility(View.VISIBLE);
                                tvCashReturn.setVisibility(View.GONE);
                                adapterDebt.inputPaid(totalReturn, true);

                                if (d > mActivity.currentDebt) {
                                    tvCashReturn.setVisibility(View.VISIBLE);
                                    tvCashReturn.setText(String.format(cash, Util.FormatMoney(d - mActivity.currentDebt)));

                                } else {
                                    tvCashReturn.setVisibility(View.GONE);
                                }

                            } else {
                                rvDebt.setVisibility(View.GONE);
                                tvCashReturn.setVisibility(View.VISIBLE);
                                tvCashReturn.setText(String.format(cash, Util.FormatMoney(d)));
                            }

                        } else {
                            rvDebt.setVisibility(View.GONE);
                            lnOption.setVisibility(View.GONE);
                            tvCashReturn.setVisibility(View.GONE);
                        }

                    } else {
                        rvDebt.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.GONE);
                        lnOption.setVisibility(View.GONE);
                        tvCashReturn.setVisibility(View.GONE);

                        if (d < 0.0) {
                            tvSum.setText("HÓA ĐƠN ĐÃ TRẢ HẾT HÀNG");
                        }
                    }

                }


            }
        });
        Util.createLinearRV(rvReturn, adapterReturn);
        rvReturn.setHasFixedSize(true);
    }

    private List<BaseModel> remakeDebtBill(List<BaseModel> listdebt, BaseModel currentdebt) {
        List<BaseModel> list = DataUtil.getListDebtRemain(listdebt, currentdebt);
        Collections.reverse(list);
        list.add(0, currentdebt);

        return list;

    }

    private void createRVDebt(List<BaseModel> listdebt) {
        adapterDebt = new DebtAdapter(listdebt, true, true);
        Util.createLinearRV(rvDebt, adapterDebt);
        rvReturn.setNestedScrollingEnabled(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            adapterDebt.inputPaid(totalReturn, true);
            rvDebt.setVisibility(View.VISIBLE);
            tvCashReturn.setVisibility(View.GONE);

            if (totalReturn > mActivity.currentDebt) {
                tvCashReturn.setVisibility(View.VISIBLE);
                tvCashReturn.setText(String.format(cash, Util.FormatMoney(totalReturn - mActivity.currentDebt)));

            } else {
                tvCashReturn.setVisibility(View.GONE);
            }

        } else {
            rvDebt.setVisibility(View.GONE);
            tvCashReturn.setVisibility(View.VISIBLE);
            tvCashReturn.setText(String.format(cash, Util.FormatMoney(totalReturn)));
            adapterDebt.inputPaid(0.0, true);
        }
    }

    private void onlyPostUpdateBillReturn(List<BaseModel> listProductReturn, Double sumreturn, BaseModel currentBill) {
        postUpdateBillReturn(listProductReturn, sumreturn, currentBill, new CallbackObject() {
            @Override
            public void onResponse(BaseModel result) {
                Util.showToast("Trả hàng thành công");
                finish();
            }

        }, true);

    }

    private void postBillReturnUpdatePayment(List<BaseModel> listProductReturn, Double sumreturn, BaseModel currentBill) {
        postUpdateBillReturn(listProductReturn, sumreturn, currentBill, new CallbackObject() {
            @Override
            public void onResponse(BaseModel result) {
                postListPayment(DataUtil.createListPaymentParam(mActivity.currentCustomer.getInt("id"),
                        adapterDebt.getListBillPayment(), true), new CallbackListCustom() {
                    @Override
                    public void onResponse(List result) {
                        Util.showToast("Trả hàng thành công");
                        finish();
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

            }


        }, false);

    }

    private void postBillReturnUpdatePaymentAndCash(List<BaseModel> listProductReturn, List<BaseModel> listpay, Double sumreturn, BaseModel currentBill) {
        postUpdateBillReturn(listProductReturn, sumreturn, currentBill, new CallbackObject() {
            @Override
            public void onResponse(BaseModel result) {
                List<BaseModel> listpayment = adapterDebt.getListBillPayment();
                for (BaseModel model : listpayment) {
                    if (currentBill.getInt("id") == model.getInt("billId")) {
                        model.put("paid", sumreturn - mActivity.currentDebt + currentBill.getDouble("debt"));
                        break;
                    }
                }

                List<String> payments = DataUtil.createListPaymentParam(mActivity.currentCustomer.getInt("id"),
                        listpayment, true);


//                payments.add(DataUtil.createPostPaymentParam(mActivity.currentCustomer.getInt("id"),
//                        currentBill.getInt("user_id"),
//                        mActivity.currentDebt - sumreturn,
//                        currentBill.getInt("id"),
//                        "", false));
                payments.add(DataUtil.createPostPaymentParam(mActivity.currentCustomer.getInt("id"),
                        currentBill.getInt("user_id"),
                        mActivity.currentDebt - sumreturn,
                        0,
                        "", false));


                postListPayment(payments, new CallbackListCustom() {
                    @Override
                    public void onResponse(List result) {
                        Util.showToast("Trả hàng thành công");
                        finish();
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

//                List<BaseModel> listpayment = new ArrayList<>();
//
//                for (int i=0; i<listpay.size(); i++){
//                    listpayment.add(i, listpay.get(i));
//
//                }
//
//                if (listpayment.size() > 0){
//                    listpayment.get(0).put("paid", sumreturn - mActivity.currentDebt + currentBill.getDouble("debt"));


//                    updateListBill(listpayment, reBill, currBill, new CallbackBoolean() {
//                        @Override
//                        public void onRespone(Boolean result) {
//                            if (result){
//                                postPayment(mActivity.currentCustomer.getInt("id"),
//                                        currentBill.getInt("id"),
//                                        mActivity.currentDebt - sumreturn ,
//                                        currentBill.getDouble("total"),
//                                        new CallbackBoolean() {
//                                            @Override
//                                            public void onRespone(Boolean result) {
//                                                if (result){
//                                                    Util.showToast("Trả hàng thành công");
//                                                    finish();
//
//                                                }
//                                            }
//                                        }, true);
//                            }
//                        }
//                    }, false);

//                }else {
//                    postPayment(mActivity.currentCustomer.getInt("id"),
//                            currentBill.getInt("id"),
//                            mActivity.currentDebt - sumreturn ,
//                            currentBill.getDouble("total"),
//                            new CallbackBoolean() {
//                                @Override
//                                public void onRespone(Boolean result) {
//                                    if (result){
//                                        Util.showToast("Trả hàng thành công");
//                                        finish();
//
//                                    }
//                                }
//                            }, true);
//                }
            }

        }, false);

    }

//    private void updateListBill(List<BaseModel> listPayment , BaseModel billReturn, BaseModel currentbill , CallbackBoolean listener, boolean loading){
//        List<String> listparams = new ArrayList<>();
//        //update debt Bill
//        for (BaseModel baseModel : listPayment){
//            for (int i=0; i<listDebt.size(); i++){
//                if (baseModel.getInt("billId") == listDebt.get(i).getInt("id")){
//                    String param = "";
//                    if (listDebt.get(i).getInt("id") == currentbill.getInt("id")){
//                        param = DataUtil.updateBillHavePaymentParam(mActivity.currentCustomer.getInt("id"),
//                                currentbill,
//                                billReturn,
//                                baseModel.getDouble("paid"));
//
//                    }else {
//                        param = DataUtil.updateBillHavePaymentParam(mActivity.currentCustomer.getInt("id"),
//                                listDebt.get(i),
//                                billReturn,
//                                baseModel.getDouble("paid"));
//
//                    }
//
//                    listparams.add(param);
//
//                }
//            }
//        }
//
//        CustomerConnect.PostListBill(listparams, new CallbackListCustom() {
//            @Override
//            public void onResponse(List result) {
//                listener.onRespone(true);
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.showSnackbarError(error);
//                listener.onRespone(false);
//            }
//        }, loading);
//
//    }

    private void postUpdateBillReturn(List<BaseModel> listProductReturn, Double sumreturn, BaseModel currentBill, CallbackObject listener, boolean loading) {
        BaseModel param = createPostParam(ApiUtil.BILL_NEW(),
                DataUtil.newBillJsonParam(mActivity.currentCustomer.getInt("id"),
                User.getId(),
                0.0,
                0.0,
                0.0,
                listProductReturn,
                "",
                User.getId(),
                currentBill.getInt("id")),
                true,
                false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(result);
            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();


    }


    private void postListPayment(List<String> listParam, CallbackListCustom listener) {
        List<BaseModel> params = new ArrayList<>();

        for (String itemdetail: listParam){
            BaseModel item = createPostParam(ApiUtil.PAY_NEW(), itemdetail, false, false);
            params.add(item);
        }

        new GetPostListMethod(params, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                listener.onResponse(results);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }


}
