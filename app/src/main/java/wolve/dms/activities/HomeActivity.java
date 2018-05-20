package wolve.dms.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.HomeAdapter;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackList;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.ItemDecorationGridSpace;
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

import static wolve.dms.utils.Constants.REQUEST_PERMISSION_LOCATION;

/**
 * Created by macos on 9/15/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter {
    private RecyclerView rvItems;
    private CTextIcon btnLogout;
    private TextView tvFullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        tvFullname = findViewById(R.id.home_fullname);
    }

    @Override
    public void initialData() {
        createListItem();
        loadCurrentData();
        tvFullname.setText(String.format("%s (%s)",User.getFullName(), User.getRole()));

    }

    private void createListItem() {
        HomeAdapter adapter = new HomeAdapter(HomeActivity.this);
        adapter.notifyDataSetChanged();
        rvItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        rvItems.setLayoutManager(linearLayoutManager);
        rvItems.addItemDecoration(new ItemDecorationGridSpace((int) Util.convertDp2Px(1),3));

    }

    @Override
    public void addEvent() {
        btnLogout.setOnClickListener(this);
        tvFullname.setOnClickListener(this);
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
        CustomCenterDialog.alertWithCancelButton(null, String.format("Đăng xuất tài khoản %s",User.getFullName()) , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
//                CustomSQL.setString(Constants.USER_USERNAME,"");
//                CustomSQL.setString(Constants.USER_PASSWORD,"");
                CustomSQL.clear();
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
//                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(Util.getInstance().getCurrentActivity(),
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
//                    return;
//                }
                Transaction.gotoMapsActivity();

                break;

            case 1:
                Transaction.gotoStatisticalActivity();

                break;

            case 2:
                Util.showToast("Chưa hỗ trợ");
                Transaction.gotoBluetoothListActivity();

                break;

            case 3:
                Util.showToast("Chưa hỗ trợ");
                break;

            case 4:
                if (Distributor.getDistributorId().equals("1")){
                    choiceSetupItem();
                }else {
                    Util.showToast("Chưa hỗ trợ");
                }
                break;

            case 5:
                Util.showToast("Chưa hỗ trợ");
                break;
        }
    }

    private void loadCurrentData() {
        SystemConnect.getAllData(new CallbackList() {
            @Override
            public void onResponse(List result) {
                try {
                    Status.saveStatusList(new JSONArray(result.get(0).toString()));
                    District.saveDistrictList(new JSONArray(result.get(1).toString()));
                    ProductGroup.saveProductGroupList(new JSONArray(result.get(2).toString()));
                    Product.saveProductList(new JSONArray(result.get(3).toString()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(String error) {

            }
        }, true);

//        StatusConnect.ListStatus(new CallbackJSONArray() {
//            @Override
//            public void onResponse(JSONArray result) {
//                Status.saveStatusList(result);
//                loadDistrict(false);
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, false);
    }

//    private void loadProvince(Boolean cancelLoading){
//        LocationConnect.getAllProvinces(new CallbackJSONArray() {
//            @Override
//            public void onResponse(JSONArray result) {
//                Util.setProvincesList(result);
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, true);
//    }
//
//    private void loadDistrict(Boolean cancelLoading){
//        LocationConnect.getAllDistrict("79", new CallbackJSONArray() {
//            @Override
//            public void onResponse(JSONArray result) {
//                District.saveDistrictList(result);
//                loadProductGroup(false);
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, cancelLoading);
//    }
//
//    private void loadProductGroup(Boolean cancelLoading){
//        ProductConnect.ListProductGroup(false, new CallbackJSONArray() {
//            @Override
//            public void onResponse(JSONArray result) {
//                ProductGroup.saveProductGroupList(result);
//                loadProducts(true);
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, cancelLoading);
//    }
//
//    private void loadProducts(Boolean cancelLoading){
//        ProductConnect.ListProduct(new CallbackJSONArray() {
//            @Override
//            public void onResponse(JSONArray result) {
//                Product.saveProductList(result);
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, cancelLoading);
//    }

    private void choiceSetupItem(){
        CustomBottomDialog.choiceFourOption(getString(R.string.icon_group), "Nhân viên",
                getString(R.string.icon_product_group), "Nhóm sản phẩm",
                getString(R.string.icon_product), "Sản phẩm",
                getString(R.string.icon_comment), "Trạng thái", new CustomBottomDialog.FourMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        Util.showToast("Chưa hỗ trợ");
                        Transaction.gotoTestActivity();
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

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED ) {
                Util.showToast("Cấp quyền truy cập không thành công");

            }else {
                Transaction.gotoMapsActivity();
            }

        }

    }

}
