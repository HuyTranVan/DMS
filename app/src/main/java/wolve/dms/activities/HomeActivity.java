package wolve.dms.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.HomeAdapter;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.District;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.Province;
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

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter{
    private RecyclerView rvItems;
    private CTextIcon btnLogout, btnChangeUser;
    private TextView tvFullname ;
    private LinearLayout lnUser, lnRelogin;

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
        return R.id.home_parent;
    }

    @Override
    public void findViewById() {
        rvItems= (RecyclerView) findViewById(R.id.home_rvitems);
        btnLogout = findViewById(R.id.home_logout);
        tvFullname = findViewById(R.id.home_fullname);
        lnUser = findViewById(R.id.home_user);
        btnChangeUser = findViewById(R.id.home_change_user);
        lnRelogin = findViewById(R.id.home_relogin);
    }

    @Override
    public void initialData() {
        loadCurrentData();
        if (CustomSQL.getBoolean(Constants.ON_MAP_SCREEN)){
            Transaction.gotoMapsActivity();

        }
        createListItem();
        tvFullname.setText(String.format("%s _ %s (%s)",User.getFullName(), User.getRole(), Distributor.getName()));
        lnRelogin.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN) ? View.VISIBLE : View.GONE);

    }

    private void createListItem() {
        HomeAdapter adapter = new HomeAdapter(HomeActivity.this);
        Util.createGridRV(rvItems, adapter, 3);

    }

    @Override
    public void addEvent() {
        btnChangeUser.setOnClickListener(this);
        lnUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_change_user:
                changeUser();
                break;

            case R.id.home_user:
                CustomBottomDialog.choiceTwoOption(getString(R.string.icon_password),
                        "Đổi mật khẩu",
                        getString(R.string.icon_logout),
                        "Đăng xuất", new CustomBottomDialog.TwoMethodListener() {
                            @Override
                            public void Method1(Boolean one) {
                                changePassword();
                            }

                            @Override
                            public void Method2(Boolean two) {
                                doLogout();
                            }
                        });

                break;
        }
    }

    private void changePassword() {
        CustomCenterDialog.showDialogChangePass("Đổi mật khẩu", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    CustomCenterDialog.alertWithButtonCanceled("", "Đổi mật khẩu thành công , vui lòng đăng nhập lại", "ĐỒNG Ý",false, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                List<BaseModel> listUser = CustomSQL.getListObject(Constants.USER_LIST);
                                CustomSQL.clear();
                                CustomSQL.setListBaseModel(Constants.USER_LIST, listUser);

                                Transaction.gotoLoginActivityRight();
                            }

                        }
                    });

                }
            }
        });

    }

    private void changeUser(){
        List<BaseModel> listTemp = CustomSQL.getListObject(Constants.USER_LIST);
        List<BaseModel> listUser = new ArrayList<>();

        for (int i=0; i<listTemp.size(); i++){
            if (listTemp.get(i).getInt("id") !=  User.getId()){
                listTemp.get(i).put("showName", String.format("%s (%s)",listTemp.get(i).getString("displayName") ,
                                                new BaseModel(listTemp.get(i).getJsonObject("distributor")).getString("name")) );

                listUser.add(listTemp.get(i));

            }
        }

        BaseModel newobject = new BaseModel();
        newobject.put("showName", "TÀI KHOẢN KHÁC ...");
        newobject.put("phone","");
        newobject.put("displayName","");
        listUser.add(0, newobject);

        CustomBottomDialog.choiceListObject("ĐỔI SANG TÀI KHOẢN", "showName", listUser, new CallbackBaseModel() {
            @Override
            public void onResponse(BaseModel object) {
                showReloginDialog(object);

            }

            @Override
            public void onError() {

            }

        });

    }

    private void showReloginDialog(BaseModel user){

        CustomCenterDialog.showDialogRelogin(String.format("Đăng nhập vào tài khoản %s", user.getString("displayName")), user, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                loadCurrentData();
                tvFullname.setText(String.format("%s _ %s (%s)",User.getFullName(), User.getRole(), Distributor.getName()));

            }

            @Override
            public void onError(String error) {
                Util.showToast("Đăng nhập thất bại!");
            }
        });
    }

    private void doLogout() {
        CustomCenterDialog.alertWithCancelButton(null, String.format("Đăng xuất tài khoản %s",User.getFullName()) , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
//                CustomSQL.setString(Constants.USER_USERNAME,"");
//                CustomSQL.setString(Constants.USER_PASSWORD,"");
                if (result){
                    List<BaseModel> listUser = CustomSQL.getListObject(Constants.USER_LIST);
                    CustomSQL.clear();
                    CustomSQL.setListBaseModel(Constants.USER_LIST, listUser);

                    Transaction.gotoLoginActivityRight();
                }

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
                Util.showToast("Chưa hỗ trợ");
                CustomCenterDialog.showDialogSignature();


                break;

            case 3:
                Transaction.gotoScannerActivity();

                break;

            case 4:
                choiceSetupItem();

                break;

            case 5:
                Util.showToast("Chưa hỗ trợ");
                break;
        }
    }

    private void loadCurrentData() {
        SystemConnect.getAllData(new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                try {
                    Status.saveStatusList(new JSONArray(result.get(0).toString()));
                    District.saveDistrictList(new JSONArray(result.get(1).toString()));
                    ProductGroup.saveProductGroupList(new JSONArray(result.get(2).toString()));
                    Product.saveProductList(new JSONArray(result.get(3).toString()));
                    Province.saveProvinceList(new JSONArray(result.get(4).toString()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(String error) {

            }
        }, true);

    }

    private void choiceStatisticalItem(){
        CustomBottomDialog.choiceTwoOption(getString(R.string.icon_dashboard), "Thống kê hóa đơn",
                getString(R.string.icon_group), "Thống kê khách hàng"
                , new CustomBottomDialog.TwoMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        Transaction.gotoStatisticalActivity();

                    }

                    @Override
                    public void Method2(Boolean two) {
                        Transaction.gotoStatisticalCustomerActivity();

                    }

                });
    }

    private void choiceSetupItem(){
        CustomBottomDialog.choiceFourOption(getString(R.string.icon_group), "Nhân viên",
                getString(R.string.icon_product_group), "Nhóm sản phẩm",
                getString(R.string.icon_product), "Sản phẩm",
                getString(R.string.icon_comment), "Trạng thái", new CustomBottomDialog.FourMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        Util.showToast("Chưa hỗ trợ");

                    }

                    @Override
                    public void Method2(Boolean two) {
                        if (User.getRole().equals(Constants.ROLE_ADMIN)){
                            Transaction.gotoProductGroupActivity();
                        }

                    }

                    @Override
                    public void Method3(Boolean three) {
                        if (User.getRole().equals(Constants.ROLE_ADMIN)){
                            Transaction.gotoProductActivity();
                        }

                    }

                    @Override
                    public void Method4(Boolean four) {
                        if (User.getRole().equals(Constants.ROLE_ADMIN)){
                            Transaction.gotoStatusActivity();
                        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
