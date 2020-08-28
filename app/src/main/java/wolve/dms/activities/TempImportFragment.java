package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Import_ProductAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;

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
        view = inflater.inflate(R.layout.fragment_tempimport, container, false);
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

    public void reloadData() {
        adapter.reloadData(mActivity.listTempImport);
        if (mActivity.listTempImport.size() == 0) {
            mActivity.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;


        }
    }

    private void createRVImport(List<BaseModel> list) {
        adapter = new Import_ProductAdapter(0,list, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    reloadImportList();

                }
            }
        });
        Util.createLinearRV(rvTempImport, adapter);
    }

    private void reloadImportList() {
        BaseModel param = createGetParam(String.format(ApiUtil.IMPORTS(), 0, 20, 0), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                mActivity.updateTempImportVisibility(list);

                reloadData();
            }

            @Override
            public void onError(String error) {

            }
        }, true).execute();

//        CustomerConnect.ListImport(0,0, new CallbackCustomList() {
//            @Override
//            public void onResponse(List<BaseModel> results) {
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, true, true);
    }


}
