package wolve.dms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
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
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Checkin;
import wolve.dms.models.Customer;
import wolve.dms.models.Status;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.apiconnect.CustomerConnect.createParamCustomer;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {
    private ImageView btnBack;
    private CTextIcon tvTrash;
    protected Button btnSubmit;
    private FloatingActionButton btnCurrentLocation;
    private TextView  tvCheckInStatus, tvTime;
    private TextView tvTitle, tvDebt, tvPaid, tvTotal, tvBDF, btnShopCart;
    private RadioGroup rgStatus;
    private RadioButton rdInterested, rdNotInterested, rdOrdered;
    private ScrollView scContent;
    private LinearLayout lnPaidParent, lnBottomSheet;
    private RelativeLayout rlHeader, rlStatusGroup;
    private CoordinatorLayout coParent;
    private RecyclerView rvCheckin;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HorizontalScrollView scrollOverView;
    private SmoothProgressBar smLoading;

    private BottomSheetBehavior mBottomSheetBehavior;
    protected BaseModel currentCustomer;
    private List<Checkin> listCheckins = new ArrayList<>();
    protected List<BaseModel> listBills = new ArrayList<>();
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    protected List<BaseModel> listDebtBill= new ArrayList<>();
    private Status currentStatus;
    private String firstName = "";
    private FusedLocationProviderClient mFusedLocationClient;
    private long countTime;
    private int currentStateBottom = BottomSheetBehavior.STATE_COLLAPSED;
    private Fragment mFragment;
    protected List<String> mYears = new ArrayList<>();
    private Customer_ViewpagerAdapter pageAdapter;
    protected BaseModel currentBill;
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
                            // update TextView here!
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
        rlHeader = (RelativeLayout) findViewById(R.id.customer_header);
        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnShopCart = (TextView) findViewById(R.id.customer_shopcart);
        tvTrash = (CTextIcon) findViewById(R.id.icon_more);
        tvTitle = (TextView) findViewById(R.id.add_customer_title);
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



    }

    @Override
    public void initialData() {
        threadShowTime.start();

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

        updateView();
//        currentStatus = Status.getStatusList().get(0);

//        setCheckinGroup();
//            firstName = currentCustomer.getString("name");
//            edName.setText(currentCustomer.getString("name") == null ? "" : currentCustomer.getString("name"));
//            edPhone.setText(currentCustomer.getString("phone") == null ? "" : currentCustomer.getString("phone"));
//            edAdress.setText((currentCustomer.getString("address") == null ? "" : currentCustomer.getString("address")));
//            edStreet.setText(currentCustomer.getString("street") == null ? "" : currentCustomer.getString("street"));
//            setNoteText(currentCustomer.getString("note"));
////            edNote.setText(currentCustomer.getString("note"));
//            edDistrict.setText(currentCustomer.getString("district"));
//            edCity.setText(currentCustomer.getString("province"));
//            edShopName.setText(currentCustomer.getString("signBoard"));
//            edShopType.setText(Constants.getShopName(currentCustomer.getString("shopType"), null));
//            tvTitle.setText(edShopType.getText().toString() + " - " + currentCustomer.getString("signBoard"));
//
//            if (currentCustomer.getString("status") != null) {
//                currentStatus = new Status(currentCustomer.getJsonObject("status"));
//            }
//
//
//
//            switch (currentStatus.getInt("id")) {
//                case 1:
//                    rdInterested.setChecked(true);
//                    break;
//                case 2:
//                    rdNotInterested.setChecked(true);
//                    break;
//                case 3:
//                    rdOrdered.setChecked(true);
//                    break;
//            }
//
//            if (currentCustomer.getString("checkIns") != null) {
//                JSONArray array = new JSONArray(currentCustomer.getString("checkIns"));
//                for (int i = 0; i < array.length(); i++) {
//                    listCheckins.add(new Checkin(array.getJSONObject(i)));
//                }
//            }
//            createRVCheckin(listCheckins);
//            lnBottomSheet.setVisibility(listCheckins.size() >0?View.VISIBLE: View.GONE);
//

//
//            if (currentCustomer.isNull("bills")){
//                lnPaidParent.setVisibility(View.GONE);
//            }
//

//



//
//        if (edShopName.getText().toString().trim().equals("")) {
//            edShopName.setSelection();
//        } else {
//            edPhone.setSelection();
//        }


    }

    private void updateView(){
        currentCustomer = new BaseModel(CustomSQL.getString(Constants.CUSTOMER));

        currentCustomer.put("status.id", 3);

        tvTitle.setText(String.format("%s %s", Constants.getShopName(currentCustomer.getString("shopType")), currentCustomer.getString("signBoard")));

        List<BaseModel> mLists = new ArrayList<>();
        if (currentCustomer.getString("bills") != null) {
            List<BaseModel> list = DataUtil.array2ListObject(currentCustomer.getString("bills"));

            for (int i = 0; i < list.size(); i++) {
                BaseModel user = list.get(i).getBaseModel("user");
                if (User.getRole().equals(Constants.ROLE_ADMIN)){
                    mLists.add(list.get(i));
                }else {
                    if (User.getId() == user.getInt("id")){
                        mLists.add(list.get(i));
                    }
                }
            }

        }
        listBills = DataUtil.remakeBill(mLists);
        listBillDetail = DataUtil.getAllBillDetail(listBills);
        listDebtBill = DataUtil.getAllBillHaveDebt(listBills);

        if (listBills.size() >0){
            scrollOverView.setVisibility(View.VISIBLE);
            showMoneyOverview(listBills);
            Log.e("abcde", "hereeeee" + currentDebt +"dfsdf" + currentCustomer.getDouble("currentDebt"));

            if (!currentDebt.equals(currentCustomer.getDouble("currentDebt")) ){
                Log.e("abcde", "here");
                submitCustomer();
            }
        }else {
            scrollOverView.setVisibility(View.GONE);
        }

        billsFragment.updateList();
        infoFragment.reloadInfo();
        productFragment.updateList();
        paymentFragment.updateList();

        updateBillTabNotify();
        showTrashButton();

    }

    private void showTrashButton(){
        if (currentCustomer.getInt("id") != 0) {
            tvTrash.setVisibility(User.getRole().equals("MANAGER") ? View.VISIBLE : View.GONE);

        } else {
            tvTrash.setVisibility(View.GONE);

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

    private void updateBillTabNotify(){
        //pageAdapter.getNotifyBaged(count);
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.setCustomView(null);
        tab.setCustomView(pageAdapter.getNotifyBaged(listBills.size()));
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
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
        tvDebt.setOnClickListener(this);

    }

//    private void setCheckinGroup(){
//        if (CustomSQL.getBoolean(Constants.CHECKIN_FLAG)){
//            countTime = Util.CurrentTimeStamp() - CustomSQL.getLong(Constants.CHECKIN_TIME);
//            rlStatusGroup.setBackground(getResources().getDrawable(R.color.colorBlueTransparent));
//            tvCheckInStatus.setText("Đang check in ....");
//            tvCheckInStatus.setTextColor(getResources().getColor(R.color.black_text_color_hint));
//            tvTime.setVisibility(View.VISIBLE);
//            threadShowTime.start();
//            btnSubmit.setText("CHECK_IN & CẬP NHẬT");
//
//        }else {
//            tvTime.setVisibility(View.GONE);
//            rlStatusGroup.setBackground(getResources().getDrawable(R.color.colorRedTransparent));
//            tvCheckInStatus.setText("Đang bên ngoài khu vực cửa hàng ....");
//            tvCheckInStatus.setTextColor(getResources().getColor(R.color.white_text_color));
//            btnSubmit.setText("CẬP NHẬT");
//
//        }
//    }

//    private void createRVCheckin(List<Checkin> list){
//        Customer_CheckinsAdapter adapter = new Customer_CheckinsAdapter(list);
//        adapter.notifyDataSetChanged();
//        rvCheckin.setAdapter(adapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        rvCheckin.setLayoutManager(layoutManager);
//
//    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();

                break;

            case R.id.icon_more:
                if (listBills.size() > 0) {
                    Util.showToast("Không thể xóa khách hàng có phát sinh hóa đơn");

                } else if (User.getRole().equals(Constants.ROLE_ADMIN)){
                    deleteCustomer();
                }

                break;

            case R.id.customer_shopcart:
//                openShopCartScreen(getCurrentCustomer());
                openShopCartScreen(currentCustomer);

                break;

            case R.id.customer_debt:
                if (currentDebt >0){
                    showDialogPayment();
                }

                break;

//            case R.id.customer_checkin_title:
//                mBottomSheetBehavior.setState(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ?
//                BottomSheetBehavior.STATE_EXPANDED:BottomSheetBehavior.STATE_COLLAPSED);
//
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

    protected void openShopCartScreen(BaseModel customer){

        if (customer.getInt("id") == 0){
            CustomerConnect.CreateCustomer(createParamCustomer(new BaseModel(CustomSQL.getString(Constants.CUSTOMER)) , 0.0), new CallbackCustom() {
                @Override
                public void onResponse(BaseModel result) {
                    currentCustomer.put("id", result.getInt("id"));

                    result.put("bills", new JSONArray());
                    Transaction.gotoShopCartActivity(result.BaseModelstoString() , DataUtil.convertListObject2Array(DataUtil.getAllBillHaveDebt(listBills)).toString());
                }

                @Override
                public void onError(String error) {

                }
            }, true);

        }else {
            Transaction.gotoShopCartActivity(customer.BaseModelstoString() , DataUtil.convertListObject2Array(DataUtil.getAllBillHaveDebt(listBills)).toString());

        }

    }



//    private void submitCustomer(final CallbackObject mListener, Boolean loading){
//        if (checkInputField()){
//            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackCustom() {
//                @Override
//                public void onResponse(BaseModel result) {
//                    mListener.onResponse(result.convertJsonObject());
//                }
//
//                @Override
//                public void onError(String error) {
//
//                }
//            }, loading);
//        }
//
//    }

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
                CustomerConnect.DeleteCustomer(currentCustomer.getString("id"), new CallbackJSONObject() {
                    @Override
                    public void onResponse(JSONObject result) {
                        returnPreviousScreen(String.format("delete-%s",currentCustomer.getString("id")));
                    }

                    @Override
                    public void onError(String error) {

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
            //Util.smoothImageRotation(btnBack, 90);

        } else if(mFragment != null && mFragment instanceof CustomerEditMapFragment){
            getSupportFragmentManager().popBackStack();

        }else {
            returnPreviousScreen(currentCustomer.BaseModelstoString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);

        if (requestCode == Constants.RESULT_SHOPCART_ACTIVITY){
            if (data.getStringExtra(Constants.SHOP_CART_ACTIVITY).equals(Constants.RELOAD_DATA)){
                reloadCustomer();



//                reloadBills(new CallbackBoolean() {
//                    @Override
//                    public void onRespone(Boolean result){
//                        CustomCenterDialog.alertWithCancelButton(
//                                "Done!",
//                                "Bạn vừa hoàn tất hóa đơn. Bạn có muốn trở lại màn hình bản đồ",
//                                "Đồng ý",
//                                "Không", new CallbackBoolean() {
//                            @Override
//                            public void onRespone(Boolean result) {
//                                if (result){
//                                    submitCustomerAndCheckin("Đã mua hàng" );
//                                }
//
//                            }
//                        });
//                    }
//                });
            }

        }else {
            onResume();

        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.customer_radio_status_interested:
                currentStatus = Status.getStatusList().get(0);

                break;

            case R.id.customer_radio_status_nointerested:
                currentStatus = Status.getStatusList().get(1);

                break;

            case R.id.customer_radio_status_ordered:
                currentStatus = Status.getStatusList().get(2);

                break;

        }
    }

    private void showCheckinStatus() {
        if (rdNotInterested.isChecked()){
            submitCustomerAndCheckin("Không quan tâm" );

        }else {
            CustomCenterDialog.showCheckinReason("NỘI DUNG GHÉ THĂM CỬA HÀNG", Status.getStatusList(), new CallbackString() {
                @Override
                public void Result(String s) {
                    submitCustomerAndCheckin(s );
                }
            });
        }

    }

//    protected BaseModel getCurrentCustomer(BaseModel mCustomer){
//        BaseModel customer = new Customer();
//        customer = currentCustomer;
//        customer.put("name", edName.getText().toString().trim());
//        customer.put("signBoard", edShopName.getText().toString().trim());
//        customer.put("address", edAdress.getText().toString().trim());
//        customer.put("phone", edPhone.getText().toString().trim());
//        customer.put("street", edStreet.getText().toString().trim());
////            customer.put("note", edNote.getText().toString().trim());
//        customer.put("note", getCustomerNote());
//        customer.put("district", edDistrict.getText().toString().trim());
//        customer.put("province", edCity.getText().toString().trim());
//        customer.put("lat", currentCustomer.getDouble("lat"));
//        customer.put("lng", currentCustomer.getDouble("lng"));
//        customer.put("volumeEstimate", 10);
//        customer.put("shopType", Constants.getShopType(edShopType.getText().toString()));
//        customer.put("status.id", currentStatus.getInt("id"));
//        customer.put("debt", Util.getTotal(listBills).getDouble("debt") );
//
//        return customer;
//    }

//    private String getCustomerNote(){
//        String note = "";
//        Double total = Util.getTotal(listBills).getDouble("debt");
//        if (total > 0.0){
//            try{
//                JSONObject object = new JSONObject();
//                object.put("debt", total);
//                object.put("note", edNote.getText().toString());
//                object.put("userId", listBills.get(listBills.size()-1).getJsonObject("user").getInt("id"));
//                object.put("userName", listBills.get(listBills.size()-1).getJsonObject("user").getString("displayName"));
//                note = object.toString();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }else {
//            note = edNote.getText().toString();
//        }
//
//        return note;
//    }

    protected void saveCustomerToLocal(BaseModel customer){
        CustomSQL.setString(Constants.CUSTOMER, customer.BaseModelstoString());

        mHandlerUpdateCustomer.removeCallbacks(mUpdateTask);
        mHandlerUpdateCustomer.postDelayed(mUpdateTask, 2000);


//        BaseModel customer = new Customer();
//        customer = currentCustomer;
//        customer.put("name", edName.getText().toString().trim());
//        customer.put("signBoard", edShopName.getText().toString().trim());
//        customer.put("address", edAdress.getText().toString().trim());
//        customer.put("phone", edPhone.getText().toString().trim());
//        customer.put("street", edStreet.getText().toString().trim());
////            customer.put("note", edNote.getText().toString().trim());
//        customer.put("note", getCustomerNote());
//        customer.put("district", edDistrict.getText().toString().trim());
//        customer.put("province", edCity.getText().toString().trim());
//        customer.put("lat", currentCustomer.getDouble("lat"));
//        customer.put("lng", currentCustomer.getDouble("lng"));
//        customer.put("volumeEstimate", 10);
//        customer.put("shopType", Constants.getShopType(edShopType.getText().toString()));
//        customer.put("status.id", currentStatus.getInt("id"));
//        customer.put("debt", Util.getTotal(listBills).getDouble("debt") );
//
//        return customer;
    }

    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            submitCustomer();

        }
    };

    protected void submitCustomer(){
        smLoading.setVisibility(View.VISIBLE);
//        String param = createParamCustomer(new BaseModel(CustomSQL.getString(Constants.CUSTOMER)));
        String param = createParamCustomer(currentCustomer, currentDebt);

        CustomerConnect.CreateCustomer(param , new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                smLoading.setVisibility(View.INVISIBLE);
                CustomSQL.setString(Constants.CUSTOMER, result.BaseModelstoString());
                currentCustomer =result;

                infoFragment.reshowAddress(result);

            }

            @Override
            public void onError(String error) {
                smLoading.setVisibility(View.INVISIBLE);
                Util.showToast(error);
            }
        }, false);
    }

//    @Override
//    public void onGlobalLayout() {
//        coParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
////        int height = coParent.getHeight() - rlHeader.getHeight();
//        ViewGroup.LayoutParams params = lnBottomSheet.getLayoutParams();
//        params.height = (int) (coParent.getHeight()*0.6);
//        lnBottomSheet.requestLayout();
//
//        setupBottomSheet();
//
//    }

//    private void setupBottomSheet() {
//        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottomSheet);
//        int height = getResources().getDimensionPixelSize(R.dimen._34sdp);
////        mBottomSheetBehavior.setPeekHeight(Util.convertDp2PxInt(60-8));
//        mBottomSheetBehavior.setPeekHeight(height);
//        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
////                Util.hideKeyboard(lnBottomSheet);
//                switch (newState){
//                    case BottomSheetBehavior.STATE_DRAGGING:
////                        tvBottomDown.setVisibility(View.INVISIBLE);
//                        break;
//
//                    case BottomSheetBehavior.STATE_COLLAPSED:
////                        tvBottomDown.setVisibility(View.INVISIBLE);
////                        tvBottomUp.setVisibility(View.VISIBLE);
//                        break;
//
//                    case BottomSheetBehavior.STATE_EXPANDED:
//                        Util.hideKeyboard(lnBottomSheet);
////                        tvBottomDown.setVisibility(View.VISIBLE);
////                        tvBottomUp.setVisibility(View.INVISIBLE);
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
////                tvBottomUp.setAlpha(1-slideOffset);
//            }
//        });
//    }


    protected void showMoneyOverview(List<BaseModel> listbill){
        BaseModel object = Util.getTotal(listbill);
        currentDebt = object.getDouble("debt");
        if (!currentDebt.equals(currentCustomer.getDouble("currentDebt"))){
            currentCustomer.put("currentDebt", currentDebt);
            submitCustomer();
        }

        tvTotal.setText(String.format("Tổng: %s" ,Util.FormatMoney(object.getDouble("total"))));
        tvDebt.setText(String.format("Nợ: %s" ,Util.FormatMoney(object.getDouble("debt"))));
        tvPaid.setText(String.format("Trả: %s" ,Util.FormatMoney(object.getDouble("paid"))));
        tvBDF.setText(String.format("BDF:%s ", DataUtil.defineBDFPercent(listBillDetail)) +"%");

    }
//
//    protected void reloadBills(final CallbackBoolean mListener){
//        String param = currentCustomer.getString("id");
//        CustomerConnect.GetCustomerDetail(param, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel customer) {
//                List<BaseModel> mList = new ArrayList<>();
//                if (customer.getString("bills") != null) {
//                    //JSONArray array = customer.getJSONArray("bills");
//                    List<BaseModel> listBill = DataUtil.array2ListBaseModel(customer.getJSONArray("bills"));
//
//                    for (int i = 0; i < listBill.size(); i++) {
//                        //BaseModel objectBill = new BaseModel(array.getJSONObject(i);
//                        BaseModel objectUser = new BaseModel(listBill.get(i).getJsonObject("user"));
//                        if (User.getRole().equals(Constants.ROLE_ADMIN)) {
//                            mList.add(listBill.get(i));
//
//                        } else {
//                            if (User.getId() == objectUser.getInt("id")) {
//                                mList.add(listBill.get(i));
//                            }
//                        }
//                        addYearToList(Util.YearString(listBill.get(i).getLong("createAt")));
//
//                    }
//
//                    currentCustomer.put("bills", customer.getJSONArray("bills"));
//                    showMoneyOverview(mList);
//                    mListener.onRespone(true);
//
//
//                    if (mList.size() > 0) {
//                        rdOrdered.setChecked(true);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                mListener.onRespone(false);
//            }
//        }, true,true);
//    }

//    private void shopTypeEvent(){
//        edShopType.setDropdown(true, new CInputForm.ClickListener() {
//            @Override
//            public void onClick(View view) {
//                CustomBottomDialog.choiceList("CHỌN NHÓM CỬA HÀNG",Util.arrayToList(Constants.shopName) , new CustomBottomDialog.StringListener() {
//                    @Override
//                    public void onResponse(String content) {
//                        edShopType.setText(content);
//                    }
//                });
//
//            }
//        });
//
//    }



    private String createParamCheckin(BaseModel customer, String note){
        String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
                currentStatus.getInt("id"),
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

    protected void reloadCustomer(){
        String param = currentCustomer.getString("id");
        CustomerConnect.GetCustomerDetail(param, new CallbackCustom() {
            @Override
            public void onResponse(final BaseModel result) {
                CustomSQL.setString(Constants.CUSTOMER, result.BaseModelstoString());
                updateView();

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
                reloadCustomer();

            }

            @Override
            public void onError(String error) {

            }//
        }, true);
    }



}
