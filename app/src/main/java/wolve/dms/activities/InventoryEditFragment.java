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

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.InventoryEditAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;

/**
 * Created by macos on 9/16/17.
 */

public class InventoryEditFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tvTitle;
    private RecyclerView rvInventory;
    private ImageView btnBack;

    private WarehouseActivity mActivity;
    private List<BaseModel> listInventory = new ArrayList<>();
    private InventoryEditAdapter adapter;
    private BaseModel currrentWarehouse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inventory_edit, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.TEMPWAREHOUSE);
        if (bundle != null) {
            currrentWarehouse = new BaseModel(bundle);

            tvTitle.setText(String.format("Hiệu chỉnh tồn %s", currrentWarehouse.getString("name")));

            loadInventories(currrentWarehouse.getInt("id"), new CallbackListObject() {
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

    }

    private void initializeView() {
        mActivity = (WarehouseActivity) getActivity();
        btnBack = view.findViewById(R.id.icon_back);
        rvInventory = view.findViewById(R.id.inventory_edit_rv);
        tvTitle = view.findViewById(R.id.inventory_edit_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;


        }
    }

    private void loadInventories(int warehouse_id, CallbackListObject listener) {
        BaseModel param = createGetParam(String.format(ApiUtil.INVENTORIES(), warehouse_id), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();




    }

    private void createRVInventory(List<BaseModel> list) {
        adapter = new InventoryEditAdapter(list);
        Util.createLinearRV(rvInventory, adapter);
    }


}
