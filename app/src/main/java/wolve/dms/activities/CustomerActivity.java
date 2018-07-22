package wolve.dms.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.adapter.CustomerBillsAdapter;
import wolve.dms.adapter.CustomerCheckinsAdapter;
import wolve.dms.adapter.CustomerViewpagerAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.CallbackUpdateBill;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.customviews.WorkaroundMapFragment;
import wolve.dms.models.Bill;
import wolve.dms.models.Checkin;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Status;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_PERMISSION_LOCATION;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener, ViewTreeObserver.OnGlobalLayoutListener {
    public GoogleMap mMap;
    public SupportMapFragment mapFragment;
    private ImageView btnBack;
    private CTextIcon tvTrash, btnEditLocation, tvDropdown;
    private Button btnSubmit;
    private FloatingActionButton btnShopCart, btnCurrentLocation;
    private TextView tvTitle, tvTotal, tvDebt, tvPaid;
    private CInputForm edName, edPhone, edAdress, edStreet, edDistrict, edCity, edNote, edShopType, edShopName;
    private RadioGroup rgStatus;
    private RadioButton rdInterested, rdNotInterested, rdOrdered;
    private ScrollView scParent;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout lnBottomSheet, lnPaidParent;
    private CoordinatorLayout coParent;
    private RelativeLayout rlHeader;


    private CustomerViewpagerAdapter viewpagerAdapter;
    private Customer currentCustomer;
    private List<Checkin> listCheckins = new ArrayList<>();
    private List<Bill> listBills = new ArrayList<>();
    private List<RecyclerView.Adapter> listAdapter;
    private Status currentStatus;
    private int currentState = BottomSheetBehavior.STATE_COLLAPSED;
    private String firstName = "";
    private float bottomSheetHeight;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitScrollWithFullscreen.assistActivity(this, 1);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_customer;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        coParent = (CoordinatorLayout) findViewById(R.id.customer_parent);
        rlHeader = (RelativeLayout) findViewById(R.id.customer_header);
        btnSubmit = (Button) findViewById(R.id.add_customer_submit);
        btnEditLocation = (CTextIcon) findViewById(R.id.add_customer_editlocation);
        btnCurrentLocation = (FloatingActionButton) findViewById(R.id.add_customer_currentlocation);
        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnShopCart = (FloatingActionButton) findViewById(R.id.add_customer_shopcart);
        tvTrash = (CTextIcon) findViewById(R.id.icon_more);
        tvTitle = (TextView) findViewById(R.id.add_customer_title);
        edName = (CInputForm) findViewById(R.id.add_customer_name);
        edPhone = (CInputForm) findViewById(R.id.add_customer_phone);
        edAdress = (CInputForm) findViewById(R.id.add_customer_adress);
        edStreet = (CInputForm) findViewById(R.id.add_customer_street);
        edDistrict = (CInputForm) findViewById(R.id.add_customer_district);
        edCity = (CInputForm) findViewById(R.id.add_customer_city);
        edNote = (CInputForm) findViewById(R.id.add_customer_note);
        edShopType = (CInputForm) findViewById(R.id.add_customer_shoptype);
        edShopName = (CInputForm) findViewById(R.id.add_customer_shopname);
        rgStatus = (RadioGroup) findViewById(R.id.customer_radiogroup_status);
        rdInterested = (RadioButton) findViewById(R.id.customer_radio_status_interested);
        rdNotInterested = (RadioButton) findViewById(R.id.customer_radio_status_nointerested);
        rdOrdered = (RadioButton) findViewById(R.id.customer_radio_status_ordered);
        scParent = (ScrollView) findViewById(R.id.customer_scrollview);
        viewPager = (ViewPager) findViewById(R.id.customer_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.customer_tabs);
        lnBottomSheet = (LinearLayout) findViewById(R.id.customer_bottom_sheet);
        lnPaidParent = (LinearLayout) findViewById(R.id.customer_paid_parent);
        tvTotal = (TextView) findViewById(R.id.customer_paid_total);
        tvDebt = (TextView) findViewById(R.id.customer_paid_debt);
        tvPaid = (TextView) findViewById(R.id.customer_paid_paid);
        tvDropdown = findViewById(R.id.add_customer_dropdown);

        //fix MapView in ScrollView
        mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scParent.requestDisallowInterceptTouchEvent(true);
            }
        });

    }

    @Override
    public void initialData() {
        setupMap();
        tabLayout.setupWithViewPager(viewPager);
        currentStatus = Status.getStatusList().get(0);

        String bundle = getIntent().getExtras().getString(Constants.CUSTOMER);
        if (bundle != null) {
            try {
                currentCustomer = new Customer(new JSONObject(bundle));
                firstName = currentCustomer.getString("name");

                edName.setText(currentCustomer.getString("name") == null ? "" : currentCustomer.getString("name"));
                edPhone.setText(currentCustomer.getString("phone") == null ? "" : currentCustomer.getString("phone"));
                edAdress.setText((currentCustomer.getString("address") == null ? "" : currentCustomer.getString("address")));
                edStreet.setText(currentCustomer.getString("street") == null ? "" : currentCustomer.getString("street"));
                edNote.setText(currentCustomer.getString("note"));
                edDistrict.setText(currentCustomer.getString("district"));
                edCity.setText(currentCustomer.getString("province"));
                edShopName.setText(currentCustomer.getString("signBoard"));
                edShopType.setText(Constants.getShopInfo(currentCustomer.getString("shopType"), null));
                tvTitle.setText(edShopType.getText().toString() + " - " + currentCustomer.getString("signBoard"));

                if (currentCustomer.getString("status") != null) {
                    currentStatus = new Status(currentCustomer.getJsonObject("status"));
                }

                switch (currentStatus.getInt("id")) {
                    case 1:
                        rdInterested.setChecked(true);
                        break;
                    case 2:
                        rdNotInterested.setChecked(true);
                        break;
                    case 3:
                        rdOrdered.setChecked(true);
                        break;
                }

                if (currentCustomer.getString("checkIns") != null) {
                    JSONArray array = new JSONArray(currentCustomer.getString("checkIns"));
                    for (int i = 0; i < array.length(); i++) {
                        listCheckins.add(new Checkin(array.getJSONObject(i)));
                    }
                }

                if (currentCustomer.getString("bills") != null) {
                    JSONArray array = new JSONArray(currentCustomer.getString("bills"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject objectBill = array.getJSONObject(i);
                        JSONObject objectUser = objectBill.getJSONObject("user");
                        if (User.getRole().equals(Constants.ROLE_ADMIN)){
                            listBills.add(new Bill(objectBill));
                        }else {
                            if (User.getId() == objectUser.getInt("id")){
                                listBills.add(new Bill(objectBill));
                            }
                        }

                    }
                }

                setupViewPager(listCheckins, listBills);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {

            if (currentCustomer.getInt("id") != 0) {
                tvTrash.setVisibility(User.getRole().equals("MANAGER") ? View.VISIBLE : View.GONE);

                if (currentCustomer.getJsonObject("distributor").getString("id").equals(Distributor.getDistributorId())) {
                    tvTrash.setVisibility(View.VISIBLE);
                } else {
                    tvTrash.setVisibility(View.GONE);
                }
                btnSubmit.setText("CHECK_IN & CẬP NHẬT");

            } else {

                tvTrash.setVisibility(View.GONE);
                btnSubmit.setText("CẬP NHẬT");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (edShopName.getText().toString().trim().equals("")) {
            edShopName.setSelection();
        } else {
            edPhone.setSelection();
        }


        edShopType.setDropdown(true);
        edShopType.setDropdownList(Util.arrayToList(Constants.shopName));

        edShopName.addTextChangeListenter(new CInputForm.OnQueryTextListener() {
            @Override
            public boolean textChanged(String query) {
                if (!firstName.contains("Anh ")) {
                    edName.setText("Anh " + query.substring(query.trim().lastIndexOf(" ") + 1));
                }
                tvTitle.setText(edShopType.getText().toString() + " - " + query);
                return true;
            }
        });

    }

    private void setupMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        rgStatus.setOnCheckedChangeListener(this);
        btnEditLocation.setOnClickListener(this);
        btnCurrentLocation.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
        coParent.getViewTreeObserver().addOnGlobalLayoutListener(this);
        tvDropdown.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupBottomSheet() {
        if (listBills.size() > 0) {
            lnPaidParent.setVisibility(View.VISIBLE);
            bottomSheetHeight = Util.convertDp2Px(88);

        } else {
            lnPaidParent.setVisibility(View.GONE);
            bottomSheetHeight = Util.convertDp2Px(58);

        }

        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) bottomSheetHeight);
        mBottomSheetBehavior.setState(currentState);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            int lastState = BottomSheetBehavior.STATE_COLLAPSED;

            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                currentState = newState;

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (tvTrash.getVisibility() == View.VISIBLE) {
                        tvTrash.setVisibility(View.GONE);
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (currentCustomer.getInt("id") != 0) {
                        tvTrash.setVisibility(User.getRole().equals("MANAGER") ? View.VISIBLE : View.GONE);
                    } else {
                        tvTrash.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setupViewPager(List<Checkin> listcheckin, List<Bill> listbills) {
        final List<Bill> listbill = new ArrayList<>();
        try {
            for (int i = 0; i < listbills.size(); i++) {
                if (listbills.get(i).getJsonObject("distributor").getString("id").equals(Distributor.getDistributorId())) {
                    listbill.add(listbills.get(i));
                }
            }

            showMoneyOverview(listbill);
            setupBottomSheet();

            int currentPosition = listbill.size();

            final List<String> listTitle = new ArrayList<>();
            listTitle.add(0, "CHECK_IN");
            listTitle.add(1, "HÓA ĐƠN");

            final List<RecyclerView.Adapter> listadapter = new ArrayList<>();

            listadapter.add(0, new CustomerCheckinsAdapter(listcheckin));
            listadapter.add(1, new CustomerBillsAdapter(listbill, new CallbackDeleteAdapter() {
                @Override
                public void onDelete(String data, int position) {
                    listbill.remove(position);
                    showMoneyOverview(listbill);
                    reloadBillCount(listbill.size());
                    setupBottomSheet();

                }
            }, new CallbackUpdateBill() {
                @Override
                public void onUpdate(final Bill bill, int position) {
                    showMoneyOverview(listbill);
                    Util.showToast("Thanh toán thành công");

                }
            }));

            viewpagerAdapter = new CustomerViewpagerAdapter(listadapter, listTitle);
            viewPager.setAdapter(viewpagerAdapter);
            viewPager.setCurrentItem(currentPosition);
            viewPager.setOffscreenPageLimit(2);

            createTabLayout(listTitle, listadapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createTabLayout(List<String> listTitle, List<RecyclerView.Adapter> listadapter) {
        for (int i = 0; i < listTitle.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(this).inflate(R.layout.view_tab_customer, null);
            TextView tabTextTitle = (TextView) customView.findViewById(R.id.tabNotify);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);
            textTitle.setText(listTitle.get(i));

            if (listadapter.get(i).getItemCount() <= 0) {
                tabTextTitle.setVisibility(View.GONE);
            } else {
                tabTextTitle.setVisibility(View.VISIBLE);
                tabTextTitle.setText(String.valueOf(listadapter.get(i).getItemCount()));
            }

            tab.setCustomView(customView);
        }
    }

    private void reloadBillCount(int count) {
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        TextView tabTextTitle = (TextView) tab.getCustomView().findViewById(R.id.tabNotify);

        if (count <= 0) {
            tabTextTitle.setVisibility(View.GONE);
        } else {
            tabTextTitle.setVisibility(View.VISIBLE);
            tabTextTitle.setText(String.valueOf(count));
        }

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.add_customer_submit:
                if (currentCustomer.getInt("id") == 0) {
                    if (currentStatus.getInt("id") == 1) {
                        loadReasonList();
                    } else {
                        submitCustomerCheckin(currentStatus, true);
                    }

                } else {
                    loadReasonList();

                }

                break;

            case R.id.icon_more:
                if (listBills.size() > 0) {
                    Util.showToast("Không thể xóa khách hàng có phát sinh hóa đơn");
                } else {
                    deleteCustomer();
                }

                break;

            case R.id.add_customer_editlocation:
                btnLocationEvent();

                break;

            case R.id.add_customer_currentlocation:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 18), 800, null);

                    }
                });

                break;

            case R.id.add_customer_shopcart:
                openShopCartScreen(getCurrentCustomer());

                break;

            case R.id.add_customer_dropdown:
                CustomDropdow.createDropdownStatus(tvDropdown, Status.getStatusList(), new CallbackString() {
                    @Override
                    public void Result(String s) {
                        edNote.setText(s);
                    }
                });
                break;
        }
    }

    private void returnPreviousScreen(String customer){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.CUSTOMER, customer != null? customer : null);
        setResult(Constants.RESULT_CUSTOMER_ACTIVITY,returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    private void openShopCartScreen(Customer customer){
        if (customer.getInt("id") == 0){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    final Customer responseCustomer = new Customer(result);
                    try {
                        currentCustomer.put("id", responseCustomer.getInt("id"));

                        responseCustomer.put("bills", "[]");
                        Transaction.gotoShopCartActivity(responseCustomer.CustomertoString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String error) {

                }
            }, true);

        }else {
            try {
                JSONArray array = new JSONArray(currentCustomer.getString("bills"));
                customer.put("bills", array.toString());
                Transaction.gotoShopCartActivity(customer.CustomertoString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private Boolean checkInputField(){
        Boolean check = false;
        if (edName.getText().toString().trim().equals("")
                || edDistrict.getText().toString().trim().equals("")
                || edCity.getText().toString().trim().equals("")
                || edShopName.getText().toString().trim().equals("")
                || edShopType.getText().toString().trim().equals("")){
            CustomCenterDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override public void onRespone(Boolean result) {

                }
            });

            check = false;

        }else {
            check = true;
        }
        return check;
    }

    private void submitCustomer(){
        if (checkInputField()){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    final Customer responseCustomer = new Customer(result);
                    returnPreviousScreen(responseCustomer.CustomertoString());

                }

                @Override
                public void onError(String error) {

                }
            }, true);
        }

    }

    private void submitCustomerCheckin(final Status status, final Boolean backtoMap){
        if (checkInputField()){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    final Customer responseCustomer = new Customer(result);
                    submitCheckin(responseCustomer, status, backtoMap);

                }

                @Override
                public void onError(String error) {

                }
            }, false);
        }

    }

    private void submitCheckin(final Customer customer, Status status, final Boolean backtoMap){
            String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
                    status.getInt("id"),
                    Util.encodeString(edNote.getText().toString().trim()),
                    User.getUserId()
            );

            CustomerConnect.PostCheckin(params, new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    if (backtoMap){
                        returnPreviousScreen(customer.CustomertoString());
                    }

                }

                @Override
                public void onError(String error) {

                }
            }, true);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Util.getInstance().getCurrentActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map));
        //mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        mMap.setOnCameraIdleListener(this);

        LatLng curLocation = new LatLng(currentCustomer.getDouble("lat"), currentCustomer.getDouble("lng"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 18), 800, null);

    }

    @Override
    public void onCameraIdle() {
        Util.hideKeyboard(edName);
        LatLng curLocation =  mMap.getCameraPosition().target;
        try {
            currentCustomer.put("lat", curLocation.latitude);
            currentCustomer.put("lng", curLocation.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteCustomer(){
        CustomCenterDialog.alertWithCancelButton(null, "Xóa khách hàng " + edShopType.getText().toString() +" " + currentCustomer.getString("signBoard"), "ĐỒNG Ý","HỦY", new CallbackBoolean() {
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
        backEvent();
    }

    private void backEvent(){
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else {
            returnPreviousScreen(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra(Constants.CUSTOMER_CART) != null && requestCode == Constants.RESULT_SHOPCART_ACTIVITY){
            try {
                Customer customer =new Customer(new JSONObject(data.getStringExtra(Constants.CUSTOMER_CART)));
                if (customer.getString("bills") != null ){
                    JSONArray array = new JSONArray(customer.getString("bills"));
                    if (array.length() >0){
                        rdOrdered.setChecked(true);
                        submitCustomerCheckin(currentStatus, false);
                    }
                    listBills = new ArrayList<>();
                    for (int i=0; i<array.length(); i++){
                        listBills.add(new Bill(array.getJSONObject(i)));
                    }

                    setupViewPager(listCheckins, listBills);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private void loadReasonList() {
        CustomCenterDialog.showCheckinReason("CHỌN TRẠNG THÁI CẬP NHẬT", Status.getStatusList(), new CartCheckinReasonAdapter.CallbackStatus() {
            @Override
            public void Status(Status status) {
                submitCustomerCheckin(status, true );

            }
            @Override
            public void UpdateOnly() {
                submitCustomer();
            }
        });

    }

    private Customer getCurrentCustomer(){
        Customer customer = new Customer();
        try {
            customer = currentCustomer;
            customer.put("name", edName.getText().toString().trim());
            customer.put("signBoard", edShopName.getText().toString().trim());
            customer.put("address", edAdress.getText().toString().trim());
            customer.put("phone", edPhone.getText().toString().trim());
            customer.put("street", edStreet.getText().toString().trim());
            customer.put("note", edNote.getText().toString().trim());
            customer.put("district", edDistrict.getText().toString().trim());
            customer.put("province", edCity.getText().toString().trim());
            customer.put("lat", currentCustomer.getDouble("lat"));
            customer.put("lng", currentCustomer.getDouble("lng"));
            customer.put("volumeEstimate", 10);
            customer.put("shopType", Constants.getShopInfo(null,edShopType.getText().toString()));
            customer.put("status.id", currentStatus.getInt("id"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customer;
    }

    private String createParamCustomer(Customer customer){
        String param = String.format(Api_link.CUSTOMER_CREATE_PARAM, customer.getInt("id") == 0? "" : String.format("id=%s&",customer.getString("id") ),
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
                customer.getInt("status.id"), //currentStatusId
                Distributor.getDistributorId() //DistributorId
        );

        return param;
    }

    @Override
    public void onGlobalLayout() {
        coParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int height = coParent.getHeight() - rlHeader.getHeight();

        ViewGroup.LayoutParams params = lnBottomSheet.getLayoutParams();
        params.height = height;
        lnBottomSheet.requestLayout();

        setupBottomSheet();

    }

    private void btnLocationEvent(){
        if (btnEditLocation.getText().toString().trim().equals(getResources().getString(R.string.icon_edit_map))){
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            btnEditLocation.setText(getResources().getString(R.string.icon_save));
            btnEditLocation.setBackgroundResource(R.drawable.bg_blue_rounded_button);
            btnCurrentLocation.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);

        }else if (
            btnEditLocation.getText().toString().trim().equals(getResources().getString(R.string.icon_save))){
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            btnEditLocation.setText(getResources().getString(R.string.icon_edit_map));
            btnEditLocation.setBackgroundResource(R.drawable.bg_red_rounded_button);
            btnCurrentLocation.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }

    }

    private void showMoneyOverview(List<Bill> list){
        tvTotal.setText("Tổng: " + Util.FormatMoney(Util.getTotalMoney(list)));
        tvPaid.setText("Trả: " + Util.FormatMoney(Util.getTotalPaid(list)));
        tvDebt.setText("Nợ: " + Util.FormatMoney(Util.getTotalDebt(list)));
    }

}
