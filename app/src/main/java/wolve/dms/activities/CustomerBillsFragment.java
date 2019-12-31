package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Customer_BillsAdapter;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
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
            public void onResponse(BaseModel result) {
                switch (result.getString(Constants.TYPE)){
                    case Constants.BILL_RETURN:
                        mActivity.openReturnFragment(result.getBaseModel(Constants.RESULT));
                        break;

                    case Constants.BILL_DELIVER:
                        mActivity.printTempBill(result.getBaseModel(Constants.RESULT));
                        break;

                    case Constants.BILL_DELETE:
                        mActivity.reloadCustomer(mActivity.currentCustomer.getString("id"), 3);
                        break;

                    case Constants.BILL_PAY:
                        mActivity.reloadCustomer(mActivity.currentCustomer.getString("id"), 3);
                        break;


                }
            }

            @Override
            public void onError() {

            }

        });

        Util.createLinearRV(rvBill, adapter);

    }

    public void updateTempBill(){
        adapter.updateTempBill();
    }

}
