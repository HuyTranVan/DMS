package wolve.dms.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ProductImportChoosenAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.activities.BaseActivity.createPostParam;

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

    private List<BaseModel> listInventory = new ArrayList<>();
    private ProductImportChoosenAdapter adapter;
    private BaseModel currrentWarehouse, returnWarehouse;
    private CallbackObject onDataPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_import_return, container, false);
        initializeView();

        currrentWarehouse = CustomSQL.getBaseModel(Constants.CURRENT_WAREHOUSE);

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        loadTempWarehouse(new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                returnWarehouse = object;
                tvTitle.setText(String.format("Trả hàng về %s", returnWarehouse.getString("name")));

                loadListInventory(currrentWarehouse.getInt("id"), new CallbackListObject() {
                    @Override
                    public void onResponse(List<BaseModel> list){
                        listInventory = list;
                        createRVInventory(listInventory);

                    }
                }, 1);
            }
        }, 2);

    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        checkBoxEvent();
    }

    private void initializeView() {
        btnBack = view.findViewById(R.id.icon_back);
        rvInventory = view.findViewById(R.id.import_return_rvcustomer);
        tvTitle = view.findViewById(R.id.import_return_title);
        cbAll = view.findViewById(R.id.import_return_checkall);
        btnSubmit = view.findViewById(R.id.import_return_submit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.import_return_submit:
                submitImport();

                break;


        }
    }

    private void loadTempWarehouse(CallbackObject listener, int loadingtimes) {
        BaseModel param = createGetParam(String.format(ApiUtil.WAREHOUSE_TEMP()), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list){
                listener.onResponse(result);
            }

            @Override
            public void onError(String error) {

            }
        }, loadingtimes).execute();



    }

    private void loadListInventory(int warehouse_id, CallbackListObject listener, int loadingtimes) {
        BaseModel param = createGetParam(String.format(ApiUtil.INVENTORIES(), warehouse_id, 0), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list){
                listener.onResponse(list);
            }

            @Override
            public void onError(String error) {

            }
        }, loadingtimes).execute();



    }

    private void createRVInventory(List<BaseModel> list) {
        adapter = new ProductImportChoosenAdapter(list, false,  new CallbackInt() {
            @Override
            public void onResponse(int value) {
                btnSubmit.setVisibility(adapter.getAllSelected() >= 0 ? View.VISIBLE : View.GONE);

            }
        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {

            }
        });
        Util.createLinearRV(rvInventory, adapter);
    }

    private void checkBoxEvent() {
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    adapter.selectAll();

                } else {
                    adapter.noneSelect();

                }
            }
        });
    }

    private void submitImport() {
        String param = DataUtil.createPostImportJsonParam(returnWarehouse.getInt("id"),
                currrentWarehouse.getInt("id"),
                adapter.getAllDataHaveQuantity(),
                Util.isAdmin() ? User.getId() : 0,
                "");
        postImport(param, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    DataUtil.saveProductPopular(adapter.getAllDataHaveQuantity());
                    getActivity().getSupportFragmentManager().popBackStack();
                    onDataPass.onResponse(new BaseModel());

                }
            }
        });


    }

    private void postImport(String paramdetail, CallbackBoolean listener) {
        BaseModel param = createPostParam(ApiUtil.IMPORT_NEW(),
                paramdetail, true, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onRespone(true);
            }

            @Override
            public void onError(String error) {
                listener.onRespone(false);
            }
        }, 1).execute();


//        CustomerConnect.PostImport(param, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                listener.onRespone(true);
//
//            }
//
//            @Override
//            public void onError(String error) {
//                listener.onRespone(false);
//            }
//        }, true);
    }


}
