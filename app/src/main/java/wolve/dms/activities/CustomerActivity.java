package wolve.dms.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
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
import wolve.dms.adapter.CustomerCheckinsAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.customviews.WorkaroundMapFragment;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.Checkin;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Province;
import wolve.dms.models.Status;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.apiconnect.CustomerConnect.createParamCustomer;
import static wolve.dms.utils.Constants.REQUEST_PERMISSION_LOCATION;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    public GoogleMap mMap;
    public SupportMapFragment mapFragment;
    private ImageView btnBack;
    private CTextIcon tvTrash, btnEditLocation;
//    tvBottomUp, tvBottomDown;
    private Button btnSubmit, btnShopCart;
    private FloatingActionButton btnCurrentLocation;
    private TextView  tvTotal, tvDebt , tvBillDetail, tvCheckInStatus, tvTime, tvCheckinTitle;
    protected TextView tvTitle;
    private EditText  edNote;
    private CInputForm edName, edPhone, edAdress, edStreet, edDistrict, edCity, edShopType, edShopName;
    private RadioGroup rgStatus;
    private RadioButton rdInterested, rdNotInterested, rdOrdered;
    private ScrollView scContent;
    private LinearLayout lnPaidParent, lnBottomSheet;
    private RelativeLayout rlHeader, rlStatusGroup;
    private CoordinatorLayout coParent;
    private RecyclerView rvCheckin;

    private BottomSheetBehavior mBottomSheetBehavior;
    protected BaseModel currentCustomer;
    private List<Checkin> listCheckins = new ArrayList<>();
    protected List<BaseModel> listBills = new ArrayList<>();
    private Status currentStatus;
    private String firstName = "";
    private FusedLocationProviderClient mFusedLocationClient;
    private long countTime;
    private int currentStateBottom = BottomSheetBehavior.STATE_COLLAPSED;
    private Fragment mFragment;
    protected List<String> mYears = new ArrayList<>();


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
        rlHeader = (RelativeLayout) findViewById(R.id.customer_header);
        btnSubmit = (Button) findViewById(R.id.add_customer_submit);
        btnEditLocation = (CTextIcon) findViewById(R.id.add_customer_editlocation);
        btnCurrentLocation = (FloatingActionButton) findViewById(R.id.add_customer_currentlocation);
        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnShopCart = (Button) findViewById(R.id.add_customer_shopcart);
        tvTrash = (CTextIcon) findViewById(R.id.icon_more);
        tvTitle = (TextView) findViewById(R.id.add_customer_title);
        edName = (CInputForm) findViewById(R.id.add_customer_name);
        edPhone = (CInputForm) findViewById(R.id.add_customer_phone);
        edAdress = (CInputForm) findViewById(R.id.add_customer_adress);
        edStreet = (CInputForm) findViewById(R.id.add_customer_street);
        edDistrict = (CInputForm) findViewById(R.id.add_customer_district);
        edCity = (CInputForm) findViewById(R.id.add_customer_city);
        edNote = (EditText) findViewById(R.id.add_customer_note);
        edShopType = (CInputForm) findViewById(R.id.add_customer_shoptype);
        edShopName = (CInputForm) findViewById(R.id.add_customer_shopname);
        rgStatus = (RadioGroup) findViewById(R.id.customer_radiogroup_status);
        rdInterested = (RadioButton) findViewById(R.id.customer_radio_status_interested);
        rdNotInterested = (RadioButton) findViewById(R.id.customer_radio_status_nointerested);
        rdOrdered = (RadioButton) findViewById(R.id.customer_radio_status_ordered);
        scContent = (ScrollView) findViewById(R.id.customer_scrollview);
        lnBottomSheet = (LinearLayout) findViewById(R.id.customer_bottom_sheet);
        lnPaidParent = (LinearLayout) findViewById(R.id.customer_paid_parent);
        tvTotal = (TextView) findViewById(R.id.customer_paid_total);
        tvDebt = (TextView) findViewById(R.id.customer_paid_debt);
//        tvPaid = (TextView) findViewById(R.id.customer_paid_paid);
//        tvDropdown = findViewById(R.id.add_customer_dropdown);
        rvCheckin = findViewById(R.id.add_customer_rvcheckin);
        tvBillDetail = findViewById(R.id.customer_bill_more);
        tvCheckInStatus = findViewById(R.id.customer_checkin_status);
        tvTime = findViewById(R.id.customer_checkin_time);
        rlStatusGroup = findViewById(R.id.customer_checkin_status_group);
//        tvBottomUp = findViewById(R.id.customer_bottom_up);
//        tvBottomDown = findViewById(R.id.customer_bottom_down);
        tvCheckinTitle = findViewById(R.id.customer_checkin_title);


        //fix MapView in ScrollView
        mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scContent.requestDisallowInterceptTouchEvent(true);
            }
        });

    }

    @Override
    public void initialData() {
        setupMap();
        currentStatus = Status.getStatusList().get(0);
        currentCustomer = new BaseModel(CustomSQL.getString(Constants.CUSTOMER));
        setCheckinGroup();
        try {
            firstName = currentCustomer.getString("name");
            edName.setText(currentCustomer.getString("name") == null ? "" : currentCustomer.getString("name"));
            edPhone.setText(currentCustomer.getString("phone") == null ? "" : currentCustomer.getString("phone"));
            edAdress.setText((currentCustomer.getString("address") == null ? "" : currentCustomer.getString("address")));
            edStreet.setText(currentCustomer.getString("street") == null ? "" : currentCustomer.getString("street"));
            setNoteText(currentCustomer.getString("note"));
//            edNote.setText(currentCustomer.getString("note"));
            edDistrict.setText(currentCustomer.getString("district"));
            edCity.setText(currentCustomer.getString("province"));
            edShopName.setText(currentCustomer.getString("signBoard"));
            edShopType.setText(Constants.getShopTitle(currentCustomer.getString("shopType"), null));
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
            createRVCheckin(listCheckins);
            lnBottomSheet.setVisibility(listCheckins.size() >0?View.VISIBLE: View.GONE);

            if (currentCustomer.getString("bills") != null) {
                JSONArray array = new JSONArray(currentCustomer.getString("bills"));
                List<BaseModel> mLists = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject objectBill = array.getJSONObject(i);
                    JSONObject objectUser = objectBill.getJSONObject("user");
                    if (User.getRole().equals(Constants.ROLE_ADMIN)){
                        mLists.add(new Bill(objectBill));
                    }else {
                        if (User.getId() == objectUser.getInt("id")){
                            mLists.add(new Bill(objectBill));
                        }
                    }
                    addYearToList(Util.YearString(objectBill.getLong("createAt")));

                }

                showMoneyOverview(mLists);

            }

            if (currentCustomer.isNull("bills")){
                lnPaidParent.setVisibility(View.GONE);
            }

            if (currentCustomer.getInt("id") != 0) {
                tvTrash.setVisibility(User.getRole().equals("MANAGER") ? View.VISIBLE : View.GONE);

                if (currentCustomer.getJsonObject("distributor").getString("id").equals(Distributor.getDistributorId())) {
                    tvTrash.setVisibility(View.VISIBLE);
                } else {
                    tvTrash.setVisibility(View.GONE);
                }

            } else {
                tvTrash.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (edShopName.getText().toString().trim().equals("")) {
            edShopName.setSelection();
        } else {
            edPhone.setSelection();
        }


    }

    private void addYearToList(String year){
        if (mYears.size() ==0){
            mYears.add(0, Constants.ALL_FILTER);
        }
        if (!mYears.get(mYears.size()-1).equals(year)){
            mYears.add(year);
        }
    }

    private void setNoteText(String note){
        if (!note.isEmpty()){
            try {
                JSONObject object = new JSONObject(note);
                edNote.setText(object.getString("note"));

            } catch (JSONException e) {
                edNote.setText(note);
            }
        }

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tvBillDetail.setOnClickListener(this);
        tvCheckinTitle.setOnClickListener(this);
        shopTypeEvent();
        shopNameEvent();
        setProvinceEvent();
        setDistrictEvent();

    }

    private void setCheckinGroup(){
        if (CustomSQL.getBoolean(Constants.CHECKIN_FLAG)){
            countTime = Util.CurrentTimeStamp() - CustomSQL.getLong(Constants.CHECKIN_TIME);
            rlStatusGroup.setBackground(getResources().getDrawable(R.color.colorBlueTransparent));
            tvCheckInStatus.setText("Đang check in ....");
            tvCheckInStatus.setTextColor(getResources().getColor(R.color.black_text_color_hint));
            tvTime.setVisibility(View.VISIBLE);
            threadShowTime.start();
            btnSubmit.setText("CHECK_IN & CẬP NHẬT");

        }else {
            tvTime.setVisibility(View.GONE);
            rlStatusGroup.setBackground(getResources().getDrawable(R.color.colorRedTransparent));
            tvCheckInStatus.setText("Đang bên ngoài khu vực cửa hàng ....");
            tvCheckInStatus.setTextColor(getResources().getColor(R.color.white_text_color));
            btnSubmit.setText("CẬP NHẬT");

        }
    }

    private void createRVCheckin(List<Checkin> list){
        CustomerCheckinsAdapter adapter = new CustomerCheckinsAdapter(list);
        adapter.notifyDataSetChanged();
        rvCheckin.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCheckin.setLayoutManager(layoutManager);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.add_customer_submit:
                submitEvent();

                break;

            case R.id.icon_more:
                if (listBills.size() > 0) {
                    Util.showToast("Không thể xóa khách hàng có phát sinh hóa đơn");

                } else if (User.getRole().equals(Constants.ROLE_ADMIN)){
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


            case R.id.customer_bill_more:
                changeFragment(new BillDetailFragment() , true);
                Util.smoothImageRotation(btnBack, -90);


                break;

            case R.id.customer_checkin_title:
                mBottomSheetBehavior.setState(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ?
                BottomSheetBehavior.STATE_EXPANDED:BottomSheetBehavior.STATE_COLLAPSED);


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

    protected void openShopCartScreen(BaseModel customer){
        if (customer.getInt("id") == 0){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackCustom() {
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

    private Boolean checkInputField(){
        if (edName.getText().toString().trim().equals("")
                || edDistrict.getText().toString().trim().equals("")
                || edCity.getText().toString().trim().equals("")
                || edShopName.getText().toString().trim().equals("")
                || edShopType.getText().toString().trim().equals("")){
            CustomCenterDialog.alertWithButton("", "Vui lòng nhập đủ thông tin", "đồng ý", new CallbackBoolean() {
                @Override public void onRespone(Boolean result) {


                }
            });

            return false;

        }else {
            return true;
        }
    }

    private void submitCustomer(final CallbackObject mListener, Boolean loading){
        if (checkInputField()){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackCustom() {
                @Override
                public void onResponse(BaseModel result) {
                    mListener.onResponse(result.convertJsonObject());
                }

                @Override
                public void onError(String error) {

                }
            }, loading);
        }

    }

    private void submitCustomerAndCheckin(final String statusNote){
        if (checkInputField()){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackCustom() {
                @Override
                public void onResponse(BaseModel result) {
                    submitCheckin(result, statusNote);
                }

                @Override
                public void onError(String error) {

                }
            }, false);
        }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Util.getInstance().getCurrentActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map));
        mMap.setMyLocationEnabled(true);
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
//        try {
            currentCustomer.put("lat", curLocation.latitude);
            currentCustomer.put("lng", curLocation.longitude);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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
        mFragment = getSupportFragmentManager().findFragmentById(R.id.customer_parent);
        if (Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);

        }else if(mFragment != null && mFragment instanceof BillDetailFragment) {
            getSupportFragmentManager().popBackStack();
            Util.smoothImageRotation(btnBack, 90);

        }else {
            returnPreviousScreen(null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);

        if (requestCode == Constants.RESULT_SHOPCART_ACTIVITY){
            if (data.getStringExtra(Constants.SHOP_CART_ACTIVITY).equals(Constants.RELOAD_DATA)){
                reloadBills(new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result){
                        CustomCenterDialog.alertWithCancelButton(
                                "Done!",
                                "Bạn vừa hoàn tất hóa đơn. Bạn có muốn trở lại màn hình bản đồ",
                                "Đồng ý",
                                "Không", new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                if (result){
                                    submitCustomerAndCheckin("Đã mua hàng" );
                                }

                            }
                        });
                    }
                });
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

    protected BaseModel getCurrentCustomer(){
        BaseModel customer = new Customer();
//        try {
            customer = currentCustomer;
            customer.put("name", edName.getText().toString().trim());
            customer.put("signBoard", edShopName.getText().toString().trim());
            customer.put("address", edAdress.getText().toString().trim());
            customer.put("phone", edPhone.getText().toString().trim());
            customer.put("street", edStreet.getText().toString().trim());
//            customer.put("note", edNote.getText().toString().trim());
            customer.put("note", getCustomerNote());
            customer.put("district", edDistrict.getText().toString().trim());
            customer.put("province", edCity.getText().toString().trim());
            customer.put("lat", currentCustomer.getDouble("lat"));
            customer.put("lng", currentCustomer.getDouble("lng"));
            customer.put("volumeEstimate", 10);
            customer.put("shopType", Constants.getShopTitle(null,edShopType.getText().toString()));
            customer.put("status.id", currentStatus.getInt("id"));
            customer.put("debt", Util.getTotal(listBills).getDouble("debt") );

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return customer;
    }

    private String getCustomerNote(){
        String note = "";
        Double total = Util.getTotal(listBills).getDouble("debt");
        if (total > 0.0){
            try{
                JSONObject object = new JSONObject();
                object.put("debt", total);
                object.put("note", edNote.getText().toString());
                object.put("userId", listBills.get(listBills.size()-1).getJsonObject("user").getInt("id"));
                object.put("userName", listBills.get(listBills.size()-1).getJsonObject("user").getString("displayName"));
                note = object.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            note = edNote.getText().toString();
        }

        return note;
    }

    @Override
    public void onGlobalLayout() {
        coParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//        int height = coParent.getHeight() - rlHeader.getHeight();
        ViewGroup.LayoutParams params = lnBottomSheet.getLayoutParams();
        params.height = (int) (coParent.getHeight()*0.6);
        lnBottomSheet.requestLayout();

        setupBottomSheet();

    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottomSheet);
        int height = getResources().getDimensionPixelSize(R.dimen._34sdp);
//        mBottomSheetBehavior.setPeekHeight(Util.convertDp2PxInt(60-8));
        mBottomSheetBehavior.setPeekHeight(height);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                Util.hideKeyboard(lnBottomSheet);
                switch (newState){
                    case BottomSheetBehavior.STATE_DRAGGING:
//                        tvBottomDown.setVisibility(View.INVISIBLE);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        tvBottomDown.setVisibility(View.INVISIBLE);
//                        tvBottomUp.setVisibility(View.VISIBLE);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        Util.hideKeyboard(lnBottomSheet);
//                        tvBottomDown.setVisibility(View.VISIBLE);
//                        tvBottomUp.setVisibility(View.INVISIBLE);
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                tvBottomUp.setAlpha(1-slideOffset);
            }
        });
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

    protected void showMoneyOverview(List<BaseModel> listbill ){
//        listBills = DataUtil.mergeWithReturnBill(listbill);
        listBills = DataUtil.remakeBill(listbill);
        BaseModel object = Util.getTotal(listBills);

        lnPaidParent.setVisibility(object.getInt("size") > 0 ?View.VISIBLE :View.GONE);
        tvTotal.setText(String.format("Tổng: %s" ,Util.FormatMoney(object.getDouble("total"))));
        tvDebt.setText(String.format("Nợ: %s" ,Util.FormatMoney(object.getDouble("debt"))));

    }

    protected void reloadBills(final CallbackBoolean mListener){
        String param = currentCustomer.getString("id");
        CustomerConnect.GetCustomerDetail(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel customer) {
                List<BaseModel> mList = new ArrayList<>();
                if (customer.getString("bills") != null) {
                    //JSONArray array = customer.getJSONArray("bills");
                    List<BaseModel> listBill = DataUtil.array2ListBaseModel(customer.getJSONArray("bills"));

                    for (int i = 0; i < listBill.size(); i++) {
                        //BaseModel objectBill = new BaseModel(array.getJSONObject(i);
                        BaseModel objectUser = new BaseModel(listBill.get(i).getJsonObject("user"));
                        if (User.getRole().equals(Constants.ROLE_ADMIN)) {
                            mList.add(listBill.get(i));

                        } else {
                            if (User.getId() == objectUser.getInt("id")) {
                                mList.add(listBill.get(i));
                            }
                        }
                        addYearToList(Util.YearString(listBill.get(i).getLong("createAt")));

                    }

                    currentCustomer.put("bills", customer.getJSONArray("bills"));
                    showMoneyOverview(mList);
                    mListener.onRespone(true);


                    if (mList.size() > 0) {
                        rdOrdered.setChecked(true);
                    }
                }

            }

            @Override
            public void onError(String error) {
                mListener.onRespone(false);
            }
        }, true);
    }

    private void shopTypeEvent(){
        edShopType.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog.choiceList("CHỌN NHÓM CỬA HÀNG",Util.arrayToList(Constants.shopName) , new CustomBottomDialog.StringListener() {
                    @Override
                    public void onResponse(String content) {
                        edShopType.setText(content);
                    }
                });

            }
        });

    }

    private void shopNameEvent(){
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

    private void setProvinceEvent(){
        edCity.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                final String curentProvince = edCity.getText().toString();

                CustomBottomDialog.choiceList("CHỌN TỈNH", Province.getListProvince(), new CustomBottomDialog.StringListener() {
                    @Override
                    public void onResponse(String content) {
                        edCity.setText(content);
                        if (!edCity.getText().toString().equals(curentProvince)){
                            edDistrict.setText("");
                        }
                    }
                });

            }
        });

    }

    private void setDistrictEvent(){
        edDistrict.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                SystemConnect.getDistrict(Province.getDistrictId(edCity.getText().toString()), new CallbackCustomList() {
                    @Override
                    public void onResponse(List<BaseModel> results) {
                        List<String> list = new ArrayList<>();
                        for (int i=0; i<results.size(); i++){
                            //JSONObject object = result.getJSONObject(i);
                            if (results.get(i).getString("name").matches(Util.DETECT_NUMBER)){
                                list.add(String.format("%s %s",results.get(i).getString("type"),results.get(i).getString("name")));
                            }else {
                                list.add(results.get(i).getString("name"));
                            }

                        }

                        CustomBottomDialog.choiceList("CHỌN QUẬN/HUYỆN",list , new CustomBottomDialog.StringListener() {
                            @Override
                            public void onResponse(String content) {
                                edDistrict.setText(content);

                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Util.showToast("Không thể lấy danh sách quận/ huyện");

                    }
                });

            }
        });

    }

    private void submitEvent(){
        if (CustomSQL.getBoolean(Constants.CHECKIN_FLAG)){
            showCheckinStatus();

        }else {
            submitCustomer(new CallbackObject() {
                @Override
                public void onResponse(JSONObject object) {
                    returnPreviousScreen(object.toString());
                }
            }, true);

        }
    }

    private String createParamCheckin(BaseModel customer, String note){
        String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
                currentStatus.getInt("id"),
                Util.encodeString(String.format("[%s] %s", Util.HourStringNatural(countTime), note)),
                User.getUserId()
        );
        return params;
    }



}
