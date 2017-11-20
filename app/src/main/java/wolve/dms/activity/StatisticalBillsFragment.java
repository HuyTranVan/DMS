package wolve.dms.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.StatisticalBillsAdapter;
import wolve.dms.models.Bill;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalBillsFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvBill;
    private TextView tvTotal, tvCount;
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

    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        rvBill = (RecyclerView) view.findViewById(R.id.statistical_bills_rvbill);
        tvTotal = (TextView) view.findViewById(R.id.statistical_bills_total);
        tvCount = (TextView) view.findViewById(R.id.statistical_bills_count);

    }

    @Override
    public void onClick(View v) {

    }

    public void reloadData(List<Bill> list){
        CreateRVBill(list);
    }


    public void CreateRVBill(List<Bill> list){
        Double total = 0.0;
        for (int i=0; i<list.size(); i++){
            total +=list.get(i).getDouble("total");
        }
        tvTotal.setText("Tổng cộng: "+ Util.FormatMoney(total));
        tvCount.setText("Số lượng hóa đơn: " + list.size());

        adapter = new StatisticalBillsAdapter(list);
        rvBill.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvBill.setLayoutManager(layoutManager);
        rvBill.setNestedScrollingEnabled(true);
    }


}
