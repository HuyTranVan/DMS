package wolve.dms.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.WarehouseAdapter;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class WarehouseActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBack;
    private RecyclerView rvDepot;
    private WarehouseAdapter adapter;
    private CTextIcon btnAddPDepot;
    private LinearLayout btnImport;

    public List<BaseModel> listDepot ;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_warehouse;
    }

    @Override
    public int setIdContainer() {
        return R.id.depot_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvDepot = (RecyclerView) findViewById(R.id.depot_rv);
        btnAddPDepot =  findViewById(R.id.depot_add_new);
        btnImport = findViewById(R.id.warehouse_import);

    }

    @Override
    public void initialData() {
        btnAddPDepot.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        loadDepot();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddPDepot.setOnClickListener(this);
        btnImport.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.depot_add_new:
                openFragmentNewDepot(null);

                break;

            case R.id.warehouse_import:


                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.depot_parent);
        if(mFragment != null && mFragment instanceof NewUpdateDepotFragment) {
            getSupportFragmentManager().popBackStack();

        }else {
            Transaction.gotoHomeActivityRight(true);
        }
    }

    protected void loadDepot() {
        SystemConnect.ListWarehouse(true, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                createRVDepot(results);

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void createRVDepot(List<BaseModel> list){
        listDepot = list;
        adapter = new WarehouseAdapter(listDepot, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (Util.isAdmin() || User.getId() == object.getInt("user_id")){
                    if (object.getInt("isMaster") == 1){
                        Util.showSnackbar("Không thể nhập kho tổng", null, null);

                    }else if (object.getInt("isMaster") == 2 || object.getInt("isMaster") == 3){
                        Transaction.gotoImportActivity(object, false);

                    }

                }else {
                    Util.showSnackbar("Không thể thực hiện", null, null);
                }

            }

        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (Util.isAdmin()){
                    openFragmentNewDepot(object.BaseModelstoString());

                }else {
                    Util.showSnackbar("Không thể sửa thông tin", null, null);
                }
            }

        });
        Util.createLinearRV(rvDepot, adapter);

    }

    private void openFragmentNewDepot(String depot){
        NewUpdateDepotFragment depotFragment = new NewUpdateDepotFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEPOT, depot);
        changeFragment(depotFragment, bundle, true );
    }




}
