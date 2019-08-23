package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Customer_BillsAdapter;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerBillsFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvBill;
    private Customer_BillsAdapter adapter;

    private CustomerActivity mActivity;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_bills,container,false);
        CustomerActivity.billsFragment = this;

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        CreateRVBill();
    }

    private void addEvent() {
        //tvCount.setOnClickListener(this);
    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        rvBill = (RecyclerView) view.findViewById(R.id.customer_rvbill);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


        }
    }

    public void updateList(){
        adapter.updateData(mActivity.listBills);
    }


    public void CreateRVBill(){
        adapter = new Customer_BillsAdapter(new ArrayList<>(), new CallbackBaseModel() {
            @Override
            public void onResponse(BaseModel bill) {
                mActivity.openReturnFragment(bill);

            }

            @Override
            public void onError() {

            }

        }, new CustomBottomDialog.FourMethodListener() {
            @Override
            public void Method1(Boolean one) {
                mActivity.reloadCustomer();
            }

            @Override
            public void Method2(Boolean two) {
                mActivity.reloadCustomer();

            }

            @Override
            public void Method3(Boolean three) {
                mActivity.reloadCustomer();
            }

            @Override
            public void Method4(Boolean four) {
                mActivity.reloadCustomer();
            }
        });

        Util.createLinearRV(rvBill, adapter);

    }

}