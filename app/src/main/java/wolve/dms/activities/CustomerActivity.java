package wolve.dms.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.Customer_ViewpagerAdapter;
import wolve.dms.adapter.FilterChoiceAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.calendarpicker.CalendarUtil;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {
    private ImageView btnBack;
    private CTextIcon tvTrash, tvDeleteBill;
    protected Button btnSubmit;
    private TextView  tvCheckInStatus, tvTime;
    protected TextView tvTitle,tvAddress, tvDebt, tvPaid, tvTotal, tvBDF, btnShopCart, tvFilter;
    private ScrollView scContent;
    private CoordinatorLayout coParent;
    private RecyclerView rvFilterTitle;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HorizontalScrollView scrollOverView;
    private SmoothProgressBar smLoading;
    private RelativeLayout rlStatusGroup;
    private LinearLayout lnFilter;

    protected BaseModel currentCustomer;
    protected List<BaseModel> listCheckins = new ArrayList<>();
    protected List<BaseModel> listBills = new ArrayList<>();
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    protected List<BaseModel> listDebtBill= new ArrayList<>();
    private long countTime;
    private Fragment mFragment;
    protected List<String> mYears = new ArrayList<>();
    private Customer_ViewpagerAdapter pageAdapter;
    protected BaseModel currentBill, tempBill = null;
    protected Double currentDebt =0.0;
    private Handler mHandlerUpdateCustomer = new Handler();

    protected static CustomerBillsFragment billsFragment;
    protected static CustomerInfoFragment infoFragment;
    protected static CustomerProductFragment productFragment;
    protected static CustomerPaymentFragment paymentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitScrollWithFullscreen.assistActivity(this, 1);
    }

    Thread threadShowTime = new Thread() {
        @Override
        public void run() {
            try { while (!threadShowTime.isInterrupted()) {
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
        coParent = (CoordinatorLayout) findViewById(R.id.customer_parent);
        coParent.setPadding(0, 0, 0, Util.getNavigationBarHeight());
        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnShopCart = (TextView) findViewById(R.id.customer_shopcart);
        tvTrash = (CTextIcon) findViewById(R.id.icon_more);
        tvTitle = (TextView) findViewById(R.id.customer_title);
        tvDebt = (TextView) findViewById(R.id.customer_debt);
        scContent = (ScrollView) findViewById(R.id.customer_scrollview);
        tvCheckInStatus = findViewById(R.id.customer_checkin_status);
        tvTime = findViewById(R.id.customer_checkin_time);
        rlStatusGroup = findViewById(R.id.customer_checkin_status_group);
        viewPager = findViewById(R.id.customer_viewpager);
        tabLayout = findViewById(R.id.customer_tabs);
        tvDebt = findViewById(R.id.customer_debt);
        tvPaid = findViewById(R.id.customer_paid);
        tvTotal = findViewById(R.id.customer_total);
        tvBDF = findViewById(R.id.customer_bdf);
        scrollOverView = findViewById(R.id.customer_overview);
        smLoading = findViewById(R.id.customer_loading);
        tvAddress = findViewById(R.id.customer_address);
        lnFilter = findViewById(R.id.customer_filter);
        tvFilter = findViewById(R.id.customer_filter_text);
        rvFilterTitle = findViewById(R.id.customer_filter_rv);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
        tvDebt.setOnClickListener(this);
        tvPaid.setOnClickListener(this);
        tvPaid.setOnLongClickListener(this);
        lnFilter.setOnClickListener(this);
        Util.textViewEvent(tvFilter, new CallbackString() {
            @Override
            public void Result(String s) {
                if (!s.equals("")){
                    tvFilter.setPadding( Util.convertSdpToInt(R.dimen._15sdp),0,0,0);
                }else {
                    tvFilter.setPadding( 0,0,0,0);
                }
            }
        });


    }

    @Override
    public void initialData() {
        countTime = Util.CurrentTimeStamp()  - CustomSQL.getLong(Constants.CHECKIN_TIME);
        CustomSQL.removeKey(Constants.CURRENT_DISTANCE);

        tvTrash.setVisibility(User.getCurrentRoleId() == Constants.ROLE_ADMIN ? View.VISIBLE : View.GONE);

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

        updateView(CustomSQL.getBaseModel(Constants.CUSTOMER));
        checkLocation(new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    billsFragment.updateTempBill();

                }
            }
        });

    }

    private void updateView(BaseModel customer){
        createRVFilter();
        currentCustomer = customer;
        tvTitle.setText(String.format("%s %s", Constants.getShopName(currentCustomer.getString("shopType")), currentCustomer.getString("signBoard")));

        if (currentCustomer.hasKey(Constants.TEMPBILL) ){
            tempBill = currentCustomer.getBaseModel(Constants.TEMPBILL);
            viewPager.setCurrentItem(1, true);

        }else {
            tempBill = null;
        }

        listDebtBill = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.DEBTS)));
        listBills = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.BILLS)));
        listBillDetail = new ArrayList<>(DataUtil.getAllBillDetail(listBills));
        listCheckins = new ArrayList<>(DataUtil.array2ListBaseModel(currentCustomer.getJSONArray(Constants.CHECKINS)));

        updateBillTabNotify(tempBill != null?true : false , listBills.size());

        if (listBills.size() >0){
            BaseModel object = Util.getTotal(listBills);
            currentDebt = object.getDouble("debt");
            tvTotal.setText(String.format("Tổng: %s" ,Util.FormatMoney(object.getDouble("total"))));
            tvDebt.setText(String.format("Nợ: %s" ,Util.FormatMoney(object.getDouble("debt"))));
            tvPaid.setText(String.format("Trả: %s" ,Util.FormatMoney(object.getDouble("paid"))));
            tvBDF.setText(String.format("BDF:%s ", DataUtil.defineBDFPercent(listBillDetail)) +"%");
            lnFilter.setVisibility(View.VISIBLE);

        }else {
            tvTotal.setText("...");
            tvDebt.setText("...");
            tvPaid.setText("...");
            tvBDF.setText("...");
            lnFilter.setVisibility(View.GONE);
        }
        infoFragment.reloadInfo();

        billsFragment.updateList();
        productFragment.updateList();
        paymentFragment.updateList();



    }

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new Customer_ViewpagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerInfoFragment.class.getName()), "Thông tin",0);
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerBillsFragment.class.getName()),"Hóa đơn",0);
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerProductFragment.class.getName()), "Sản Phẩm",0);
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerPaymentFragment.class.getName()), "Tiền mặt",0);

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

    private void updateBillTabNotify(boolean hasTempBill, int billAmount){
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.setCustomView(null);
        tab.setCustomView(pageAdapter.getNotifyBaged(billAmount, hasTempBill));

    }

    private void addYearToList(String year){
        if (mYears.size() ==0){
            mYears.add(0, Constants.ALL_FILTER);
        }
        if (!mYears.get(mYears.size()-1).equals(year)){
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

                } else {
                    deleteCustomer();

                }

                break;

            case R.id.customer_shopcart:
                if (tempBill != null){
                    Util.showSnackbar("Có 1 đơn hàng chưa hoàn thành nên không thể tạo hóa đơn mới", null, null);

                }else {
                    CustomSQL.setListBaseModel(Constants.BILL_DETAIL, listBillDetail);
                    openShopCartScreen();
                }

                break;

            case R.id.customer_debt:
                if (currentDebt >0){
                    showDialogPayment();
                }

                break;

            case R.id.customer_paid:
                if (listBills.size() >0){
                    CustomCenterDialog.alert("Chiết khấu", "Nhấn giữ vào biểu tường này để nhập số tiền chiết khấu cho khách hàng", "đồng ý");

                }

                break;

            case R.id.customer_filter:
                if (rvFilterTitle.getVisibility() == View.VISIBLE){
                    rvFilterTitle.setVisibility(View.GONE);

                }else {
                    rvFilterTitle.setVisibility(View.VISIBLE);
                    new Handler().postDelayed (new Runnable() {
                        @Override
                        public void run() {
                            rvFilterTitle.setVisibility(View.GONE);
                        }}, 2000);
                }


                break;

        }
    }

    private void returnPreviousScreen(String customer){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.CUSTOMER, customer != null? customer : null);
        setResult(Constants.RESULT_CUSTOMER_ACTIVITY,returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.nothing, R.anim.slide_down);
        Util.getInstance().getCurrentActivity().finish();

    }

    protected void openShopCartScreen(){
        Transaction.gotoShopCartActivity(DataUtil.convertListObject2Array(listDebtBill).toString());

    }

    private void submitCustomerAndCheckin(final String statusNote){
//        if (checkInputField()){
//            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackCustom() {
//                @Override
//                public void onResponse(BaseModel result) {
//                    submitCheckin(result, statusNote);
//                }
//
//                @Override
//                public void onError(String error) {
//
//                }
//            }, false);
//        }

    }

//    private void submitCheckin(final BaseModel customer, String checkinNote){
//        CustomerConnect.PostCheckin(createParamCheckin(customer, checkinNote), new CallbackCustom() {
//
//            @Override
//            public void onResponse(BaseModel result) {
//                returnPreviousScreen(customer.BaseModelstoString());
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, true);
//
//    }

    private void deleteCustomer(){
        CustomCenterDialog.alertWithCancelButton(null, String.format("Xóa khách hàng %s", tvTitle.getText().toString()) , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                CustomerConnect.DeleteCustomer(currentCustomer.getString("id"), new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {
                        Util.getInstance().stopLoading(true);
                        currentCustomer.put("deleted", true);
                        CustomSQL.setString(Constants.CUSTOMER, currentCustomer.BaseModelstoString());

                        Util.showToast("Xóa thành công!");
                        returnPreviousScreen(currentCustomer.BaseModelstoString());


                    }

                    @Override
                    public void onError(String error) {
                        Util.getInstance().stopLoading(true);
                        Constants.throwError(error);

                    }


                }, true);
            }
        });

    }

    @Override
    public void onBackPressed() {

        mFragment = getSupportFragmentManager().findFragmentById(R.id.customer_parent);
        if (Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);

        } else if(mFragment != null && mFragment instanceof CustomerReturnFragment) {
            getSupportFragmentManager().popBackStack();

        } else if(mFragment != null && mFragment instanceof CustomerEditMapFragment) {
            getSupportFragmentManager().popBackStack();

        }else if(mFragment != null && mFragment instanceof CheckinFragment) {
            getSupportFragmentManager().popBackStack();

        } else if(tabLayout.getSelectedTabPosition() !=0){
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();

        }else {
            returnPreviousScreen(currentCustomer.BaseModelstoString());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Util.getInstance().setCurrentActivity(this);
        if (requestCode == Constants.RESULT_SHOPCART_ACTIVITY){
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.SHOP_CART_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)) {
                reloadCustomer(CustomSQL.getBaseModel(Constants.CUSTOMER).getString("id"));

            }

        }else if (requestCode == Constants.RESULT_PRINTBILL_ACTIVITY){
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.PRINT_BILL_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)) {
                reloadCustomer(CustomSQL.getBaseModel(Constants.CUSTOMER).getString("id"));

            }

        }else {
            onResume();

        }

    }

    protected void saveCustomerToLocal(String key, Object value){
        currentCustomer = CustomSQL.getBaseModel(Constants.CUSTOMER);
        currentCustomer.put(key, value);
        CustomSQL.setBaseModel(Constants.CUSTOMER, currentCustomer);

        mHandlerUpdateCustomer.removeCallbacks(mUpdateTask);
        mHandlerUpdateCustomer.postDelayed(mUpdateTask, 2000);

    }

    private Runnable mUpdateTask = new Runnable(){
        @Override
        public void run() {
            submitCustomer();

        }
    };

    protected void submitCustomer(){
        smLoading.setVisibility(View.VISIBLE);
        BaseModel customer = CustomSQL.getBaseModel(Constants.CUSTOMER);
        customer.put("checkinCount", listCheckins.size());
        String param = CustomerConnect.createParamCustomer(customer);

        CustomerConnect.CreateCustomer(param , new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                smLoading.setVisibility(View.INVISIBLE);



            }

            @Override
            public void onError(String error) {
                smLoading.setVisibility(View.INVISIBLE);
            }
        }, false);
    }

    protected void reshowAdd(BaseModel add){
        tvAddress.setText(String.format(Constants.addressFormat,
                add.getString("address"),
                add.getString("street"),
                add.getString("district"),
                add.getString("province")));

    }

//    private String createParamCheckin(BaseModel customer, String note){
//        String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
//                customerStatusID,
//                Util.encodeString(String.format("[%s] %s", Util.HourStringNatural(countTime), note)),
//                User.getUserId()
//        );
//        return params;
//    }

    protected void openReturnFragment(BaseModel bill){
        currentBill = bill;
        changeFragment(new CustomerReturnFragment(), true);

    }

    protected void openEditMapFragment(){
        changeFragment(new CustomerEditMapFragment(), true);

    }

    protected void reloadCustomer(String id){
        CustomerConnect.GetCustomerDetail(id, new CallbackCustom() {
            @Override
            public void onResponse(final BaseModel result) {
                BaseModel customer = DataUtil.rebuiltCustomer(result, false);
                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);
                updateView(customer);

                updateCustomerDebt(customer);


            }

            @Override
            public void onError(String error) {
                Util.showSnackbarError(error);
            }
        }, true,false);

    }

    private void showDialogPayment(){
        CustomCenterDialog.showDialogPayment("THANH TOÁN CÁC HÓA ĐƠN NỢ",
                listDebtBill,
                0.0,
                true,
                new CallbackListCustom() {
                    @Override
                    public void onResponse(List result) {
                        postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"),  result, false) );

                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }

    private void showDiscountPayment(){
        CustomCenterDialog.showDialogDiscountPayment("TRỪ TIỀN CHIẾT KHẤU KHÁCH HÀNG",
                "Nhập số tiền chiết khấu",
                currentCustomer.getDouble("paid"),
                new CallbackDouble() {
                    @Override
                    public void Result(Double d) {
                        String param = DataUtil.createPostPaymentParam(currentCustomer.getInt("id"),
                                User.getId(),
                                d *-1,
                                0,
                                "Trả chiết khấu",
                                false) ;
                        CustomerConnect.PostPay(param, new CallbackCustom() {
                            @Override
                            public void onResponse(BaseModel result) {
                                reloadCustomer(currentCustomer.getString("id"));
                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, true);
                    }
                });
    }

    private void postPayToServer(List<String> listParam){
        CustomerConnect.PostListPay(listParam, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                reloadCustomer(currentCustomer.getString("id"));

            }

            @Override
            public void onError(String error) {

            }//
        }, true);
    }

    protected void printDebtBills(){
        Transaction.gotoPrintBillActivity(new BaseModel(), true);

    }

    private void dialogTempBill(BaseModel tempBill){
        CustomCenterDialog.alertWithCancelButton("Hóa đơn tạm",
                String.format("Khách hàng có 1 hóa đơn do %s tạo vói số tiên %sđ nhưng chưa giao\n Bấm -giao hàng- nếu bạn là nhân viên giao hàng",
                        tempBill.getBaseModel("user").getString("displayName"),
                        Util.FormatMoney(tempBill.getDouble("total"))),
                "Giao hàng",
                "Không",
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result){
                            printTempBill(tempBill);

                        }else {
                            viewPager.setCurrentItem(1, true);
                        }

                    }
                }
        );
    }

    protected void printTempBill(BaseModel bill){
        Transaction.gotoPrintBillActivity(bill, false);

    }

    private void checkLocation(CallbackBoolean mSuccess){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tvCheckInStatus.setText("Đang kiểm tra vị trí...");
        getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double distance = MapUtil.distance(location.getLatitude(), location.getLongitude(), currentCustomer.getDouble("lat"), currentCustomer.getDouble("lng"));
                CustomSQL.setLong(Constants.CURRENT_DISTANCE, (long) distance);
                mSuccess.onRespone(true);

                if (distance < Constants.CHECKIN_DISTANCE){
                    tvCheckInStatus.setText(String.format("Đang trong phạm vi cửa hàng ~%sm", Math.round(distance)));
                    threadShowTime.start();

                }else {
                    tvCheckInStatus.setText(String.format("Đang bên ngoài cửa hàng ~%s", distance >1000? Math.round(distance)/1000 +"km": Math.round(distance) + "m"));

                }

            }
        });
    }

    private void updateCustomerDebt(BaseModel customer){
        List<BaseModel> results = new ArrayList<>();
        List<String> params = new ArrayList<>();

        if (listDebtBill.size() >0){
            for (int i=0; i<listDebtBill.size(); i++){
                BaseModel item = new BaseModel();
                item.put("user_id", listDebtBill.get(i).getInt("user_id"));
                item.put("customer_id", listDebtBill.get(i).getInt("customer_id"));
                item.put("distributor_id", listDebtBill.get(i).getInt("distributor_id"));
                item.put("debt", listDebtBill.get(i).getDouble("debt"));

                boolean check = false;
                for (int ii=0; ii< results.size(); ii++){
                    if (listDebtBill.get(i).getInt("user_id") == results.get(ii).getInt("user_id")){
                        results.get(ii).put("debt", results.get(ii).getDouble("debt") + listDebtBill.get(i).getDouble("debt") );

                        check = true;
                        break;
                    }
                }

                if (!check){
                    results.add(item);
                }

            }

            for (int a=0; a<results.size(); a++){
                params.add(String.format(Api_link.DEBT_PARAM,
                            results.get(a).getDouble("debt"),
                            results.get(a).getInt("user_id"),
                            results.get(a).getInt("customer_id"),
                            results.get(a).getInt("distributor_id")));

            }

        }else if (listBills.size() >0){
            params.add(String.format(Api_link.DEBT_PARAM,
                    0.0,
                    listBills.get(0).getInt("user_id"),
                    customer.getInt("id"),
                    Distributor.getId()));
        }

        CustomerConnect.PostListDebt(params, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                Util.showSnackbarError("Cập nhật công nợ thành công");
            }

            @Override
            public void onError(String error) {
                Util.showSnackbarError("Cập nhật công nợ thất bại");

            }
        }, true);

    }


    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.customer_paid:
                showDiscountPayment();
                break;
        }
        return true;
    }

    private void createRVFilter(){
        FilterChoiceAdapter adapter = new FilterChoiceAdapter(new CallbackString() {
            @Override
            public void Result(String s) {
                rvFilterTitle.setVisibility(View.GONE);
                switch (s){
                    case Constants.ALL_FILTER:
                        tvFilter.setText("");
                        filterBillByRange(0, 0);
                        break;


                    case Constants.FILTER_BY_DATE:
                        CalendarUtil.datePicker(new CustomCenterDialog.CallbackRangeTime() {
                            @Override
                            public void onSelected(long start, long end) {
                                filterBillByRange(start, end);
                            }
                        }, new CallbackString() {
                            @Override
                            public void Result(String s) {
                                tvFilter.setText(s);

                            }
                        });

                        break;

                    case Constants.FILTER_BY_MONTH:
                        CalendarUtil.monthPicker(new CustomCenterDialog.CallbackRangeTime() {
                            @Override
                            public void onSelected(long start, long end) {
                                filterBillByRange(start, end);

                            }
                        },new CallbackString() {
                            @Override
                            public void Result(String s) {
                                tvFilter.setText(s);

                            }
                        }).show(getSupportFragmentManager(), null);

                        break;

                    case Constants.FILTER_BY_YEAR:
                        CalendarUtil.showDialogYearPicker(null, new CustomCenterDialog.CallbackRangeTime() {
                            @Override
                            public void onSelected(long start, long end) {
                                filterBillByRange(start, end);
                            }
                        }, new CallbackString() {
                            @Override
                            public void Result(String s) {
                                tvFilter.setText(s);

                            }
                        });
                        break;
                }

            }
        });
        Util.createLinearRV(rvFilterTitle, adapter);

    }

    private void filterBillByRange(long start, long end){
        if (start == 0 && end == 0){
            billsFragment.adapter.getFilter().filter("");
            paymentFragment.adapter.getFilter().filter("");
            productFragment.updateListByRange("");
        }else {
            BaseModel model = BaseModel.put2ValueToNewObject("from", start, "to", end);
            billsFragment.adapter.getFilter().filter(model.BaseModelstoString());
            paymentFragment.adapter.getFilter().filter(model.BaseModelstoString());
            productFragment.updateListByRange(model.BaseModelstoString());
        }

    }

}
