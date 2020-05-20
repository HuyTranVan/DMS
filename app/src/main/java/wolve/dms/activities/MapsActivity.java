package wolve.dms.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CustomWindowAdapter;
import wolve.dms.adapter.Customer_SearchAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackLong;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.LatlngListener;
import wolve.dms.customviews.CButton;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Customer;
import wolve.dms.models.District;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMapLongClickListener, RadioGroup.OnCheckedChangeListener,
        GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnCameraMoveListener,
        ViewTreeObserver.OnGlobalLayoutListener{
    public GoogleMap mMap;
    private FloatingActionMenu btnNewCustomer;
    private CardView btnLocation;
    private FloatingActionButton  btnRepair, btnPhoneNumber;
    public SupportMapFragment mapFragment;
    private RadioGroup rdFilter;
    private RadioButton rdAll, rdIntersted, rdOrdered;
    private ImageView btnBack, btnClose;
    private EditText edSearch;
    private CoordinatorLayout coParent;
    private SmoothProgressBar progressLoading;
    private RelativeLayout rlSearchLayout;
    private RecyclerView rvSearch;
    private LinearLayout lnSheetBody, lnBottomSheet ;
    private CButton btnDirection, btnCall, btnShare, btnAddList;
    private TextView tvShopname, tvAddress, tvTempBill, tvListWating,
            tvLocation, tvReload, tvStatusDot, tvStatus, tvCheckin, tvDistance;

    private Handler mHandlerMoveMap = new Handler();
    private Handler mHandlerSearch = new Handler();
    private String currentPhone;
    private Boolean recheckGPS = false;
    private String mSearchText = "";
    private Customer_SearchAdapter mSearchAdapter;
    private Boolean loadCustomer = false;
    private CustomWindowAdapter adapterInfoMarker;
    private Marker currentMarker;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ProgressBar progressLoadCustomer;
    protected List<BaseModel> listCustomerWaiting = new ArrayList<>();
    private List<Marker> listMarker = new ArrayList<>();
    private Fragment mFragment;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_map;
    }

    @Override
    public int setIdContainer() { return R.id.map_parent; }

    @Override
    public void findViewById() {
        mapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btnLocation = findViewById(R.id.map_current_location);
        btnNewCustomer = (FloatingActionMenu) findViewById(R.id.map_new_customer);
        btnRepair = (FloatingActionButton) findViewById(R.id.map_new_repair);
        btnPhoneNumber = (FloatingActionButton) findViewById(R.id.map_new_addphone);
        rdFilter = (RadioGroup) findViewById(R.id.map_filter);
        rdAll = findViewById(R.id.map_filter_all);
        rdIntersted = findViewById(R.id.map_filter_interested);
        rdOrdered = findViewById(R.id.map_filter_ordered);
        coParent = (CoordinatorLayout) findViewById(R.id.map_parent);
        progressLoading = findViewById(R.id.map_loading);
        btnBack = findViewById(R.id.icon_back);
        btnClose = findViewById(R.id.icon_close);
        tvLocation = findViewById(R.id.custom_search_location);
        tvReload = findViewById(R.id.map_reload);
        edSearch = findViewById(R.id.maintext);
        rlSearchLayout = findViewById(R.id.map_search_bạckground);
        lnSheetBody = findViewById(R.id.map_bottom_sheet_body);
        btnNewCustomer.setMenuButtonColorNormalResId(R.color.colorBlue);
        btnNewCustomer.setMenuButtonColorPressedResId(R.color.colorBlueDark);
        rvSearch = findViewById(R.id.map_rvsearch);
        lnBottomSheet = (LinearLayout) findViewById(R.id.map_bottom_sheet);
        tvShopname = findViewById(R.id.map_detail_shopname);
        tvAddress = findViewById(R.id.map_detail_address);
        tvCheckin = findViewById(R.id.map_detail_checkin_text);
        btnCall = findViewById(R.id.map_detail_call);
        btnDirection = findViewById(R.id.map_detail_direction);
        btnShare= findViewById(R.id.map_detail_share);
        btnAddList = findViewById(R.id.map_detail_add);
        tvDistance = findViewById(R.id.map_detail_distance);
        progressLoadCustomer = findViewById(R.id.map_loading_customer);
        tvStatus = findViewById(R.id.map_detail_status);
        tvStatusDot = findViewById(R.id.map_detail_status_dot);
        tvTempBill = findViewById(R.id.map_detail_tempbill);
        tvListWating = findViewById(R.id.map_waiting_list_text);


    }

    @Override
    public void initialData() {
        Util.hideKeyboard(edSearch);
        Util.mapsActivity = this;
        MapUtil.customers = new ArrayList<>();
        MapUtil.markers = new ArrayList<>();
        edSearch.setFocusable(false);
        tvReload.setVisibility(loadCustomer ? View.GONE : View.VISIBLE);
        getWaitingList();
    }

    @Override
    public void addEvent() {
        mapFragment.getMapAsync(this);
        btnLocation.setOnClickListener(this);
        btnRepair.setOnClickListener(this);
        rdFilter.setOnCheckedChangeListener(this);
        btnPhoneNumber.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        edSearch.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        tvListWating.setOnClickListener(this);
        searchEvent();

        tvReload.setOnClickListener(this);
        coParent.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recheckGPS != null && recheckGPS)
            checkGPS();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_current_location:
                loadCustomer = true;
                tvLocation.setTextColor(getResources().getColor(R.color.black_text_color_hint));
                getCurrentLocation(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        triggerCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()), true);
                    }
                });

                break;

            case R.id.map_new_repair:
                btnNewCustomer.close(true);
                getCurrentLocation(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null){
                            inputShopName(location.getLatitude(),location.getLongitude());
                        }

                    }
                });

                break;

            case R.id.map_new_addphone:
                btnNewCustomer.close(true);
                getCurrentLocation(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null){
                            inputPhoneNumber(location.getLatitude(),location.getLongitude());
                        }

                    }
                });


                break;

            case R.id.icon_back:
                backPress();
                break;

            case R.id.custom_search_location:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showListDistrict();
                break;

            case R.id.maintext:
                openSearch();

                break;

            case R.id.icon_close:
                edSearch.setText("");

                break;

            case R.id.map_reload:
                tvReload.setVisibility(View.GONE);
                loadCustomer = true;
                loadCustomerAtCurrent();
                break;

            case R.id.map_waiting_list_text:
                Util.hideKeyboard(view);

                showListWaiting();


                break;

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map));
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        adapterInfoMarker = new CustomWindowAdapter(new LatlngListener() {
            @Override
            public void onLatlngChange(LatLng latlng) {
                triggerCurrentLocation(latlng, false);
            }
        });
        mMap.setInfoWindowAdapter(adapterInfoMarker);
        checkGPS();

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Util.hideKeyboard(btnNewCustomer);
        btnNewCustomer.close(true);
        mMap.setOnCameraMoveListener(MapsActivity.this);
        loadCustomer = true;
        tvReload.setVisibility(loadCustomer ? View.GONE : View.VISIBLE);

        unsetCurrentMarker();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setNullButton();
    }

    @Override
    public void onCameraIdle() {

    }

    private void showListDistrict() {
        CustomBottomDialog.choiceListObject("Chọn quận / huyện", District.getDistricts(),"text", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                tvLocation.setTextColor(getResources().getColor(R.color.colorMain));
                loadCustomer = false;
                loadCustomersByDistrict(object.getString("text"));

            }
        }, null);

    }

    public void triggerCurrentLocation(LatLng latLng, Boolean loadAround) {
        if (latLng != null) {
            loadCustomer = loadAround;

            tvReload.setVisibility(loadCustomer?View.GONE:View.VISIBLE);
            float zoom = mMap.getCameraPosition().zoom < 15 ? 15 : mMap.getCameraPosition().zoom;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 800, null);

        }

    }

    public void onLocationChanged(Location loc) {
        CameraPosition currentPlace = new CameraPosition.Builder()
                        .target(new LatLng(loc.getLatitude(), loc.getLongitude()))
                        .tilt(0f)
                        .zoom(18)
                        .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 200, null);
    }

    private void inputShopName(double lat, double lng) {
        CustomInputDialog.inputShopName(coParent, new CustomInputDialog.ShopNameListener() {
            @Override
            public void onShopname(String shopname, int shoptype) {
                createCustomerFast(shoptype, shopname, "", lat, lng);
            }



        });

    }

    private void inputPhoneNumber(double lat, double lng) {
        CustomInputDialog.inputPhoneNumber(coParent,new CallbackString() {
            @Override
            public void Result(String s) {
                createCustomerFast(0,"-", s, lat, lng);

            }
        });

    }

    private void loadCustomersByDistrict(String district) {
        String param = "district=" + Util.encodeString(district);
        CustomerConnect.ListCustomer(param, 500, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                List<BaseModel> tempCustomers = new ArrayList<>();
                for (BaseModel model : results){
                    tempCustomers.add(buildMarkerByCustomer(model));
                }
                addListMarkertoMap(mMap,true, tempCustomers,true);

            }

            @Override
            public void onError(String error) {
            }
        }, true,true);

    }

    private void loadCustomersByLocation(final Boolean clearMap, Double lat, Double lng) {
        progressLoading.setVisibility(View.VISIBLE);

        CustomerConnect.ListCustomerLocation(Util.encodeString(String.valueOf(lat)), Util.encodeString(String.valueOf(lng)), new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                progressLoading.setVisibility(View.GONE);
                List<BaseModel> tempCustomers = new ArrayList<>();
                for (BaseModel model : results){
                    tempCustomers.add(buildMarkerByCustomer(model));
                }
                addListMarkertoMap(mMap, clearMap, tempCustomers,false);

            }

            @Override
            public void onError(String error) {
                progressLoading.setVisibility(View.GONE);
            }

        }, true);
    }

    private BaseModel buildMarkerByCustomer(BaseModel customer) {
        BaseModel currentCustomer = customer;
        int customerStatus = currentCustomer.getInt("status_id");

        if (customerStatus == 0){
            currentCustomer.put("icon", R.drawable.ico_pin_pink);
            currentCustomer.put("checkincount","N");
            currentCustomer.put("statusDetail", "Khách hàng mới");
            currentCustomer.put("statusColor", getResources().getColor(R.color.colorPink));
            currentCustomer.put("statusInterested", true);

        }else if (customerStatus == 1) {
            currentCustomer.put("icon", R.drawable.ico_pin_red);
            currentCustomer.put("checkincount", customer.getInt("checkinCount") > 99? "99+" : customer.getInt("checkinCount"));
            currentCustomer.put("statusDetail", "Khách hàng có quan tâm");
            currentCustomer.put("statusColor", getResources().getColor(R.color.orange_dark));
            currentCustomer.put("statusInterested", true);

        } else if (customerStatus == 2) {
            currentCustomer.put("icon", R.drawable.ico_pin_grey);
            currentCustomer.put("checkincount", customer.getInt("checkinCount") > 99? "99+" : customer.getInt("checkinCount"));
            currentCustomer.put("statusDetail", "Khách hàng không quan tâm");
            currentCustomer.put("statusColor", getResources().getColor(R.color.black_text_color_hint));
            currentCustomer.put("statusInterested", false);

        } else {
            currentCustomer.put("icon", R.drawable.ico_pin_blue);
            currentCustomer.put("checkincount", customer.getInt("checkinCount") > 99? "99+" : customer.getInt("checkinCount"));
            currentCustomer.put("statusDetail", "Khách đã mua hàng" );
            currentCustomer.put("statusColor", getResources().getColor(R.color.colorBlue));
            currentCustomer.put("statusInterested", true);

        }

        return currentCustomer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        if (mMap != null){
            mMap.setOnCameraMoveListener(MapsActivity.this);
            if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY) {
                BaseModel cust = new BaseModel(data.getStringExtra(Constants.CUSTOMER));

                if (cust.hasKey("deleted") && cust.getBoolean("deleted")) {
                    removeCustomer(cust.getString("id"));

                }else {
                    reUpdateMarkerDetail(cust);

                }

            }else {
                onResume();
            }

        }else {
            onResume();
        }

    }

    private void removeCustomer(String id){
        currentMarker =null;
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        for (int i=0; i< listMarker.size(); i++){
            if (listMarker.get(i).getTitle().equals(id)){
                listMarker.get(i).remove();
                listMarker.remove(i);
                CustomSQL.removeKey(Constants.CUSTOMER);
                break;
            }
        }
    }

    private void addMarkerToList(BaseModel  customer){
        boolean isNewMarker = true;
        for (int i=0; i< listMarker.size(); i++){
            if (listMarker.get(i).getTitle().equals(customer.getString("id"))){
                listMarker.get(i).setTag(buildMarkerByCustomer(customer).BaseModelstoString());
                currentMarker = listMarker.get(i);
                isNewMarker = false;
                break;
            }
        }

        if (isNewMarker){
            currentMarker = MapUtil.addMarkerToMap(mMap, buildMarkerByCustomer(customer), Constants.MARKER_ALL);
            listMarker.add(currentMarker);

        }
    }

    private void reUpdateMarkerDetail(BaseModel  customer){
        unsetCurrentMarker();
        addMarkerToList(customer);

        if (currentMarker.getTag() != null) {
            getDistanceFromCurrent(customer.getDouble("lat"), customer.getDouble("lng"), new CallbackLong() {
                @Override
                public void onResponse(Long value) {
                    Bitmap bitmap = MapUtil.GetBitmapMarker(MapsActivity.this, customer.getInt("icon"), customer.getString("checkincount"), R.color.pin_waiting);
                    currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*2,bitmap.getHeight()*2, false)));

                    updateBottomDetail(customer, value);
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.map_filter_all:
                MapUtil.updateCustomerFilter(listMarker, Constants.MARKER_ALL);

                break;

            case R.id.map_filter_interested:
                MapUtil.updateCustomerFilter(listMarker, Constants.MARKER_INTERESTED);

                break;

            case R.id.map_filter_ordered:
                MapUtil.updateCustomerFilter(listMarker, Constants.MARKER_ORDERED);

                break;
        }
    }

    protected void addListMarkertoMap(GoogleMap map, Boolean clearMap, List<BaseModel> customers, Boolean isBound) {
        if (clearMap){
            listMarker = new ArrayList<>();
            map.clear();
            for (BaseModel maker: customers){
                listMarker.add(MapUtil.addMarkerToMap(map, maker, getCheckedFilter()));

            }

        }else {
            if (listMarker == null){
                listMarker = new ArrayList<>();
            }
            for (BaseModel maker: customers){
                if (!DataUtil.checkDuplicateMarker(listMarker, maker)){
                    listMarker.add(MapUtil.addMarkerToMap(map, maker, getCheckedFilter()));

                }
            }

        }
        reupdateStatusCount();
        if (isBound){
            MapUtil.reboundMap(map, listMarker);
        }

    }

    private void reupdateStatusCount(){
        BaseModel count = DataUtil.countMarkerStatus(listMarker);
        rdAll.setText(count.getInt(Constants.MARKER_ALL)> 0? String.format("Tất cả: %d", count.getInt(Constants.MARKER_ALL)) : "Tất cả");
        rdIntersted.setText(count.getInt(Constants.MARKER_INTERESTED)> 0? String.format("Quan tâm: %d", count.getInt(Constants.MARKER_INTERESTED)) : "Quan tâm");
        rdOrdered.setText(count.getInt(Constants.MARKER_ORDERED)> 0? String.format("Đã mua: %d", count.getInt(Constants.MARKER_ORDERED)) : "Đã mua");

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        final Customer customer = new Customer((JSONObject) marker.getTag());
        currentPhone = customer.getString("phone");

        if (!currentPhone.equals("")) {
            CustomCenterDialog.alertWithCancelButton(null, "Gọi điện thoại cho " + customer.getString("name") + " (" + customer.getString("phone") + ")", "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    Transaction.openCallScreen(currentPhone);

                }
            });
        } else {
            Util.showSnackbar("Khách hàng chưa có số điện thoại", null, null);
        }


    }

    @Override
    public void onCameraMove() {
        mHandlerMoveMap.removeCallbacks(mFilterTask);
        mHandlerMoveMap.postDelayed(mFilterTask, 1000);

    }

    private Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            if (loadCustomer){
                loadCustomerAtCurrent();
            }

        }
    };

    private void loadCustomerAtCurrent(){
        LatLng curLocation = mMap.getCameraPosition().target;
        loadCustomersByLocation(false, curLocation.latitude, curLocation.longitude);
    }

    private String getCheckedFilter() {
        if (rdFilter.getCheckedRadioButtonId() == R.id.map_filter_all) {
            return Constants.MARKER_ALL;

        } else if (rdFilter.getCheckedRadioButtonId() == R.id.map_filter_interested) {
            return Constants.MARKER_INTERESTED;

        } else {
            return Constants.MARKER_ORDERED;

        }

    }

    private void unsetCurrentMarker(){
        if (currentMarker != null){
            final BaseModel cust = new BaseModel(currentMarker.getTag().toString());
            Bitmap bitmap = MapUtil.GetBitmapMarker(this, cust.getInt("icon"), cust.getString("checkincount"), R.color.pin_waiting);
            currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        unsetCurrentMarker();
        currentMarker = marker;

        if (marker.getTag() != null) {
            final BaseModel customer = new BaseModel(marker.getTag().toString());

            getDistanceFromCurrent(customer.getDouble("lat"), customer.getDouble("lng"), new CallbackLong() {
                @Override
                public void onResponse(Long value) {
                    Bitmap bitmap = MapUtil.GetBitmapMarker(MapsActivity.this, customer.getInt("icon"), customer.getString("checkincount"), R.color.pin_waiting);
                    assert bitmap != null;
                    currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*2,bitmap.getHeight()*2, false)));


                    updateBottomDetail(customer, value);
                }
            });

        }

        return true;
    }

    public void checkGPS() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        //Todo check GPS is enable?
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        assert mLocationManager != null;
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            CustomCenterDialog.alertWithButton("Xác thực quyền truy cập vị trí", "Bạn cần mở GPS truy cập vị trí để sử dụng toàn bộ tính năng phần mềm", "Bật GPS", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    recheckGPS = true;
                    startActivity(intent);

                }
            });

        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getCurrentLocation(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        triggerCurrentLocation(latLng, true);
                    }

                }
            });
        }


    }

    private void backPress(){
        mFragment = getSupportFragmentManager().findFragmentById(R.id.map_parent);
        if (mFragment != null && mFragment instanceof MapWaitingListFragment) {
            getSupportFragmentManager().popBackStack();

        }  else if (rlSearchLayout.getVisibility() == View.VISIBLE){
            closeSearch();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    private void searchEvent(){
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    btnClose.setVisibility(View.VISIBLE);

                    if (!s.toString().isEmpty() && s.toString().length()>1){
                        mSearchText = s.toString();
                        mHandlerSearch.removeCallbacks(delayForSerch);
                        mHandlerSearch.postDelayed(delayForSerch, 500);

                }

                }else {
                    btnClose.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openSearch(){
        rlSearchLayout.setVisibility(View.VISIBLE);
        edSearch.setFocusable(true);
        edSearch.setFocusableInTouchMode(true);
        edSearch.setSelection(edSearch.getText().toString().isEmpty()?0 :edSearch.getText().toString().length() );
        Util.showKeyboard(edSearch);
        tvLocation.setVisibility(View.GONE);
        tvReload.setVisibility(View.GONE);
        tvListWating.setVisibility(View.GONE);

        unsetCurrentMarker();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setNullButton();

    }

    private void closeSearch(){
        edSearch.setText("");
        rlSearchLayout.setVisibility(View.GONE);
        Util.hideKeyboard(edSearch);
        edSearch.setFocusable(false);
        tvLocation.setVisibility(View.VISIBLE);
        tvReload.setVisibility(loadCustomer?View.VISIBLE:View.GONE);
        tvListWating.setVisibility(View.VISIBLE);

    }

    private Runnable delayForSerch = new Runnable() {
        @Override
        public void run() {
            String param = "";
            if(mSearchText.matches(Util.DETECT_NUMBER)) {
                param = "phone=" + Util.encodeString(mSearchText);
            }
            else{
                param = "shopName=" + Util.encodeString(mSearchText);
            }

            CustomerConnect.ListCustomer(param, 10, new CallbackCustomList() {
                @Override
                public void onResponse(List<BaseModel> results) {
                    createRVList(results);
                }

                @Override
                public void onError(String error) {

                }
            }, false,true);
        }
    };

    private void createRVList(List<BaseModel> list){
        mSearchAdapter = new Customer_SearchAdapter(list, new CallbackBaseModel() {
            @Override
            public void onResponse(BaseModel cust) {
                showMarker(cust);
                closeSearch();

            }

            @Override
            public void onError() {
                closeSearch();
            }


        });
        Util.createLinearRV(rvSearch, mSearchAdapter);
    }

    private void showMarker(BaseModel customer){
        progressLoading.setVisibility(View.GONE);
        LatLng newLatLng = new LatLng(customer.getDouble("lat") , customer.getDouble("lng"));
        triggerCurrentLocation(newLatLng ,  false);

        reUpdateMarkerDetail(customer);

    }

    private void createCustomerFast( int shoptype, String shopname, String phone, Double lat,  Double lng){
        getAddressFromLatlng(lat, lng, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result1) {

                postCustomerFast(shoptype, shopname, phone, lat, lng, result1, new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {
                        reUpdateMarkerDetail(result);

                        CustomInputDialog.dismissDialog();
                        Util.showToast("Tạo cửa hàng thành công");


                    }

                    @Override
                    public void onError(String error) {
                        Log.e("errrororr", error);
                    }
                }, true);
            }

            @Override
            public void onError(String error) {

            }

        }, false);

    }

    private void getAddressFromLatlng(Double lat, Double lng, final CallbackCustom mListener, Boolean stopLoading){
        LocationConnect.getAddressFromLocation(lat, lng, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                mListener.onResponse(MapUtil.getAddressFromMapResult(result));
            }

            @Override
            public void onError(String error) {

            }
        }, stopLoading);
    }

    private void postCustomerFast(int shoptype, String shopName, String phone , Double lat, Double lng, BaseModel objectAdress, final CallbackCustom mListener, Boolean stopLoading){
        BaseModel customer = new BaseModel();
        customer.put("id","0");
        customer.put("shopType",shoptype);
        customer.put("signBoard",shopName);
        customer.put("name","Anh " + shopName.substring(shopName.lastIndexOf(" ") + 1));
        customer.put("phone",phone);
        customer.put("note","");
        customer.put("lat",lat);
        customer.put("lng",lng);
        customer.put("volumeEstimate","10");
        customer.put("status_id",0); // new customer
        customer.put("address",objectAdress.getString("address"));
        customer.put("street",objectAdress.getString("street"));
        customer.put("district",objectAdress.getString("district"));
        customer.put("province",objectAdress.getString("province"));
        customer.put("checkinCount",0);

        CustomerConnect.CreateCustomer(CustomerConnect.createParamCustomer(customer), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                mListener.onResponse(result);
            }

            @Override
            public void onError(String error) {
                mListener.onError(error);
            }
        }, stopLoading);

    }

    private void getDistanceFromCurrent(final Double lat, final Double lng, final CallbackLong mListener) {
        getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double distance = MapUtil.distance(location.getLatitude(), location.getLongitude(), lat, lng);

                mListener.onResponse(Math.round(distance));
            }
        });

    }

    @Override
    public void onGlobalLayout() {
        coParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        ViewGroup.LayoutParams params = lnBottomSheet.getLayoutParams();
        params.height = Util.convertSdpToInt(R.dimen._120sdp);
        lnBottomSheet.requestLayout();

        setupBottomSheet();

    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottomSheet);
        mBottomSheetBehavior.setPeekHeight(5);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    btnNewCustomer.animate().scaleX(0).scaleY(0).setDuration(200).start();

                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    btnNewCustomer.animate().scaleX(1).scaleY(1).setDuration(200).start();
                }

                switch (newState){
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        setNullButton();
                        unsetCurrentMarker();
                        btnNewCustomer.setVisibility(View.VISIBLE);

                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        btnNewCustomer.setVisibility(View.GONE);

                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {


            }
        });
    }

    private void updateBottomDetail(final BaseModel customer, long distance){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        final String title = Constants.shopName[customer.getInt("shopType")] +" " + customer.getString("signBoard");
        String add = String.format("%s %s, %s",
                customer.getString("address"),
                customer.getString("street"),
                customer.getString("district"));

        tvShopname.setText(title);
        tvAddress.setText(add);
        tvDistance.setText(Util.getIconString(R.string.icon_car, "  ", distance >1000? distance/1000 +" km" :distance +" m"));
        currentPhone = customer.getString("phone");
        btnCall.setVisibility(currentPhone.equals("")?View.GONE : View.VISIBLE);

        progressLoadCustomer.setVisibility(View.VISIBLE);
        lnSheetBody.setOnClickListener(null);
        CustomerConnect.GetCustomerDetail(customer.getString("id"), new CallbackCustom() {
            @Override
            public void onResponse(final BaseModel result) {
                progressLoadCustomer.setVisibility(View.GONE);
                BaseModel cust = DataUtil.rebuiltCustomer(result, false);
                CustomSQL.setBaseModel(Constants.CUSTOMER, cust);
                BaseModel customer = buildMarkerByCustomer(cust);


                tvStatusDot.setTextColor(customer.getInt("statusColor"));
                if (customer.getBoolean("statusInterested")){
                    btnDirection.setBackground(getResources().getDrawable(R.drawable.btn_round_blue));
                    btnCall.setBackground(getResources().getDrawable(R.drawable.btn_round_white_border_blue));
                    btnShare.setBackground(getResources().getDrawable(R.drawable.btn_round_white_border_blue));

                }else {
                    btnDirection.setBackground(getResources().getDrawable(R.drawable.btn_round_grey));
                    btnCall.setBackground(getResources().getDrawable(R.drawable.btn_round_transparent_border_grey));
                    btnShare.setBackground(getResources().getDrawable(R.drawable.btn_round_transparent_border_grey));

                }

                if (customer.hasKey("last_time_order")){
                    tvStatus.setText(String.format("Khách đã mua hàng     %s ngày   %s",
                            Util.getIconString(R.string.icon_calendar,"  ", customer.getString("last_time_order")),
                            Util.getIconString(R.string.icon_username, "  ", customer.getString("user_order"))));

                }else {
                    tvStatus.setText(customer.getString("statusDetail"));
                }

                if (customer.hasKey("last_time_checkin")){
                    tvCheckin.setVisibility(View.VISIBLE);
                    tvCheckin.setText(String.format("%s ngày",
                            Util.getIconString(R.string.icon_district,"  ", customer.getString("last_time_checkin"))));


                }else {
                    tvCheckin.setVisibility(View.GONE);
                }

                tvTempBill.setVisibility (customer.hasKey(Constants.TEMPBILL) ? View.VISIBLE : View.GONE);
                updatAddListStatus(customer);
                lnSheetBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progressLoadCustomer.getVisibility() == View.GONE){
                            Transaction.gotoCustomerActivity();

                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                progressLoadCustomer.setVisibility(View.GONE);
                Util.showSnackbarError(error);
            }
        }, false, false);



        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction.openCallScreen(currentPhone);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction.shareViaZalo(customer.getString("id"));
            }
        });

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCenterDialog.alertWithCancelButton("Chỉ đường", "Mở ứng dụng bản đồ để tiếp tục chỉ đường", "Tiếp tục","Quay lại", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean re) {
                        if (re){
                            Transaction.openGoogleMapRoute( customer.getDouble("lat"), customer.getDouble("lng"));

                        }

                    }
                });

            }
        });

        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param = String.format(Api_link.CUSTOMER_TEMP_NEW_PARAM, customer.getInt("id"), User.getId());
                CustomerConnect.CreateCustomerWaitingList(param, new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {

                        if (result.getBoolean("success")){
                            BaseModel customer = CustomSQL.getBaseModel(Constants.CUSTOMER);
                            customer.put("waiting_list", result.getBoolean("data"));
                            updatAddListStatus(customer);
                            getWaitingList();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, false);


            }
        });

    }

    private void setNullButton(){
        btnCall.setOnClickListener(null);
        btnDirection.setOnClickListener(null);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        inputShopName(latLng.latitude, latLng.longitude);
    }

    private void updatAddListStatus(BaseModel customer){
        if (customer.hasKey("waiting_list")&& customer.getBoolean("waiting_list")){
            btnAddList.setBackground(getResources().getDrawable(R.drawable.btn_round_white_border_grey));
            btnAddList.setIconColor(getResources().getColor(R.color.black_text_color_hint));
            btnAddList.setIconText(getResources().getString(R.string.icon_minus));


        }else {
            btnAddList.setBackground(getResources().getDrawable(R.drawable.btn_round_white_border_blue));
            btnAddList.setIconColor(getResources().getColor(R.color.colorBlue));
            btnAddList.setIconText(getResources().getString(R.string.icon_plus));

        }
    }

    private void getWaitingList(){
        CustomerConnect.getWaitingList(new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                for (BaseModel model: results){
                    listCustomerWaiting.add(buildMarkerByCustomer(model));
                }

                if (listCustomerWaiting.size() >0){
                    tvListWating.setVisibility(View.VISIBLE);
                    tvListWating.setText(Util.getIconString(R.string.icon_thumbtack,"  ",String.valueOf(listCustomerWaiting.size())));
                }else {
                    tvListWating.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(String error) {

            }
        }, false);

    }

    private List<BaseModel> addLocationToListCustomer(List<BaseModel> list, double lat, double lng){
        for(BaseModel model: list){
            double distance = MapUtil.distance(lat, lng, model.getDouble("lat"), model.getDouble("lng"));
            model.put("distance", distance);

        }
        return list;
    }

    private void showListWaiting(){
        btnNewCustomer.close(true);
        unsetCurrentMarker();
        setNullButton();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                CustomCenterDialog.showWaitingList("DANH SÁCH KHÁCH HÀNG CHỜ CHECKIN",
                        addLocationToListCustomer(listCustomerWaiting, location.getLatitude(), location.getLongitude()),
                        new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result){
                            changeFragment(new MapWaitingListFragment(), true);
                            btnLocation.setVisibility(View.GONE);

                        }
                    }
                }, new CallbackInt() {
                    @Override
                    public void onResponse(int value) {
                        if (listCustomerWaiting.size() >0){
                            tvListWating.setVisibility(View.VISIBLE);
                            tvListWating.setText(Util.getIconString(R.string.icon_thumbtack,"  ",String.valueOf(listCustomerWaiting.size())));
                        }else {
                            tvListWating.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }



//    private void setWatingListVisibilityScreen(boolean isShow){
//        if (isShow){
//            mMap.setOnCameraMoveListener(null);
//            mMap.setOnMapClickListener(null);
//            rlSearchLayout.setVisibility(View.GONE);
//            btnNewCustomer.setVisibility(View.GONE);
//            tvListWating.setText(Util.getIcon(R.string.icon_x));
//
//        }else {
//            mMap.setOnCameraMoveListener(this);
//            mMap.setOnMapClickListener(this);
//            rlSearchLayout.setVisibility(View.VISIBLE);
//            btnNewCustomer.setVisibility(View.VISIBLE);
//            tvListWating.setText(Util.getIconString(R.string.icon_thumbtack,"  ",String.valueOf(listCustomerWaiting.size())));
//            getCurrentLocation(new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    if (location != null) {
//                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                        triggerCurrentLocation(latLng, true);
//                    }
//                }
//            });
//
//        }
//    }


}



