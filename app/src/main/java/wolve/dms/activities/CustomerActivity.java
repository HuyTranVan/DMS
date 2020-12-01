package wolve.dms.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import wolve.dms.R;
import wolve.dms.adapter.Customer_ViewpagerAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.CustomGetPostListMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, CallbackObject {
    private ImageView btnBack;
    protected Button btnSubmit;
    private TextView tvCheckInStatus, tvTime, tvTrash, tvPrint;
    protected TextView tvTitle, tvAddress, tvDebt, tvPaid, tvTotal, tvBDF, btnShopCart, tvFilter, tvFilterIcon;
    private FrameLayout coParent;
    //private RecyclerView rvFilterTitle;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HorizontalScrollView scrollOverView;
    private SmoothProgressBar smLoading;
    private RelativeLayout rlStatusGroup;
    private LinearLayout lnFilter;

    protected BaseModel currentCustomer = null;
    protected List<BaseModel> listCheckins = new ArrayList<>();
    protected List<BaseModel> listBills = new ArrayList<>();
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    protected List<BaseModel> listDebtBill = new ArrayList<>();
    private long countTime;
    private Fragment mFragment;
    protected List<String> mYears = new ArrayList<>();
    private Customer_ViewpagerAdapter pageAdapter;
    protected BaseModel currentBill, tempBill = null;
    protected Double currentDebt = 0.0;
    private Handler mHandlerUpdateCustomer = new Handler();
    private boolean haschange = false;
    private int currentPosition = 0;
    private long start = Util.TimeStamp1(Util.Current01MonthYear());
    private int currentCheckedTime = 1;

    protected CustomerBillsFragment billsFragment;
    protected CustomerInfoFragment infoFragment;
    protected CustomerProductFragment productFragment;
    protected CustomerPaymentFragment paymentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            FitScrollWithFullscreen.assistActivity(this, 1);

        }

    }

    Thread threadShowTime = new Thread() {
        @Override
        public void run() {
            try {
                while (!threadShowTime.isInterrupted()) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTime.setText(Util.MinuteString(countTime));
                            countTime += 1000;

                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };

    @Override
    public int getResourceLayout() {
        return R.layout.activity_customer;
    }

    @Override
    public int setIdContainer() {
        return R.id.customer_parent;
    }

    @Override
    public void findViewById() {
        coParent = (FrameLayout) findViewById(R.id.customer_parent);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            coParent.setPadding(0, 0, 0, Util.getNavigationBarHeight());
        }

        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnShopCart = (TextView) findViewById(R.id.customer_shopcart);
        tvTrash = (TextView) findViewById(R.id.icon_more);
        tvTitle = (TextView) findViewById(R.id.customer_title);
        tvDebt = (TextView) findViewById(R.id.customer_debt);
        tvCheckInStatus = findViewById(R.id.customer_checkin_status);
        tvTime = findViewById(R.id.customer_checkin_time);
        rlStatusGroup = findViewById(R.id.customer_checkin_status_group);
        viewPager = findViewById(R.id.customer_viewpager);
        tabLayout = findViewById(R.id.customer_tabs);
        //tvDebt = findViewById(R.id.customer_debt);
        tvPaid = findViewById(R.id.customer_paid);
        tvTotal = findViewById(R.id.customer_total);
        tvBDF = findViewById(R.id.customer_bdf);
        scrollOverView = findViewById(R.id.customer_overview);
        smLoading = findViewById(R.id.customer_loading);
        tvAddress = findViewById(R.id.customer_address);
        lnFilter = findViewById(R.id.customer_filter);
        tvFilter = findViewById(R.id.customer_filter_text);
        tvPrint = findViewById(R.id.customer_print);
        tvFilterIcon = findViewById(R.id.customer_filter_icon_close);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
        tvFilterIcon.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
        tvDebt.setOnClickListener(this);
        tvPaid.setOnClickListener(this);
        tvPaid.setOnLongClickListener(this);
        lnFilter.setOnClickListener(this);
        Util.textViewEvent(tvFilter, new CallbackString() {
            @Override
            public void Result(String s) {
                if (!s.equals("")) {
                    tvFilter.setPadding(Util.convertSdpToInt(R.dimen._15sdp), 0, 0, 0);
                } else {
                    tvFilter.setPadding(0, 0, 0, 0);
                }
            }
        });


    }

    @Override
    public void initialData() {
        countTime = Util.CurrentTimeStamp() - CustomSQL.getLong(Constants.CHECKIN_TIME);
        CustomSQL.removeKey(Constants.CURRENT_DISTANCE);

        tvTrash.setVisibility(User.getCurrentRoleId() == Constants.ROLE_ADMIN ? View.VISIBLE : View.GONE);

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

        updateView(CustomSQL.getBaseModel(Constants.CUSTOMER));
        checkLocation(new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    billsFragment.updateTempBill();

                }
            }
        });

    }

    private void updateView(BaseModel customer) {
        currentCustomer = customer;
        //createRVFilter();

        tvTitle.setText(String.format("%s %s", Constants.shopName[currentCustomer.getInt("shopType")], currentCustomer.getString("signBoard")));

        if (currentCustomer.hasKey(Constants.TEMPBILL)) {
            tempBill = currentCustomer.getBaseModel(Constants.TEMPBILL);
            viewPager.setCurrentItem(1, true);

        } else {
            tempBill = null;
        }

        listDebtBill = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.DEBTS)));
        listBills = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.BILLS)));
        listBillDetail = new ArrayList<>(DataUtil.getAllBillDetail(listBills));
        listCheckins = new ArrayList<>(DataUtil.array2ListBaseModel(currentCustomer.getJSONArray(Constants.CHECKINS)));

        updateBillTabNotify(tempBill != null ? true : false, listBills.size());

        updateOverview(listBills);

        currentDebt = Util.getTotalDebt(listBills);

        infoFragment.reloadInfo();
        billsFragment.updateList();
        productFragment.updateList();
        paymentFragment.updateList();

    }

    protected void updateOverview(List<BaseModel> list) {
        setFilterVisibility();
        setPrintIconVisibility();
        if (list.size() > 0) {
            BaseModel object = Util.getTotal(list);
            tvTotal.setText(String.format("Tổng: %s", Util.FormatMoney(object.getDouble("total"))));
            tvDebt.setText(String.format("Nợ: %s", Util.FormatMoney(object.getDouble("debt"))));
            tvPaid.setText(String.format("Trả: %s", Util.FormatMoney(object.getDouble("paid"))));
            tvBDF.setText(String.format("BDF:%s ", DataUtil.defineBDFPercent(listBillDetail)) + "%");

        } else {
            tvTotal.setText("...");
            tvDebt.setText("...");
            tvPaid.setText("...");
            tvBDF.setText("...");
        }
    }

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new Customer_ViewpagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerInfoFragment.class.getName()), "Thông tin", 0);
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerBillsFragment.class.getName()), "Hóa đơn", 0);
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerProductFragment.class.getName()), "Sản Phẩm", 0);
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerPaymentFragment.class.getName()), "Tiền mặt", 0);

        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(4);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Util.hideKeyboard(tabLayout);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                setPrintIconVisibility();
                setFilterVisibility();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void setupTabLayout(TabLayout tabLayout) {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pageAdapter.getTabView(i));
        }
        tabLayout.requestFocus();
    }

    protected void updateBillTabNotify(boolean hasTempBill, int billAmount) {
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.setCustomView(null);
        tab.setCustomView(pageAdapter.getNotifyBaged(billAmount, hasTempBill));

    }

    private void addYearToList(String year) {
        if (mYears.size() == 0) {
            mYears.add(0, Constants.ALL_FILTER);
        }
        if (!mYears.get(mYears.size() - 1).equals(year)) {
            mYears.add(year);
        }
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();

                break;

            case R.id.icon_more:
                if (listBills.size() > 0) {
                    Util.showToast("Không thể xóa khách hàng đã có hóa đơn");

                } else if (listCheckins.size() > 0) {
                    Util.showToast("Không thể xóa khách hàng có checkin");

                } else {
                    deleteCustomer();

                }

                break;

            case R.id.customer_shopcart:
                if (tempBill != null) {
                    Util.showSnackbar("Có 1 đơn hàng chưa hoàn thành nên không thể tạo hóa đơn mới", null, null);

                } else {
                    CustomSQL.setListBaseModel(Constants.BILL_DETAIL, listBillDetail);
                    openShopCartScreen();
                }

                break;

            case R.id.customer_debt:
                if (currentDebt > 0) {
                    showDialogPayment();
                }

                break;

            case R.id.customer_paid:
                if (listBills.size() > 0) {
                    CustomCenterDialog.alert("Chiết khấu", "Nhấn giữ vào biểu tường này để nhập số tiền chiết khấu cho khách hàng", "đồng ý");

                }

                break;

            case R.id.customer_filter:
                Bundle bundle = new Bundle();
                bundle.putInt("position", currentCheckedTime);
                bundle.putLong("start_time", start);

                changeFragment(new DatePickerFragment(), bundle,true);

                break;

            case R.id.customer_filter_icon_close:
                if (tvFilter.getVisibility() == View.GONE) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("position", currentCheckedTime);
                    bundle1.putLong("start_time", start);
                    changeFragment(new DatePickerFragment(), bundle1, true);

                } else {
                    tvFilterIcon.setText(Util.getIcon(R.string.icon_calendar));
                    tvFilter.setVisibility(View.GONE);
                    filterBillByRange(0, 0);
                }

                break;

            case R.id.customer_print:
                printDebtBills();

                break;


        }
    }

    protected void returnPreviousScreen(BaseModel customer) {
        Intent returnIntent = new Intent();
        customer.removeKey("bills");
        returnIntent.putExtra(Constants.CUSTOMER, customer != null ? customer.BaseModelstoString() : null);
        returnIntent.putExtra(Constants.RELOAD_DATA, haschange);
        setResult(Constants.RESULT_CUSTOMER_ACTIVITY, returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.nothing, R.anim.slide_down);
        Util.getInstance().getCurrentActivity().finish();

    }

    protected void openShopCartScreen() {
        Transaction.gotoShopCartActivity(DataUtil.convertListObject2Array(listDebtBill).toString());

    }

    private void deleteCustomer() {
        CustomCenterDialog.alertWithCancelButton(null, String.format("Xóa khách hàng %s", tvTitle.getText().toString()), "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                BaseModel param = createGetParam(ApiUtil.CUSTOMER_DELETE() + currentCustomer.getString("id"), false);
                new GetPostMethod(param, new NewCallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result, List<BaseModel> list) {
                        currentCustomer.put("deleted", true);
                        CustomSQL.setString(Constants.CUSTOMER, currentCustomer.BaseModelstoString());

                        Util.showToast("Xóa thành công!");
                        returnPreviousScreen(currentCustomer);
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, 1).execute();

            }
        });

    }

    @Override
    public void onBackPressed() {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.customer_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (mFragment != null && mFragment instanceof CustomerReturnFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (mFragment != null && mFragment instanceof CustomerEditMapFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (mFragment != null && mFragment instanceof CustomerCheckinFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (mFragment != null && mFragment instanceof DatePickerFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (tabLayout.getSelectedTabPosition() != 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();

        } else {
            returnPreviousScreen(currentCustomer);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Util.getInstance().setCurrentActivity(this);
        if (requestCode == Constants.RESULT_SHOPCART_ACTIVITY) {
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.SHOP_CART_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)) {
                reloadCustomer(CustomSQL.getBaseModel(Constants.CUSTOMER).getString("id"));

            }

        } else if (requestCode == Constants.RESULT_PRINTBILL_ACTIVITY) {
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.PRINT_BILL_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)) {
                reloadCustomer(CustomSQL.getBaseModel(Constants.CUSTOMER).getString("id"));

            }

        } else {
            onResume();

        }

    }

    protected void saveCustomerToLocal(String key, Object value) {
        currentCustomer.put(key, value);

        mHandlerUpdateCustomer.removeCallbacks(mUpdateTask);
        mHandlerUpdateCustomer.postDelayed(mUpdateTask, 1000);

    }

    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            submitCustomer(currentCustomer, listCheckins.size(), new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    CustomSQL.setBaseModel(Constants.CUSTOMER, currentCustomer);
                }
            });

        }
    };

    protected void submitCustomer(BaseModel customer, int checkincount, CallbackBoolean listener) {
        smLoading.setVisibility(View.VISIBLE);

        if (customer.getInt("checkinCount") != checkincount) {
            customer.put("checkinCount", checkincount);
        }

        String param = String.format(ApiUtil.CUSTOMER_CREATE_PARAM, String.format("id=%s&", customer.getString("id")),
                Util.encodeString(customer.getString("name")),//name
                Util.encodeString(customer.getString("signBoard")),//signBoard
                Util.encodeString(customer.getString("address")), //address
                Util.encodeString(customer.getString("phone")), //phone
                Util.encodeString(customer.getString("street")), //street
                Util.encodeString(customer.getString("note")), //note
                Util.encodeString(customer.getString("district")), //district
                Util.encodeString(customer.getString("province")), //province
                customer.getDouble("lat"), //lat
                customer.getDouble("lng"), //lng
                customer.getInt("volumeEstimate"), //province
                Util.encodeString(customer.getString("shopType")), //shopType
                customer.getInt("status_id"), //currentStatusId
                Distributor.getDistributorId(),//DistributorId
                customer.getInt("checkinCount")

        );

        new GetPostMethod(createPostParam(ApiUtil.CUSTOMER_NEW(), param, false, false),
                new NewCallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result, List<BaseModel> list) {
                        smLoading.setVisibility(View.INVISIBLE);
                        listener.onRespone(true);
                    }

                    @Override
                    public void onError(String error) {
                        smLoading.setVisibility(View.INVISIBLE);
                        Util.showSnackbar("Không thể lưu thay đổi của khách hàng", null, null);
                    }
                }, 0).execute();

    }

    protected void openReturnFragment(BaseModel bill) {
        currentBill = bill;
        changeFragment(new CustomerReturnFragment(), true);

    }

    protected void openEditMapFragment() {
        changeFragment(new CustomerEditMapFragment(), true);

    }

    protected void reloadCustomer(String id) {
        haschange = true;
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_GETDETAIL() + id, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                BaseModel customer = DataUtil.rebuiltCustomer(result, false);
                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);
                updateView(customer);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

//        CustomerConnect.GetCustomerDetail(id, new CallbackCustom() {
//            @Override
//            public void onResponse(final BaseModel result) {
//                BaseModel customer = DataUtil.rebuiltCustomer(result, false);
//                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);
//                updateView(customer);
//
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.showSnackbarError(error);
//            }
//        }, true, false);

    }

    private void showDialogPayment() {
        CustomCenterDialog.showDialogPayment("THANH TOÁN CÁC HÓA ĐƠN NỢ",
                listDebtBill,
                0.0,
                true,
                new CallbackListCustom() {
                    @Override
                    public void onResponse(List result) {
                        postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"), result, false));

                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }

    private void showDiscountPayment() {
        CustomCenterDialog.showDialogDiscountPayment("TRỪ TIỀN CHIẾT KHẤU KHÁCH HÀNG",
                "Nhập số tiền chiết khấu",
                currentCustomer.getDouble("paid"),
                new CallbackDouble() {
                    @Override
                    public void Result(Double d) {
                        BaseModel param = createPostParam(ApiUtil.PAY_NEW(),
                                String.format(ApiUtil.PAY_PARAM,
                                        currentCustomer.getInt("id"),
                                        String.valueOf(Math.round(d * -1)),
                                        0,
                                        User.getId(),
                                        Util.encodeString("Trả chiết khấu"),
                                         0,
                                        User.getId()),
                                false,
                                false);

//                                DataUtil.createPostPaymentParam(currentCustomer.getInt("id"),
//                                User.getId(),
//                                d * -1,
//                                0,
//                                "Trả chiết khấu",
//                                false);

                        new GetPostMethod(param, new NewCallbackCustom() {
                            @Override
                            public void onResponse(BaseModel result, List<BaseModel> list) {
                                reloadCustomer(currentCustomer.getString("id"));
                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, 1).execute();

//                        CustomerConnect.PostPay(param, new CallbackCustom() {
//                            @Override
//                            public void onResponse(BaseModel result) {
//
//                            }
//
//                            @Override
//                            public void onError(String error) {
//
//                            }
//                        }, true);
                    }
                });
    }

    private void postPayToServer(List<String> listParam) {
        List<BaseModel> params = new ArrayList<>();

        for (String itemdetail: listParam){
            BaseModel item = createPostParam(ApiUtil.PAY_NEW(), itemdetail, false, false);
            params.add(item);
        }

        new CustomGetPostListMethod(params, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                reloadCustomer(currentCustomer.getString("id"));
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();
//        CustomerConnect.PostListPay(listParam, new CallbackListCustom() {
//            @Override
//            public void onResponse(List result) {
//                reloadCustomer(currentCustomer.getString("id"));
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }//
//        }, true);
    }

    protected void printDebtBills() {
        Transaction.gotoPrintBillActivity(new BaseModel(), true);

    }

    protected void printTempBill(BaseModel bill) {
        Transaction.checkInventoryBeforePrintBill(bill,
                DataUtil.array2ListObject(bill.getString(Constants.BILL_DETAIL)),
                User.getCurrentUser().getInt("warehouse_id"));


    }

    private void checkLocation(CallbackBoolean mSuccess) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tvCheckInStatus.setText("Đang kiểm tra vị trí...");
        getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double distance = MapUtil.distance(location.getLatitude(), location.getLongitude(), currentCustomer.getDouble("lat"), currentCustomer.getDouble("lng"));
                CustomSQL.setLong(Constants.CURRENT_DISTANCE, (long) distance);
                mSuccess.onRespone(true);

                if (distance < Constants.CHECKIN_DISTANCE) {
                    tvCheckInStatus.setText(String.format("Đang trong phạm vi cửa hàng ~%sm", Math.round(distance)));
                    threadShowTime.start();
                    infoFragment.tvCheckin.setText(Util.getIconString(R.string.icon_district, "   ", "Checkin - Ghi chú"));

                } else {
                    tvCheckInStatus.setText(String.format("Đang bên ngoài cửa hàng ~%s", distance > 1000 ? Math.round(distance) / 1000 + "km" : Math.round(distance) + "m"));
                    infoFragment.tvCheckin.setText(Util.getIconString(R.string.icon_note, "   ", "Thêm ghi chú"));
                }

            }
        });
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.customer_paid:
                showDiscountPayment();
                break;
        }
        return true;
    }

    private void setPrintIconVisibility() {
        if (currentPosition == 1 && listDebtBill.size() > 0) {
            tvPrint.setVisibility(View.VISIBLE);
        } else {
            tvPrint.setVisibility(View.GONE);
        }
    }

    private void setFilterVisibility() {
        if (currentPosition != 0 && listBills.size() > 0) {
            lnFilter.setVisibility(View.VISIBLE);
        } else {
            lnFilter.setVisibility(View.GONE);
        }
    }

    private void filterBillByRange(long start, long end) {
        if (start == 0 && end == 0) {
            billsFragment.adapter.getFilter().filter("");
            paymentFragment.adapter.getFilter().filter("");
            productFragment.updateListByRange("");
        } else {
            BaseModel model = BaseModel.put2ValueToNewObject("from", start, "to", end);
            billsFragment.adapter.getFilter().filter(model.BaseModelstoString());
            paymentFragment.adapter.getFilter().filter(model.BaseModelstoString());
            productFragment.updateListByRange(model.BaseModelstoString());
        }

        viewPager.setCurrentItem(1, true);
        updateOverview(billsFragment.adapter.getAllBill());

    }

    //get date return from fragment
    @Override
    public void onResponse(BaseModel object) {
        start = object.getLong("start");
        currentCheckedTime = object.getInt("position");

        tvFilterIcon.setText(Util.getIcon(R.string.icon_x));
        tvFilter.setVisibility(View.VISIBLE);
        tvFilter.setText(object.getString("text"));

        filterBillByRange(object.getLong("start"), object.getLong("end"));

    }
}
