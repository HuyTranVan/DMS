package wolve.dms.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.Status;
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

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rvItems;
    private CircleImageView imgUser;
    private TextView tvFullname, tvCash, tvProfit, tvMonth, tvHaveNewProduct,
            tvNumberTemp, tvNumberTempImport, tvDistributor;
    private LinearLayout lnUser;
    private RelativeLayout lnTempGroup, lnTempImport;
    private SwipeRefreshLayout swipeRefreshLayout;

    protected List<BaseModel> listTempBill = new ArrayList<>();
    protected List<BaseModel> listTempImport = new ArrayList<>();
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
        rvItems = (RecyclerView) findViewById(R.id.home_rvitems);
        imgUser = findViewById(R.id.home_icon);
        tvFullname = findViewById(R.id.home_fullname);
        lnUser = findViewById(R.id.home_user);
        tvCash = findViewById(R.id.home_cash);
        tvMonth = findViewById(R.id.home_month);
        tvHaveNewProduct = findViewById(R.id.home_new_product);
        lnTempGroup = findViewById(R.id.home_tempbill_group);
        tvNumberTemp = findViewById(R.id.home_tempbill_number);
        swipeRefreshLayout = findViewById(R.id.home_refresh);
        lnTempImport = findViewById(R.id.home_tempimport_group);
        tvNumberTempImport = findViewById(R.id.home_tempimport_number);
        tvDistributor = findViewById(R.id.home_distributor);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initialData() {
        Util.getInstance().setCurrentActivity(this);
        checkPermission();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueDark));
        tvFullname.setText(String.format("%s _ %s", User.getFullName(), User.getCurrentRoleString()));
        tvDistributor.setText(Util.getStringIcon(Distributor.getName(), "    ", R.string.icon_home));
        tvMonth.setText(Util.getStringIcon(Util.CurrentMonthYear(), "     ", R.string.icon_calendar));

        if (!Util.checkImageNull(User.getImage())) {
            Glide.with(this).load(User.getImage()).centerCrop().into(imgUser);
        }

        if (CustomSQL.getBoolean(Constants.ON_MAP_SCREEN)) {
            if (checkWarehouse())
                Transaction.gotoMapsActivity();

        }

        if (CustomSQL.getBoolean(Constants.LOGIN_SUCCESS)) {
            loadCurrentData();
            CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, false);

        }
        loadPaymentByUser();
        checkNewProductUpdated(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                updateTempBillVisibility(list);

            }
        }, new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                updateTempImportVisibility(list);
            }
        });

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        List<NotificationChannel> list = notificationManager.getNotificationChannels();


    }

    private void createListItem() {
        HomeAdapter adapter = new HomeAdapter(HomeActivity.this);
        Util.createGridRV(rvItems, adapter, 3);

    }

    private void loadPaymentByUser() {
        long start = Util.TimeStamp1(Util.Current01MonthYear());
        long end = Util.TimeStamp1(Util.Next01MonthYear());

        BaseModel param = createGetParam( String.format(ApiUtil.PAYMENTS(), start, end), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                double paid = DataUtil.sumValueFromList(list, "paid");
                tvCash.setText(Util.getStringIcon(Util.FormatMoney(paid), "    ", R.string.icon_usd));
            }

            @Override
            public void onError(String error) {

            }
        }, true).execute();
//        params.add(DataUtil.createListPaymentParam(start, end));
//        SystemConnect.loadListObject(params, new CallbackCustomListList() {
//            @Override
//            public void onResponse(List<List<BaseModel>> results) {
//
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, true);
    }

    @Override
    public void addEvent() {
        lnUser.setOnClickListener(this);
        tvHaveNewProduct.setOnClickListener(this);
        lnTempGroup.setOnClickListener(this);
        lnTempImport.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_user:
                changeFragment(new UserOptionFragment(), true);

                break;

            case R.id.home_new_product:
                loadCurrentData();
                break;

            case R.id.home_tempbill_group:
                changeFragment(new TempBillFragment(), true);
                break;

            case R.id.home_tempimport_group:
                changeFragment(new TempImportFragment(), true);
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

        } else if (mFragment != null && mFragment instanceof UserOptionFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
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
                if (checkWarehouse())
                    Transaction.gotoMapsActivity();

                break;

            case 1:
                if (checkWarehouse())
                    Transaction.gotoStatisticalActivity();

                break;

            case 2:
                Transaction.gotoWarehouseActivity();

                break;

            case 3:
                choiceSetupItem();

                break;

            case 4:
                Util.showToast("Chưa hỗ trợ");

                break;

            case 5:
                Util.showToast("Chưa hỗ trợ");

                break;
        }


    }

    private void loadCurrentData() {
        BaseModel param = createGetParam(ApiUtil.CATEGORIES(), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                Status.saveStatusList(result.getJSONArray("Status"));
                ProductGroup.saveProductGroupList(result.getJSONArray("ProductGroup"));

                CustomSQL.setLong(Constants.LAST_PRODUCT_UPDATE, result.getLong("LastProductUpdate"));
                tvHaveNewProduct.setVisibility(View.GONE);

            }

            @Override
            public void onError(String error) {

            }
        }, false).execute();


    }

    private void choiceSetupItem() {
        CustomBottomDialog.choiceListObject("cài đặt",
                Constants.homeSettingSetup(),
                "text",
                new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        switch (object.getInt("position")) {
                            case 0:
                                if (CustomSQL.getBoolean(Constants.IS_ADMIN)) {
                                    Transaction.gotoDistributorActivity();
                                }
                                break;

                            case 1:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoUserActivity(false);
                                }
                                break;

                            case 2:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoProductGroupActivity();
                                }
                                break;

                            case 3:
                                Transaction.gotoProductActivity();
                                break;

                            case 4:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoStatusActivity();
                                }

                                break;


                        }

                    }
                }, null);

    }

//    protected void logout(){
//        UserConnect.Logout(new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                if (result.getBoolean("success")) {
//                    Util.deleteAllImageExternalStorage();
//
//                    CustomSQL.clear();
//                    Util.showToast("Đăng xuất thành công");
//                    Transaction.gotoLoginActivityRight();
//
//                } else {
//                    Util.showSnackbar("Đăng xuất thất bại", null, null);
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, true);
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);
        checkNewProductUpdated(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                updateTempBillVisibility(list);

                if (mFragment != null && mFragment instanceof TempBillFragment) {
                    ((TempBillFragment) mFragment).reloadData();

                }
            }
        }, new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                updateTempImportVisibility(list);
            }
        });


    }

    private void checkNewProductUpdated(CallbackListObject listenertempbill, CallbackListObject listenertempimport) {
        BaseModel param = createGetParam(ApiUtil.PRODUCT_LASTEST(), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                swipeRefreshLayout.setRefreshing(false);
                listenertempbill.onResponse(DataUtil.listTempBill(DataUtil.array2ListObject(result.getString("tempBills"))));
                listenertempimport.onResponse(DataUtil.filterListTempImport(DataUtil.array2ListObject(result.getString("tempImport"))));

                if (result.getLong("lastProductUpdate") > CustomSQL.getLong(Constants.LAST_PRODUCT_UPDATE)) {
                    tvHaveNewProduct.setVisibility(View.VISIBLE);

                    CustomCenterDialog.alertWithCancelButton("CÓ SẢN PHẨM MỚI",
                            "Đồng bộ danh mục sản phẩm với thiết bị của bạn",
                            "ĐỒNG Ý",
                            "HỦY",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result) {
                                        loadCurrentData();
                                    }
                                }
                            });
                } else {
                    tvHaveNewProduct.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(String error) {

            }
        }, false).execute();
//        SystemConnect.getLastestProductUpdated(new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, false);
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
                PermissionChecker.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){


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
                                        android.Manifest.permission.CALL_PHONE,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                        android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.READ_CONTACTS,
                                        android.Manifest.permission.WRITE_CONTACTS

                                }, Constants.REQUEST_PERMISSION);

                            } else {
                                logout(null);

                            }
                        }

                    });

        } else {
            createListItem();
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
                    createListItem();

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
        checkNewProductUpdated(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                updateTempBillVisibility(list);
            }
        }, new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                updateTempImportVisibility(list);
            }
        });
        loadPaymentByUser();
    }

    protected boolean checkWarehouse() {
        BaseModel user = User.getCurrentUser();
        if (User.getCurrentUser().getInt("warehouse_id") == 0) {
            CustomCenterDialog.alert(null, "Cập nhật thông tin kho hàng nhân viên để tiếp tục thao tác", "đồng ý");
            return false;
        } else {
            return true;
        }
    }


}
