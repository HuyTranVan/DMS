package wolve.dms.activity;

import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import org.json.JSONArray;

import java.util.ArrayList;

import android.widget.Toast;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.HomeAdapter;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.models.District;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.Status;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/15/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter {
    private RecyclerView rvItems;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createListItem() {
        HomeAdapter adapter = new HomeAdapter(HomeActivity.this);
        adapter.notifyDataSetChanged();
        rvItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (doubleBackToExitPressedOnce) {
                Util.getInstance().getCurrentActivity().finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Util.showToast("Ấn Back để thoát khỏi ứng dụng");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

        return true;
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
//                addContact();
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
                Status.saveStatusList(result);
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
                District.saveDistrictList(result);
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
                ProductGroup.saveProductGroupList(result);
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
                Product.saveProductList(result);
            }

            @Override
            public void onError(String error) {

            }
        }, cancelLoading);
    }



}
