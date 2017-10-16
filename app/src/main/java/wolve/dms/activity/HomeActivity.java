package wolve.dms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.HomeAdapter;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackStatus;
import wolve.dms.models.Status;
import wolve.dms.utils.CustomDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/15/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter {
    private RecyclerView rvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createListItem() {
        HomeAdapter adapter = new HomeAdapter(HomeActivity.this);
        adapter.notifyDataSetChanged();
        rvItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        //linearLayoutManager.setAutoMeasureEnabled(false);
        rvItems.setLayoutManager(linearLayoutManager);

    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_home;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        rvItems= (RecyclerView) findViewById(R.id.home_rvitems);
    }

    @Override
    public void initialData() {
        createListItem();
        loadCurrentData();
    }

    @Override
    public void addEvent() {

    }

    @Override
    public void onClick(View v) {
//        Transaction.gotoMapsActivity();
    }

    @Override
    public void onRespone(String data, int position) {
        switch (position){
            case 0:
                Transaction.gotoMapsActivity();
                break;

            case 1:
                Transaction.gotoStatisticalActivity();

                break;

            case 2:

                break;

            case 3:
                Transaction.gotoProductActivity();
                break;

            case 4:
                Transaction.gotoStatusActivity();
                break;
        }
    }

    private void loadCurrentData() {
        StatusConnect.ListStatus(new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                Util.setStatusList(result);
                loadDistrict(false);
            }

            @Override
            public void onError(String error) {

            }
        }, false);
    }

    private void loadProvince(Boolean cancelLoading){
        LocationConnect.getAllProvinces(new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                Util.setProvincesList(result);
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void loadDistrict(Boolean cancelLoading){
        LocationConnect.getAllDistrict("79", new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                Util.setDistrictList(result);
                loadProductGroup(false);

            }

            @Override
            public void onError(String error) {

            }
        }, cancelLoading);
    }

    private void loadProductGroup(Boolean cancelLoading){
        ProductConnect.ListProductGroup(new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                Util.setProductGroupList(result);
                loadProducts(true);

            }

            @Override
            public void onError(String error) {

            }
        }, cancelLoading);
    }

    private void loadProducts(Boolean cancelLoading){
        ProductConnect.ListProduct(new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                Util.setProductList(result);
            }

            @Override
            public void onError(String error) {

            }
        }, cancelLoading);
    }



}
