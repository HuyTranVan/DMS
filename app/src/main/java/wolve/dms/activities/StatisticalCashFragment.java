package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.StatisticalCashAdapter;
import wolve.dms.adapter.StatisticalCheckinsAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalCashFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvCash;
    private TextView tvCount;


    private StatisticalCashAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_cash,container,false);
        Util.cashFragment = this;
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

    public void reloadData(List<BaseModel> list){
        createRVCash(list);
    }

    private void createRVCash(List<BaseModel> list) {
        adapter = new StatisticalCashAdapter(list);
        Util.createLinearRV(rvCash, adapter);

        tvCount.setText(String.format("Tổng tiền thu:    %s",Util.FormatMoney(getTotalPaid(list))));

    }

    private Double getTotalPaid(List<BaseModel> list){
        double paid= 0;
        for (int i=0; i<list.size(); i++){
            paid += list.get(i).getDouble("paid");
        }
        return paid;
    }

}
