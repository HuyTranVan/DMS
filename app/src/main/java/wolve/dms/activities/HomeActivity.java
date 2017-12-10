package wolve.dms.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.HomeAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CTextIcon;
import wolve.dms.models.Distributor;
import wolve.dms.models.District;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.Status;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/15/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter {
    private RecyclerView rvItems;
    private CTextIcon btnLogout;

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
        btnLogout = findViewById(R.id.home_logout);
    }

    @Override
    public void initialData() {
        createListItem();
        loadCurrentData();


    }

    @Override
    public void addEvent() {
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_logout:
                doLogout();
                break;
        }
    }

    private void doLogout() {
        CustomCenterDialog.alertWithCancelButton(null, "Đăng xuất tài khoản hiện tại " , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                CustomSQL.setString(Constants.USER_USERNAME,"");
                CustomSQL.setString(Constants.USER_PASSWORD,"");
                Transaction.gotoLoginActivityRight();

            }
        });


//        String params = String.format(Api_link.LOGOUT_PARAM,User.getToken(), User.getUserId());
//
//        UserConnect.Logout(params, new CallbackJSONObject() {
//            @Override
//            public void onResponse(JSONObject result) {
//                String s = result.toString();
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, true);
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
                choiceSetup();
                break;

            case 3:

                break;

            case 4:

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
        ProductConnect.ListProductGroup(true, new CallbackJSONArray() {
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

    private void choiceSetup(){
        CustomBottomDialog.choiceFourOption(getString(R.string.icon_group), "Nhân viên",
                getString(R.string.icon_product_group), "Nhóm sản phẩm",
                getString(R.string.icon_product), "Sản phẩm",
                getString(R.string.icon_comment), "Trạng thái", new CustomBottomDialog.FourMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        Util.showToast("Chưa sẵn sàng");
                    }

                    @Override
                    public void Method2(Boolean two) {
                        Transaction.gotoProductGroupActivity();
                    }

                    @Override
                    public void Method3(Boolean three) {
                        Transaction.gotoProductActivity();
                    }

                    @Override
                    public void Method4(Boolean four) {
                        Transaction.gotoStatusActivity();
                    }
                });
    }



}
