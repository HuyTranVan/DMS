package wolve.dms.activities;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Import_ProductAdapter;
import wolve.dms.adapter.TempbillAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class TempImportFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private TextView tvTitle;
    private RecyclerView rvTempImport;

    private HomeActivity mActivity;
    private Import_ProductAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tempimport,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        createRVImport(mActivity.listTempImport);


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);


    }

    private void initializeView() {
        mActivity = (HomeActivity) getActivity();
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        tvTitle = view.findViewById(R.id.tempimport_title);
        rvTempImport = view.findViewById(R.id.tempimport_rv);

    }

    public void reloadData(){
        adapter.reloadData(mActivity.listTempImport);
        if (mActivity.listTempImport.size() ==0){
            mActivity.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;



        }
    }

    private void createRVImport(List<BaseModel> list){
        adapter = new Import_ProductAdapter(list, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    reloadImportList();

                }
            }
        });
        Util.createLinearRV(rvTempImport, adapter);
    }

    private void reloadImportList(){
        CustomerConnect.ListImport("", new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                mActivity.updateTempImportVisibility(results);

                reloadData();
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }


}
