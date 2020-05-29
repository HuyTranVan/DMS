package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ProductImportChoosenAdapter;
import wolve.dms.adapter.Statistical_OrderedAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ImportReturnFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tvTitle;
    private RecyclerView rvInventory;
    private ImageView btnBack;
    private CheckBox cbAll;
    private Button btnSubmit;

    private WarehouseActivity mActivity;
    private List<BaseModel> listInventory = new ArrayList<>();
    private ProductImportChoosenAdapter adapter;
    private BaseModel currrentWarehouse, returnWarehouse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_import_return,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle1 = getArguments().getString(Constants.WAREHOUSE);
        String bundle2 = getArguments().getString(Constants.TEMPWAREHOUSE);
        if (bundle1 != null){
            currrentWarehouse = new BaseModel(bundle1);
            returnWarehouse = new BaseModel(bundle2);

            tvTitle.setText(String.format("Trả hàng về %s", returnWarehouse.getString("name")));

            loadListInventory(currrentWarehouse.getInt("id"), new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> list) {
                    listInventory = list;
                    createRVInventory(listInventory);

                }
            });

        }


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        checkBoxEvent();
    }

    private void initializeView() {
        mActivity = (WarehouseActivity) getActivity();
        btnBack = view.findViewById(R.id.icon_back);
        rvInventory = view.findViewById(R.id.import_return_rvcustomer);
        tvTitle = view.findViewById(R.id.import_return_title);
        cbAll = view.findViewById(R.id.import_return_checkall);
        btnSubmit = view.findViewById(R.id.import_return_submit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;

            case R.id.import_return_submit:
                submitImport();

                break;


        }
    }

    private void loadListInventory(int warehouse_id, CallbackListObject listener){
        SystemConnect.GetInventoryList(warehouse_id, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                listener.onResponse(result);

            }

            @Override
            public void onError(String error) {

            }
        }, true);


    }

    private void createRVInventory(List<BaseModel> list){
        adapter = new ProductImportChoosenAdapter(list, false, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                btnSubmit.setVisibility(adapter.getAllSelected() >=0 ? View.VISIBLE: View.GONE);

            }}, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {

            }
        });
        Util.createLinearRV(rvInventory, adapter);
    }

    private void checkBoxEvent(){
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    adapter.selectAll();

                }else {
                    adapter.noneSelect();

                }
            }
        });
    }

    private void submitImport(){
        String param = DataUtil.createPostImportParam(returnWarehouse.getInt("id"),
                currrentWarehouse.getInt("id"),
                adapter.getAllDataHaveQuantity(),
                "");
        postImport(param, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    DataUtil.saveProductPopular( adapter.getAllDataHaveQuantity());
                    mActivity.initialData();
                    mActivity.onBackPressed();

                }
            }
        });


    }

    private void postImport(String param, CallbackBoolean listener){
        CustomerConnect.PostImport(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                listener.onRespone(true);

            }

            @Override
            public void onError(String error) {
                listener.onRespone(false);
            }
        }, true);
    }


}