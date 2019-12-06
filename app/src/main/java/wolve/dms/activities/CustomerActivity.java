package wolve.dms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.Customer_ViewpagerAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private CTextIcon tvTrash, tvDeleteBill;
    protected Button btnSubmit;
    private TextView  tvCheckInStatus, tvTime;
    protected TextView tvTitle,tvAddress, tvDebt, tvPaid, tvTotal, tvBDF, btnShopCart;
    private ScrollView scContent;
    private CoordinatorLayout coParent;
    private RecyclerView rvCheckin;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HorizontalScrollView scrollOverView;
    private SmoothProgressBar smLoading;
    private RelativeLayout rlStatusGroup;
    //private LinearLayout lnNewBillParent;

    protected BaseModel currentCustomer;
    protected int customerStatusID;
    protected List<BaseModel> listCheckins = new ArrayList<>();
    protected List<BaseModel> listBills = new ArrayList<>();
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    protected List<BaseModel> listDebtBill= new ArrayList<>();
    private long countTime;
    private Fragment mFragment;
    protected List<String> mYears = new ArrayList<>();
    private Customer_ViewpagerAdapter pageAdapter;
    protected BaseModel currentBill, tempBill;
    protected Double currentDebt =0.0;
    private Handler mHandlerUpdateCustomer = new Handler();
    private String tempBillFormat = "Hóa đơn %s tạo %s";
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


    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
        tvDebt.setOnClickListener(this);

    }

    @Override
    public void initialData() {
        countTime = Util.CurrentTimeStamp()  - CustomSQL.getLong(Constants.CHECKIN_TIME);
        threadShowTime.start();
        tvTrash.setVisibility(User.getRole().equals("MANAGER") ? View.VISIBLE : View.GONE);
        currentCustomer = CustomSQL.getBaseModel(Constants.CUSTOMER);
        customerStatusID = currentCustomer.getBaseModel("status").getInt("id");

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

        updateView(currentCustomer, customerStatusID);

    }

    private void updateView(BaseModel customer, int status){
        tvTitle.setText(String.format("%s %s", Constants.getShopName(customer.getString("shopType")), customer.getString("signBoard")));

        rlStatusGroup.setVisibility(CustomSQL.getLong(Constants.CHECKIN_TIME) != 0 ? View.VISIBLE : View.GONE);

        tempBill = null;
        customerStatusID = status;
        //currentCustomer = customer;

        if (customer.hasKey(Constants.TEMPBILL) && CustomSQL.getLong(Constants.CHECKIN_TIME) != 0){
            tempBill = customer.getBaseModel(Constants.TEMPBILL);

        }

        listDebtBill = new ArrayList<>(DataUtil.array2ListObject(customer.getString(Constants.DEBTS)));
        listBills = new ArrayList<>(DataUtil.array2ListObject(customer.getString(Constants.BILLS)));
        listBillDetail = new ArrayList<>(DataUtil.getAllBillDetail(listBills));


        listCheckins = new ArrayList<>(DataUtil.array2ListBaseModel(customer.getJSONArray("checkIns")));

        if (listBills.size() >0){
            BaseModel object = Util.getTotal(listBills);
            currentDebt = object.getDouble("debt");
            tvTotal.setText(String.format("Tổng: %s" ,Util.FormatMoney(object.getDouble("total"))));
            tvDebt.setText(String.format("Nợ: %s" ,Util.FormatMoney(object.getDouble("debt"))));
            tvPaid.setText(String.format("Trả: %s" ,Util.FormatMoney(object.getDouble("paid"))));
            tvBDF.setText(String.format("BDF:%s ", DataUtil.defineBDFPercent(listBillDetail)) +"%");

        }else{
            currentDebt = 0.0;
            if (customerStatusID ==3){
                saveCustomerToLocal("status.id", 1);
                updateView(customer, 1);
            }
        }

        billsFragment.updateList();
        infoFragment.reloadInfo();
        productFragment.updateList();
        paymentFragment.updateList();
        updateBillTabNotify(tempBill != null? true : false, listBills.size());

        if (tempBill != null){
            CustomCenterDialog.alertWithCancelButton("Hóa đơn tạm",
                    String.format("Khách hàng có 1 hóa đơn do %s tạo vói số tiên %sđ nhưng chưa giao\n Bấm -giao hàng- nếu bạn là nhân viên giao hàng",
                            tempBill.getBaseModel("user").getString("displayName"),
                            Util.FormatMoney(tempBill.getDouble("total"))),
                    "Giao hàng",
                    "Hủy",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                printTempBill();

                            }else {
                                viewPager.setCurrentItem(1, true);
                            }

                        }
                    }

            );


        }

        editStatusCustomerWithOldData(customer, status);



    }

    private void editStatusCustomerWithOldData(BaseModel customer, int status){
        if (!currentDebt.equals(customer.getDouble("currentDebt")) || status != customer.getBaseModel("status").getInt("id") ){
            saveCustomerToLocal("currentDebt", currentDebt);
            saveCustomerToLocal("status.id", status);
            submitCustomer();
        }
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
                CustomSQL.setListBaseModel(Constants.BILL_DETAIL, listBillDetail);
                openShopCartScreen();

                break;

            case R.id.customer_debt:
                if (currentDebt >0){
                    showDialogPayment();
                }

                break;

//            case R.id.customer_newbill_text:
//                printTempBill(tempBill);
//
//                break;

//            case R.id.customer_newbill_delete:
//                deleteTempBill(tempBill);
//
//                break;

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
        Transaction.gotoShopCartActivity(DataUtil.convertListObject2Array(DataUtil.getAllBillHaveDebt(listBills)).toString());

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

    private void submitCheckin(final BaseModel customer, String checkinNote){
        CustomerConnect.PostCheckin(createParamCheckin(customer, checkinNote), new CallbackCustom() {

            @Override
            public void onResponse(BaseModel result) {
                returnPreviousScreen(customer.BaseModelstoString());
            }

            @Override
            public void onError(String error) {

            }
        }, true);

    }

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

        }else if(tabLayout.getSelectedTabPosition() !=0){
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();

        }else {
            returnPreviousScreen(currentCustomer.BaseModelstoString());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);

        if (requestCode == Constants.RESULT_SHOPCART_ACTIVITY){
            if (!data.getStringExtra(Constants.SHOP_CART_ACTIVITY).equals("")) {
                reloadCustomer(data.getStringExtra(Constants.SHOP_CART_ACTIVITY), 3);

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
        String param = CustomerConnect.createParamCustomer(CustomSQL.getBaseModel(Constants.CUSTOMER));

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

    private String createParamCheckin(BaseModel customer, String note){
        String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
                customerStatusID,
                Util.encodeString(String.format("[%s] %s", Util.HourStringNatural(countTime), note)),
                User.getUserId()
        );
        return params;
    }

    protected void openReturnFragment(BaseModel bill){
        currentBill = bill;
        changeFragment(new CustomerReturnFragment(), true);

    }

    protected void openEditMapFragment(){
        changeFragment(new CustomerEditMapFragment(), true);

    }

    protected void reloadCustomer(String id, int statusID){
        CustomerConnect.GetCustomerDetail(id, new CallbackCustom() {
            @Override
            public void onResponse(final BaseModel result) {
                BaseModel customer = DataUtil.rebuiltCustomer(result);
                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);
                updateView(customer, statusID);

            }

            @Override
            public void onError(String error) {
                Util.showSnackbarError(error);
            }
        }, true,true);

    }

    private void showDialogPayment(){
        CustomCenterDialog.showDialogPayment("THANH TOÁN CÁC HÓA ĐƠN NỢ",
                listDebtBill,
                0.0,
                true,
                new CallbackListCustom() {
                    @Override
                    public void onResponse(List result) {
                        postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"),  result) );

                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }

    private void postPayToServer(List<String> listParam){
        CustomerConnect.PostListPay(listParam, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                Util.showToast("Thanh toán thành công");
                reloadCustomer(currentCustomer.getString("id"), 3);

            }

            @Override
            public void onError(String error) {

            }//
        }, true);
    }

    protected void printDebtBills(){
        Transaction.gotoPrintBillActivity(new BaseModel(), true);

    }

    protected void printTempBill(){
        Transaction.gotoPrintBillActivity(tempBill, false);

    }


}
