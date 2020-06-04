package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Customer_CheckinsAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerCheckinFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvCheckin;

    private CustomerActivity mActivity;
    private Customer_CheckinsAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_checkin, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        createRVCheckin(mActivity.listCheckins);

    }

    private void addEvent() {


    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        rvCheckin = view.findViewById(R.id.checkins_rvcheckin);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;


        }
    }

    private void createRVCheckin(List<BaseModel> list) {
        adapter = new Customer_CheckinsAdapter(list, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    mActivity.reloadCustomer(mActivity.currentCustomer.getString("id"));

                }

            }
        });
        Util.createLinearRV(rvCheckin, adapter);
    }


}
