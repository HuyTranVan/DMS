package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.StatisticalCheckinsAdapter;
import wolve.dms.adapter.StatisticalProductGroupAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalCheckinFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvCheckins;
    private TextView tvCount;


    private StatisticalCheckinsAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_checkins,container,false);
        Util.checkinFragment = this;
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
        rvCheckins = (RecyclerView) view.findViewById(R.id.checkins_rvcheckin);
        tvCount = view.findViewById(R.id.checkins_count);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public void reloadData(List<BaseModel> list){
        createRVCheckin(list);

    }

    private void createRVCheckin(List<BaseModel> list) {
        adapter = new StatisticalCheckinsAdapter(list);
        Util.createLinearRV(rvCheckins, adapter);

        tvCount.setText(String.format("Số lần ghé cửa hàng: %d", adapter.getItemCount()));

    }

    private boolean checkDuplicateCheckin(List<BaseModel> list, int id){
        boolean check = false;
        for (int i=0; i<list.size(); i++){
            if (list.get(i).getInt("id") == id){
                check = true;
                break;
            }
        }
        return check;
    }

}
