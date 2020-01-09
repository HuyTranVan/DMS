package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistica_PaymentAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalPaymentFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvCash;
    private TextView tvCount;


    private Statistica_PaymentAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_cash,container,false);
        Util.paymentFragment = this;
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {

    }



    private void addEvent() {

    }

    private void initializeView() {
        rvCash = (RecyclerView) view.findViewById(R.id.statistical_cash_rvbill);
        tvCount = view.findViewById(R.id.statistical_cash_count);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public void reloadData(String user, List<BaseModel> list ){
        createRVCash(user, list);
    }

    private void createRVCash(String user, List<BaseModel> list) {
        adapter = new Statistica_PaymentAdapter(user, list, new CallbackString() {
            @Override
            public void Result(String s) {
                CustomerConnect.GetCustomerDetail(s, new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {
                        BaseModel customer = DataUtil.rebuiltCustomer(result, false);
                        CustomSQL.setBaseModel(Constants.CUSTOMER, customer);

                        Transaction.gotoCustomerActivity();
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true,true);
            }
        });
        Util.createLinearRV(rvCash, adapter);

        tvCount.setText(String.format("Tổng tiền thu:    %s",Util.FormatMoney(adapter.sumPayments())));

    }

    public double getSumPayment(){
        return adapter.sumPayments();
    }

    public double getSumProfit(){
        return adapter.sumProfit();

    }
}
