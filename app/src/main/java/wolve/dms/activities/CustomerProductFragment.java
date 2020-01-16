package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistical_ProductGroupAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.models.BillDetail;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerProductFragment extends Fragment implements View.OnClickListener{
    private View view;
    private RecyclerView rvProductGroup;

    private Statistical_ProductGroupAdapter adapter;
    private CustomerActivity mActivity;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_product,container,false);
        CustomerActivity.productFragment = this;

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        createRVProductGroup(new ArrayList<>());
    }

    private void addEvent() {


    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        rvProductGroup = (RecyclerView) view.findViewById(R.id.customer_rvproduct);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){



        }
    }

    public void updateList(){
        adapter.updateData(mActivity.listBillDetail);
    }

    public void updateListByRange(String range){
        if (range.isEmpty()){
            adapter.updateData(mActivity.listBillDetail);

        }else {
            BaseModel contentObj = new BaseModel(range);
            List<BaseModel> listTemp = new ArrayList<>();
            for (BaseModel row : mActivity.listBillDetail) {
                if (row.getLong("createAt") >= contentObj.getLong("from") &&  row.getLong("createAt") <= contentObj.getLong("to")){
                    listTemp.add(row);
                }
            }
            adapter.updateData(listTemp);
        }

    }

    private void createRVProductGroup(List<BaseModel> listbilldetail) {
        adapter = new Statistical_ProductGroupAdapter( listbilldetail);
        Util.createLinearRV(rvProductGroup, adapter);

    }


}
