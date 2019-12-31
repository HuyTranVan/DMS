package wolve.dms.activities;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistical_BillsAdapter;
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

public class StatisticalBillsFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private View view;
    private RecyclerView rvBill;
    //private TextView tvCount;
    private RadioGroup rdGroup;
    private RadioButton rdTotal, rdDebt;
    private Statistical_BillsAdapter adapter;

    private StatisticalActivity mActivity;
    //private List<BaseModel> listDebt = new ArrayList<>();
//    private Double totalDebt = 0.0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_bills,container,false);
        Util.billsFragment = this;

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {

    }

    private void addEvent() {
        rdGroup.setOnCheckedChangeListener(this);
        //tvCount.setOnClickListener(this);
    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        rvBill = (RecyclerView) view.findViewById(R.id.statistical_bills_rvbill);
        //tvCount = (TextView) view.findViewById(R.id.statistical_bills_count);
        rdGroup = (RadioGroup) view.findViewById(R.id.statistical_bills_totalgroup);
        rdDebt = (RadioButton) view.findViewById(R.id.statistical_bills_debt);
        rdTotal = (RadioButton) view.findViewById(R.id.statistical_bills_total);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){



        }
    }

    public void reloadData(String username, List<BaseModel> list){
        CreateRVBill(username, list);
    }


    public void CreateRVBill(String username, List<BaseModel> list){
        adapter = new Statistical_BillsAdapter(username, list, new CallbackString() {
            @Override
            public void Result(String s) {
                CustomerConnect.GetCustomerDetail(s, new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {
                        BaseModel customer = DataUtil.rebuiltCustomer(result);
                        CustomSQL.setBaseModel(Constants.CUSTOMER, customer);

                        Transaction.gotoCustomerActivity();
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true,true);
            }
        });
        Util.createLinearRV(rvBill, adapter);
        updateRadioText();

    }

    private void updateRadioText(){
        BaseModel bill = adapter.sumBill();
        rdTotal.setText(String.format("Tổng: %s", Util.FormatMoney(bill.getDouble("total"))));
        rdDebt.setText(String.format("Nợ: %s", Util.FormatMoney(bill.getDouble("debt"))));
        adapter.getFilter().filter(rdTotal.isChecked()? Constants.ALL_TOTAL : Constants.ALL_DEBT);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.statistical_bills_total:
                adapter.getFilter().filter(Constants.ALL_TOTAL);
                break;

            case R.id.statistical_bills_debt:
                adapter.getFilter().filter(Constants.ALL_DEBT);
                break;
        }
    }

    public BaseModel getSumBill(){
        return adapter.sumBill();
    }

    public double getSumBillTotal(){
        return adapter.sumBillTotal();
    }
}
