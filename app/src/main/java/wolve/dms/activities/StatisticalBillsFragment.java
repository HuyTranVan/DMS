package wolve.dms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.StatisticalBillsAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.Bill;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalBillsFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private View view;
    private RecyclerView rvBill;
    private TextView tvCount;
    private RadioGroup rdGroup;
    private RadioButton rdTotal, rdDebt;
    private StatisticalBillsAdapter adapter;

    private StatisticalActivity mActivity;


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
    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        rvBill = (RecyclerView) view.findViewById(R.id.statistical_bills_rvbill);
        tvCount = (TextView) view.findViewById(R.id.statistical_bills_count);
        rdGroup = (RadioGroup) view.findViewById(R.id.statistical_bills_totalgroup);
        rdDebt = (RadioButton) view.findViewById(R.id.statistical_bills_debt);
        rdTotal = (RadioButton) view.findViewById(R.id.statistical_bills_total);

    }

    @Override
    public void onClick(View v) {

    }

    public void reloadData(List<Bill> list){
        CreateRVBill(list);
    }


    public void CreateRVBill(List<Bill> list){
        Double total = 0.0;
        Double debt = 0.0;
        List<Bill> listDebt = new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            total +=list.get(i).getDouble("total");
            debt += list.get(i).getDouble("debt");

            if (list.get(i).getDouble("debt") != 0){
                listDebt.add(list.get(i));
            }
        }
        tvCount.setText(String.format("HĐ: %s",rdTotal.isChecked()? list.size() : listDebt.size()));
        rdTotal.setText(String.format("Tổng: %s", Util.FormatMoney(total)));
        rdDebt.setText(String.format("Nợ: %s", Util.FormatMoney(debt)));

        adapter = new StatisticalBillsAdapter(rdTotal.isChecked()? list : listDebt, new CallbackString() {
            @Override
            public void Result(String s) {
                CustomerConnect.GetCustomerDetail(s, new CallbackJSONObject() {
                    @Override
                    public void onResponse(JSONObject result) {
                        Transaction.gotoCustomerActivity(result.toString());

                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true);
            }
        });
        rvBill.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvBill.setLayoutManager(layoutManager);
        rvBill.setNestedScrollingEnabled(true);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.statistical_bills_total:
                CreateRVBill(mActivity.listBill);
                break;

            case R.id.statistical_bills_debt:
                CreateRVBill(mActivity.listBill);
                break;
        }
    }
}
