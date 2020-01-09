package wolve.dms.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.HomeAdapter;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomListList;
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
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_PERMISSION_LOCATION;

/**
 * Created by macos on 9/15/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter{
    private RecyclerView rvItems;
    private CTextIcon btnLogout, btnChangeUser;
    private TextView tvFullname , tvCash, tvProfit, tvMonth, tvHaveNewProduct, tvNumberTemp;
    private LinearLayout lnUser;
    private RelativeLayout lnTempGroup;
    private View line;

    private List<BaseModel> listPayment = new ArrayList<>();
    protected List<BaseModel> listTempBill = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    private Fragment mFragment;

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
        line = findViewById(R.id.home_change_line);
        tvCash = findViewById(R.id.home_cash);
        tvProfit = findViewById(R.id.home_profit);
        tvMonth = findViewById(R.id.home_month);
        tvHaveNewProduct = findViewById(R.id.home_new_product);
        lnTempGroup = findViewById(R.id.home_tempbill_group);
        tvNumberTemp = findViewById(R.id.home_tempbill_number);

    }

    @Override
    public void initialData() {
        checkPermission();

        tvFullname.setText(String.format("%s _ %s (%s)",User.getFullName(), User.getRole(), Distributor.getName()));
        btnChangeUser.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN) ? View.VISIBLE : View.GONE);
        line.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN) ? View.VISIBLE : View.GONE);

        if (CustomSQL.getBoolean(Constants.ON_MAP_SCREEN)) {
            Transaction.gotoMapsActivity();
        }

        if (!CustomSQL.getString(Constants.CUSTOMER_ID).equals("")){
            Transaction.gotoMapsActivity();
        }

        if (CustomSQL.getBoolean(Constants.LOGIN_SUCCESS)){
            loadCurrentData();
            CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, false);
        }
        checkNewProductUpdated();
        tvMonth.setText(String.format("***Tháng %s:", Util.CurrentMonthYear()));

    }

    private void createListItem() {
        HomeAdapter adapter = new HomeAdapter(HomeActivity.this);
        Util.createGridRV(rvItems, adapter, 3);

    }

    private void loadOverView(){
        List<BaseModel> params = new ArrayList<>();
        long start = Util.TimeStamp1(Util.Current01MonthYear());
        long end = Util.TimeStamp1(Util.Next01MonthYear());

        params.add(DataUtil.createPaymentParam(start,end ));
        SystemConnect.loadListObject(params, new CallbackCustomListList() {
            @Override
            public void onResponse(List<List<BaseModel>> results) {
                String user = User.getRole().equals(Constants.ROLE_ADMIN)? Constants.ALL_FILTER: User.getFullName();
                listPayment = DataUtil.groupCustomerPayment(DataUtil.convertPaymentList(user, results.get(0), start, end));

                tvCash.setText(String.format(    "Tiền mặt:     %s đ", Util.FormatMoney(DataUtil.sumMoneyFromList(listPayment, "paid"))));
                tvProfit.setText(String.format("Chênh lệch:     %s đ", Util.FormatMoney(DataUtil.sumMoneyFromList(listPayment, "billProfit"))));

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    @Override
    public void addEvent() {
        btnChangeUser.setOnClickListener(this);
        lnUser.setOnClickListener(this);
        tvHaveNewProduct.setOnClickListener(this);
        lnTempGroup.setOnClickListener(this);
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
                                doLogout(true);
                            }
                        });

                break;

            case R.id.home_new_product:
                loadCurrentData();
                break;

            case R.id.home_tempbill_group:
                changeFragment(new TempBillFragment() , true);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);

        if(Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);

        }else if(mFragment != null && mFragment instanceof TempBillFragment) {
            getSupportFragmentManager().popBackStack();

        }else {
            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Util.showToast("Ấn Back để thoát khỏi ứng dụng");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);

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
                listTemp.get(i).put("text", String.format("%s (%s)",listTemp.get(i).getString("displayName") ,
                                                new BaseModel(listTemp.get(i).getJsonObject("distributor")).getString("name")) );

                listUser.add(listTemp.get(i));

            }
        }

        BaseModel newobject = new BaseModel();
        newobject.put("text", "TÀI KHOẢN KHÁC ...");
        newobject.put("phone","");
        newobject.put("displayName","");
        listUser.add(0, newobject);

        CustomBottomDialog.choiceListObject("ĐỔI SANG TÀI KHOẢN", listUser,"text", new CallbackBaseModel() {
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


            }

            @Override
            public void onError(String error) {
                Util.showToast("Đăng nhập thất bại!");
            }
        });
    }

    private void doLogout(boolean showAlert) {
        if (showAlert){
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

        }else {
            List<BaseModel> listUser = CustomSQL.getListObject(Constants.USER_LIST);
            CustomSQL.clear();
            CustomSQL.setListBaseModel(Constants.USER_LIST, listUser);

            Transaction.gotoLoginActivityRight();
        }


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

                break;

            case 3:
                if (CustomSQL.getBoolean(Constants.IS_ADMIN)){
                    Transaction.gotoTestActivity();
                }else {
                    Util.showToast("Chưa hỗ trợ");
                }


                break;

            case 4:
                choiceSetupItem();

                break;

            case 5:
                if (CustomSQL.getBoolean(Constants.IS_ADMIN)){
                    Transaction.gotoDistributorActivity();
                }else {
                    Util.showToast("Chưa hỗ trợ");

                }

                break;
        }



    }

    private void loadCurrentData() {
        SystemConnect.getCategories(new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Status.saveStatusList(result.getJSONArray("Status"));
                ProductGroup.saveProductGroupList(result.getJSONArray("ProductGroup"));

                CustomSQL.setLong(Constants.LAST_PRODUCT_UPDATE, result.getLong("LastProductUpdate"));
                tvHaveNewProduct.setVisibility(View.GONE);

            }

            @Override
            public void onError(String error) {

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
                        Transaction.gotoUserActivity();

                    }

                    @Override
                    public void Method2(Boolean two) {
                        if (User.getRole().equals(Constants.ROLE_ADMIN)){
                            Transaction.gotoProductGroupActivity();
                        }

                    }

                    @Override
                    public void Method3(Boolean three) {
                        Transaction.gotoProductActivity();

//                        if (User.getRole().equals(Constants.ROLE_ADMIN)){
//
//                        }

                    }

                    @Override
                    public void Method4(Boolean four) {
                        if (User.getRole().equals(Constants.ROLE_ADMIN)){
                            Transaction.gotoStatusActivity();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkNewProductUpdated();

        if(mFragment != null && mFragment instanceof TempBillFragment){
            ((TempBillFragment) mFragment).reloadData();
        }


    }

    private void checkNewProductUpdated(){
        SystemConnect.getLastestProductUpdated(new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                listTempBill = DataUtil.listTempBill(DataUtil.array2ListObject(result.getString("tempBills")));
                if (result.getLong("lastProductUpdate") > CustomSQL.getLong(Constants.LAST_PRODUCT_UPDATE)){
                    tvHaveNewProduct.setVisibility(View.VISIBLE);

                    CustomCenterDialog.alertWithCancelButton("CÓ SẢN PHẨM MỚI",
                            "Đồng bộ danh mục sản phẩm với thiết bị của bạn",
                                    "ĐỒNG Ý",
                            "HỦY",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result){
                                        loadCurrentData();
                                    }
                                }
                            });
                }else {
                    tvHaveNewProduct.setVisibility(View.GONE);
                }

                if (listTempBill.size() >0){
                    lnTempGroup.setVisibility(View.VISIBLE);
                    tvNumberTemp.setText(String.valueOf(listTempBill.size()));
                }else {
                    lnTempGroup.setVisibility(View.GONE);
                }



            }

            @Override
            public void onError(String error) {

            }
        }, false);
    }

    @SuppressLint("WrongConstant")
    private void checkPermission(){
        Activity context = Util.getInstance().getCurrentActivity();
        if (PermissionChecker.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                    "Ứng dụng cần bạn đồng ý các quyền truy cập sau để tiếp tục",
                    "đồng ý",
                    "hủy",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                ActivityCompat.requestPermissions(context, new String[]{
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                        android.Manifest.permission.CALL_PHONE,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                        android.Manifest.permission.CAMERA,

                                }, Constants.REQUEST_PERMISSION);

                            }else {
                                doLogout(false);

                            }
                    }

            });

        }else {
            createListItem();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION) {
            if (grantResults.length > 0 ) {

                boolean hasDenied = false;
                for (int i=0; i<grantResults.length; i++){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        hasDenied = true;
                        break;
                    }
                }

                if (!hasDenied){
                    createListItem();

                }else {
                    Util.showToast("Cấp quyền truy cập không thành công!");
                    CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                            "Ứng dụng chưa được cấp quyền đầy đủ",
                            "Cấp lại",
                            "hủy",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result){
                                        checkPermission();

                                    }else {
                                        doLogout(false);

                                    }


                                }

                            });


                }


            }
        }

    }


}
