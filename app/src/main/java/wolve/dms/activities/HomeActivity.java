package wolve.dms.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.adapter.HomeAdapter;
import wolve.dms.adapter.WarehouseAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/15/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter, SwipeRefreshLayout.OnRefreshListener, CallbackObject {
    private RecyclerView rvItems;
    private CircleImageView imgUser;
    private TextView tvFullname, tvRole, tvCash, tvMonth, tvHaveNewProduct,
            tvNumberTemp, tvNumberTempImport, tvWarehouseName, tvInventoryValue;
    private LinearLayout lnUser, lnCash, lnInventory;
    private RelativeLayout lnTempGroup, lnTempImport;
    private SwipeRefreshLayout swipeRefreshLayout;

    protected List<BaseModel> listTempBill = new ArrayList<>();
    protected List<BaseModel> listTempImport = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    private Fragment mFragment;
    private HomeAdapter adapterHome;
    private BaseModel tempWarehouse;

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
        rvItems = (RecyclerView) findViewById(R.id.home_rvitems);
        imgUser = findViewById(R.id.home_icon);
        tvFullname = findViewById(R.id.home_fullname);
        tvRole = findViewById(R.id.home_role);
        lnUser = findViewById(R.id.home_user);
        tvCash = findViewById(R.id.home_cash);
        tvMonth = findViewById(R.id.home_month);
        tvHaveNewProduct = findViewById(R.id.home_new_product);
        lnTempGroup = findViewById(R.id.home_tempbill_group);
        tvNumberTemp = findViewById(R.id.home_tempbill_number);
        swipeRefreshLayout = findViewById(R.id.home_refresh);
        lnTempImport = findViewById(R.id.home_tempimport_group);
        tvNumberTempImport = findViewById(R.id.home_tempimport_number);
        lnCash = findViewById(R.id.home_cash_parent);
        lnInventory = findViewById(R.id.home_inventory_parent);
        tvInventoryValue = findViewById(R.id.home_inventory_value);
        tvWarehouseName = findViewById(R.id.home_warehouse_name);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initialData() {
        Util.getInstance().setCurrentActivity(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueDark));
        tvFullname.setText( User.getFullName());
        String role = User.getCurrentRoleString();
        tvRole.setText(  role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase());
        tvMonth.setText(String.format("Thực thu %s:", Util.CurrentMonthYear()));
        if (!Util.checkImageNull(User.getImage())) {
            Glide.with(this).load(User.getImage()).centerCrop().into(imgUser);
        }

        checkPermission();

    }

    private void createMainItem() {
        adapterHome = new HomeAdapter(HomeActivity.this, new WarehouseAdapter.CallbackWarehouseOption() {
            @Override
            public void onInfo(BaseModel objInfo) {

            }

            @Override
            public void onInventory(BaseModel objInventory) {
                CustomSQL.setBaseModel(Constants.CURRENT_WAREHOUSE, User.getCurrentUser().getBaseModel("warehouse"));
                changeFragment(new InventoryDetailFragment(), true);

            }

            @Override
            public void onReturn(BaseModel objReturn){
                CustomSQL.setBaseModel(Constants.CURRENT_WAREHOUSE, User.getCurrentUser().getBaseModel("warehouse"));
                changeFragment(new ImportReturnFragment(), true);

            }
        });
        Util.createGridRV(rvItems, adapterHome, 3);

        if (CustomSQL.getBoolean(Constants.LOGIN_SUCCESS)) {
            CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, false);

            swipeRefreshLayout.setRefreshing(true);
            loadCurrentData(new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    checkNewProductUpdated(null, 0, true);
                    suggestChangePassword();

                }

            },2);


        }else {
            checkNewProductUpdated(null, 1, true);

        }

    }


    @Override
    public void addEvent() {
        lnUser.setOnClickListener(this);
        tvHaveNewProduct.setOnClickListener(this);
        lnTempGroup.setOnClickListener(this);
        lnTempImport.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        lnInventory.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_user:
                Transaction.gotoSettingActivity();

                break;

            case R.id.home_new_product:
                loadCurrentData(new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result){
                            Util.showToast("Đồng bộ thành công");
                        }
                    }
                }, 1);
                break;

            case R.id.home_tempbill_group:
                changeFragment(new TempBillFragment(), true);
                break;

            case R.id.home_tempimport_group:
                changeFragment(new TempImportFragment(), true);
                break;

            case R.id.home_cash_parent:
                Transaction.gotoStatisticalActivity(User.getCurrentUserString());

                break;

            case R.id.home_inventory_parent:
                Transaction.gotoImportActivity(tempWarehouse);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);

        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        } else if (mFragment != null && mFragment instanceof TempBillFragment) {
            TempBillFragment fragment = (TempBillFragment) mFragment;
            if (((TempBillFragment) mFragment).checkSelected()) {
                ((TempBillFragment) mFragment).closeSelected();

            } else {
                getSupportFragmentManager().popBackStack();

            }

        } else if (mFragment != null && mFragment instanceof TempImportFragment) {
            getSupportFragmentManager().popBackStack();

        }else if (mFragment != null && mFragment instanceof NewUpdateDistributorFragment) {
            getSupportFragmentManager().popBackStack();

        }else if (mFragment != null && mFragment instanceof InventoryDetailFragment) {
            getSupportFragmentManager().popBackStack();

        }else if (mFragment != null && mFragment instanceof ImportReturnFragment) {
            getSupportFragmentManager().popBackStack();

        }else {
            if (doubleBackToExitPressedOnce) {
                this.finish();
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

    @Override
    public void onRespone(String data, int position) {
        switch (position) {
            case 0:
                if (User.checkUserWarehouse())
                    Transaction.gotoMapsActivity();

                break;

            case 1:
                Transaction.gotoStatisticalActivity(Util.isAdmin()? "" : User.getCurrentUserString());

                break;

            case 2:
                Transaction.gotoImportActivity(User.getCurrentUser().getBaseModel("warehouse"));
                    //Transaction.gotoWarehouseActivity();

                break;

            case 3:
                if (Util.isAdmin()){
                    Transaction.gotoAccountActivity();
                }else {
                    Util.showToast("Không hỗ trợ");
                }


                break;

            case 4:
                choiceSetupItem();

                break;

            case 5:
                if (CustomSQL.getBoolean(Constants.IS_ADMIN)) {
                    NewUpdateDistributorFragment groupFragment = new NewUpdateDistributorFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DISTRIBUTOR, Distributor.getObjectString());
                    changeFragment(groupFragment, bundle, true);

                }

                break;
        }


    }

    private void loadCurrentData(CallbackBoolean listener, int load) {
        BaseModel param = createGetParam(ApiUtil.CATEGORIES(), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                ProductGroup.saveProductGroupList(result.getJSONArray("ProductGroup"));
                CustomSQL.setString(Constants.DISTRIBUTOR, result.getString("Distributor"));

                CustomSQL.setLong(Constants.LAST_PRODUCT_UPDATE, result.getLong("LastProductUpdate"));
                tvHaveNewProduct.setVisibility(View.GONE);

                if (listener != null){
                    listener.onRespone(true);
                }

            }

            @Override
            public void onError(String error) {

            }
        }, load).execute();


    }

    private void choiceSetupItem() {
        CustomBottomDialog.choiceListObject("danh mục",
                Constants.homeSettingSetup(),
                "text",
                new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        switch (object.getInt("position")) {
                            case 0:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoUserActivity();
                                }
                                break;

                            case 1:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoWarehouseActivity();
                                }
                                break;

                            case 2:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoCashFlowTypeActivity();
                                }

                                break;

                            case 3:
                                //if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoProductGroupActivity();
                                //}
                                break;

                            case 4:
                                Transaction.gotoDistributorActivity();

                                break;


                        }

                    }
                }, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);
        checkNewProductUpdated(new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    if (mFragment != null && mFragment instanceof TempBillFragment) {
                        ((TempBillFragment) mFragment).reloadData();

                    }else if (mFragment != null && mFragment instanceof TempImportFragment){
                        ((TempImportFragment) mFragment).reloadData();
                    }

                }
            }
        }, 1, false);

    }

    private void checkNewProductUpdated(CallbackBoolean listener, int load, boolean redirect) {
        long start = Util.TimeStamp1(Util.Current01MonthYear());
        long end = Util.TimeStamp1(Util.Next01MonthYear());

        BaseModel param = createGetParam(String.format(ApiUtil.PRODUCT_LASTEST() , start, end), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                swipeRefreshLayout.setRefreshing(false);
                adapterHome.updateInventoryDetail(result.getInt("inventories"));
                adapterHome.updateWaitingListDetail(result.getInt("countCustomerWaiting"));
                updateTempBillVisibility(DataUtil.listTempBill(DataUtil.array2ListObject(result.getString("tempBills"))));
                updateTempImportVisibility(DataUtil.filterListTempImport(DataUtil.array2ListObject(result.getString("tempImport"))));
                tvCash.setText(String.format("%s đ", Util.FormatMoney(result.getDouble("paymentInMonth"))));
                lnCash.setOnClickListener(result.getDouble("paymentInMonth") >0.0 ? HomeActivity.this: null );
                updateTempWahouseInventory(result.getBaseModel("tempwarehouse"));
                if (result.getLong("lastProductUpdate") != null && result.getLong("lastProductUpdate") > CustomSQL.getLong(Constants.LAST_PRODUCT_UPDATE)) {
                    tvHaveNewProduct.setVisibility(View.VISIBLE);
                    CustomCenterDialog.alertWithButtonCanceled("Thông tin thay đổi",
                            "Cài đặt hệ thống vừa được điều chỉnh! \n Đồng bộ lại hệ thống với thiết bị của bạn",
                            "ĐỒNG Ý",
                            true,
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result){
                                        loadCurrentData(null, 1);
                                    }
                                }
                            });

                } else {
                    tvHaveNewProduct.setVisibility(View.GONE);
                }

                if (listener != null){
                    listener.onRespone(true);
                }

                if (redirect){
                    redirectOtherScreen();
                }

            }

            @Override
            public void onError(String error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, load).execute();

    }

    private void updateTempBillVisibility(List<BaseModel> list) {
        listTempBill = list;
        if (list.size() > 0) {
            lnTempGroup.setVisibility(View.VISIBLE);
            tvNumberTemp.setText(String.valueOf(list.size()));
        } else {
            lnTempGroup.setVisibility(View.GONE);
        }

        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);
        if (mFragment != null && mFragment instanceof TempBillFragment) {
            if (((TempBillFragment) mFragment).checkSelected()) {
                ((TempBillFragment) mFragment).closeSelected();

            } else {
                ((TempBillFragment) mFragment).reloadData();

            }

        }

    }

    protected void updateTempImportVisibility(List<BaseModel> list) {
        listTempImport = list;
        if (list.size() > 0) {
            lnTempImport.setVisibility(View.VISIBLE);
            tvNumberTempImport.setText(String.valueOf(list.size()));
        } else {
            lnTempImport.setVisibility(View.GONE);
        }
        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);
        if (mFragment != null && mFragment instanceof TempImportFragment) {
            ((TempImportFragment) mFragment).reloadData();

        }
    }

    @SuppressLint("WrongConstant")
    private void checkPermission() {
        Activity context = Util.getInstance().getCurrentActivity();
        if (PermissionChecker.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                //PermissionChecker.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                //PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED||
                //PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){


            CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                    "Ứng dụng cần bạn đồng ý các quyền truy cập sau để tiếp tục",
                    "đồng ý",
                    "hủy",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result) {
                                ActivityCompat.requestPermissions(context, new String[]{
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                        //android.Manifest.permission.CALL_PHONE,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                        android.Manifest.permission.CAMERA
                                        //android.Manifest.permission.READ_CONTACTS,
                                        //android.Manifest.permission.WRITE_CONTACTS

                                }, Constants.REQUEST_PERMISSION);

                            } else {
                                logout(null);

                            }
                        }

                    });

        } else {
            createMainItem();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION) {
            if (grantResults.length > 0) {

                boolean hasDenied = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        hasDenied = true;
                        break;
                    }
                }

                if (!hasDenied) {
                    createMainItem();

                } else {
                    Util.showToast("Cấp quyền truy cập không thành công!");
                    CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                            "Ứng dụng chưa được cấp quyền đầy đủ",
                            "Cấp lại",
                            "hủy",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result) {
                                        checkPermission();

                                    } else {
                                        logout(null);
                                    }

                                }

                            });

                }

            }
        }

    }


    @Override
    public void onRefresh() {
        checkNewProductUpdated(null, 0, false);

    }

    private void redirectOtherScreen(){
        if (!CustomSQL.getString(Constants.CUSTOMER_ID).equals("")){
            BaseModel param = createGetParam(ApiUtil.CUSTOMER_GETDETAIL() + CustomSQL.getString(Constants.CUSTOMER_ID), false);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    BaseModel customer = DataUtil.rebuiltCustomer(result, false);
                    CustomSQL.setBaseModel(Constants.CUSTOMER, customer);
                    CustomSQL.removeKey(Constants.CUSTOMER_ID);
                    CustomSQL.removeKey(Constants.ON_MAP_SCREEN);
                    Transaction.gotoCustomerActivity();

                }

                @Override
                public void onError(String error){

                }
            }, 1).execute();


        }else if (CustomSQL.getBoolean(Constants.ON_MAP_SCREEN)) {
            if (User.checkUserWarehouse())
                Transaction.gotoMapsActivity();

        }

    }

    @Override
    public void onResponse(BaseModel object) {
        adapterHome.reloadItem();
    }

    private void suggestChangePassword(){
        if (User.getCurrentUser().hasKey("password") && CustomSQL.getString(Constants.USER_PASSWORD).equals("Aa123456")){
            CustomCenterDialog.alertWithCancelButton("Đổi mật khẩu!",
                    "Bạn đang sử dụng mật khẩu mặc định. Vui lòng đổi sang mật khẩu khác để bảo mật hơn!!!",
                    "đồng ý",
                    "lúc khác",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                changePassword();
                            }
                        }
                    });

        }else {
            CustomSQL.removeKey(Constants.USER_PASSWORD);
        }
    }

    private void openFragmentNewDepot(int warehouse_id){
        BaseModel param = createGetParam(ApiUtil.WAREHOUSE_DETAIL() + warehouse_id, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                NewUpdateWarehouseFragment depotFragment = new NewUpdateWarehouseFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DEPOT, result.BaseModelstoString());
                changeFragment(depotFragment, bundle, true);
            }

            @Override
            public void onError(String error) {

            }
        },1).execute();





    }

    private void openFragmentImportReturn(BaseModel warehouse, BaseModel temptWarehouse) {
        ImportReturnFragment importReturnFragment = new ImportReturnFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.WAREHOUSE, warehouse.BaseModelstoString());
        bundle.putString(Constants.TEMPWAREHOUSE, temptWarehouse.BaseModelstoString());
        changeFragment(importReturnFragment, bundle, true);
    }

    private void openFragmentInventoryEdit(BaseModel temptWarehouse) {
        InventoryDetailFragment importReturnFragment = new InventoryDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEMPWAREHOUSE, temptWarehouse.BaseModelstoString());
        changeFragment(importReturnFragment, bundle, true);
    }

    private void updateTempWahouseInventory(BaseModel warehouse){
        tempWarehouse = warehouse;
        if (Util.isAdmin()){
            lnInventory.setVisibility(View.VISIBLE);
            tvWarehouseName.setText(String.format("%s (%s)",
                    warehouse.getString("name"),
                    warehouse.getInt("quantity")));
            tvInventoryValue.setText(String.format("%s đ", Util.FormatMoney(warehouse.getDouble("total"))));

        }else {
            lnInventory.setVisibility(View.GONE);

        }
    }

}
