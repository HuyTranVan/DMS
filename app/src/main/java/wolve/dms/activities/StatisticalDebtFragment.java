package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistical_DebtAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalDebtFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvDebts;
    private TextView tvSum;
    private CTextIcon tvSort ;
    private StatisticalActivity mActivity;

    protected Statistical_DebtAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_debt,container,false);
        Util.debtFragment = this;
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        //listDebt = new ArrayList<>();
    }

    private void addEvent() {
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
    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        rvDebts = (RecyclerView) view.findViewById(R.id.statistical_debt_rvbill);
        tvSum = view.findViewById(R.id.statistical_debt_count);
        tvSort = view.findViewById(R.id.statistical_debt_sort);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public void reloadData(String user, List<BaseModel> listDebt){
        createRVCDebt(user, listDebt);

    }

    private void createRVCDebt(String user, List<BaseModel> list) {
        adapter = new Statistical_DebtAdapter(user, list, new CallbackString() {
            @Override
            public void Result(String s) {
                CustomerConnect.GetCustomerDetail(s, new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {
                        CustomSQL.setString(Constants.CUSTOMER, result.BaseModelstoString());
                        Transaction.gotoCustomerActivity(false);
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true, true);

            }
        });
        Util.createLinearRV(rvDebts, adapter);
        tvSum.setText(String.format("Tổng công nợ: %s", Util.FormatMoney(adapter.sumDebts())));

    }

    protected double getSumDebt(){
        return adapter.sumDebts();
    }


}
