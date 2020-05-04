package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistical_PaymentAdapter;
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

public class StatisticalPaymentFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private View view;
    private RecyclerView rvCash;
    //private TextView tvCount;
    private RadioGroup rdGroup;
    private RadioButton rdTotal, rdCollect;

    private Statistical_PaymentAdapter adapter;


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
        rdGroup.setOnCheckedChangeListener(this);
    }

    private void initializeView() {
        rvCash = (RecyclerView) view.findViewById(R.id.statistical_cash_rvbill);
        rdGroup = view.findViewById(R.id.statistical_cash_totalgroup);
        rdTotal = view.findViewById(R.id.statistical_cash_total);
        rdCollect = view.findViewById(R.id.statistical_cash_collect);

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
        adapter = new Statistical_PaymentAdapter(user, list, new CallbackString() {
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

        rdTotal.setText(String.format("Tổng: %s",Util.FormatMoney(adapter.sumPayments())));

        if (adapter.sumCollect() == 0.0){
            rdCollect.setVisibility(View.GONE);

        }else {
            rdCollect.setVisibility(View.VISIBLE);
            rdCollect.setText(String.format("Thu hộ: %s",Util.FormatMoney(adapter.sumCollect())));
        }


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.statistical_cash_total:
                adapter.getFilter().filter(Constants.ALL_TOTAL);
                break;

            case R.id.statistical_cash_collect:
                adapter.getFilter().filter(Constants.ALL_COLLECT);
                break;
        }
    }

    public double getSumPayment(){
        return adapter.sumPayments();
    }

    public int getCoutList(){
        return adapter.getItemCount();
    }

    public double getSumProfit(){
        return adapter.sumProfit();

    }

    public double getSumBaseProfit() {
        return adapter.sumBaseProfit();

    }
}
