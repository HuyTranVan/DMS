package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.adapter.Customer_CheckinsAdapter;
import wolve.dms.adapter.ViewpagerShopcartAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CheckinFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvCheckin;

    private CustomerActivity mActivity;
    private Customer_CheckinsAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_checkin,container,false);
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
        switch (v.getId()){
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;



        }
    }

    private void createRVCheckin(List<BaseModel> list){
        adapter = new Customer_CheckinsAdapter(list);
        Util.createLinearRV(rvCheckin, adapter);
    }



}
