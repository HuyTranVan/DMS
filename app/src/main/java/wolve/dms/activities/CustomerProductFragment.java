package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private void createRVProductGroup(List<BaseModel> listbilldetail) {
        adapter = new Statistical_ProductGroupAdapter( listbilldetail);
        Util.createLinearRV(rvProductGroup, adapter);

    }


}