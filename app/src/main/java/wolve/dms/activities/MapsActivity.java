package wolve.dms.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CustomWindowAdapter;
import wolve.dms.adapter.Customer_SearchAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackLong;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.LatlngListener;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.District;
import wolve.dms.models.Status;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.apiconnect.CustomerConnect.createParamCustomer;
import static wolve.dms.utils.Constants.REQUEST_PERMISSION_LOCATION;
import static wolve.dms.utils.MapUtil.removeMarker;

/**
 * Created by macos on 9/14/17.
 */

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener, RadioGroup.OnCheckedChangeListener,
        GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnMarkerClickListener,
        ViewTreeObserver.OnGlobalLayoutListener{
    public GoogleMap mMap;
    private FloatingActionMenu btnNewCustomer;
    private android.support.design.widget.FloatingActionButton btnLocation;
    private FloatingActionButton  btnRepair, btnPhoneNumber, btnFastCustomner;
    public SupportMapFragment mapFragment;
    private RadioGroup rdFilter;
    private RadioButton rdAll, rdIntersted, rdOrdered;
    private ImageView btnBack, btnClose;
    private CTextIcon tvLocation, tvReload;
    private EditText edSearch;
    private CoordinatorLayout coParent;
    private SmoothProgressBar progressLoading;
    private RelativeLayout rlSearchLayout;
    private RecyclerView rvSearch;
//    private CardView ;
    private LinearLayout lnSheetBody, lnBottomSheet, btnCheckin, btnDirection, btnCall, btnShare;
    private TextView tvShopname, tvAddress, tvCheckin, tvDistance;

    private Handler mHandlerMoveMap = new Handler();
    private Handler mHandlerSearch = new Handler();
    private String currentPhone;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean recheckGPS = false;
    private String mSearchText = "";
    private Customer_SearchAdapter mSearchAdapter;
    private Boolean loadCustomer = false;
    private CustomWindowAdapter adapterInfoMarker;
    private Marker currentMarker;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ProgressBar progressLoadCustomer;
    //private String VIEW_CUSTOMER_ID ="";


    @Override
    public int getResourceLayout() {
        return R.layout.activity_map;
    }

    @Override
    public int setIdContainer() {
        return R.id.map_parent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FitScrollWithFullscreen.assistActivity(this, 1);

    }

    @Override
    public void findViewById() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btnLocation = findViewById(R.id.map_current_location);
        btnNewCustomer = (FloatingActionMenu) findViewById(R.id.map_new_customer);
        btnRepair = (FloatingActionButton) findViewById(R.id.map_new_repair);
//        btnWash = (FloatingActionButton) findViewById(R.id.map_new_wash);
//        btnAccesary = (FloatingActionButton) findViewById(R.id.map_new_accessary);
        btnFastCustomner = (FloatingActionButton) findViewById(R.id.map_new_fast);
//        btnMaintain = (FloatingActionButton) findViewById(R.id.map_new_maintain);
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
        btnCheckin = findViewById(R.id.map_detail_checkin);
        btnCall = findViewById(R.id.map_detail_call);
        btnDirection = findViewById(R.id.map_detail_direction);
        btnShare= findViewById(R.id.map_detail_share);
        tvCheckin = findViewById(R.id.map_detail_checkin_text);
        tvDistance = findViewById(R.id.map_detail_distance);
        progressLoadCustomer = findViewById(R.id.map_loading_customer);


    }

    @Override
    public void initialData() {
        Util.hideKeyboard(edSearch);
        Util.mapsActivity = this;
        MapUtil.customers = new ArrayList<>();
        MapUtil.markers = new ArrayList<>();
        edSearch.setFocusable(false);
        tvReload.setVisibility(loadCustomer ? View.GONE : View.VISIBLE);

    }

    @Override
    public void addEvent() {
        mapFragment.getMapAsync(this);
        btnLocation.setOnClickListener(this);
        btnRepair.setOnClickListener(this);
//        btnWash.setOnClickListener(this);
//        btnAccesary.setOnClickListener(this);
//        btnMaintain.setOnClickListener(this);
        btnFastCustomner.setOnClickListener(this);
        rdFilter.setOnCheckedChangeListener(this);
        btnPhoneNumber.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        edSearch.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        searchEvent();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
                inputShopName();

                break;

            case R.id.map_new_addphone:
                btnNewCustomer.close(true);
                inputPhoneNumber();
                break;

            case R.id.map_new_fast:
                btnNewCustomer.close(true);
                getCurrentLocation(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            createCustomerFast(location.getLatitude(), location.getLongitude());
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


        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map));
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Util.hideKeyboard(btnNewCustomer);
                btnNewCustomer.close(true);
                mMap.setOnCameraMoveListener(MapsActivity.this);
                loadCustomer = true;
                tvReload.setVisibility(loadCustomer ? View.GONE : View.VISIBLE);

                setCurrentMarkerToDefault();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                setNullButton();
            }
        });

        adapterInfoMarker = new CustomWindowAdapter(new LatlngListener() {
            @Override
            public void onLatlngChange(LatLng latlng) {
                triggerCurrentLocation(latlng, false);
            }
        });
        mMap.setInfoWindowAdapter(adapterInfoMarker);
//        LocationC

        checkGPS();

    }


    @Override
    public void onCameraIdle() {

    }

    private void showListDistrict() {
        CustomBottomDialog.choiceList("Chọn quận / huyện", District.getDistrictList(), new CustomBottomDialog.StringListener() {
            @Override
            public void onResponse(String content) {
                //mSearchView.setText(content);
                tvLocation.setTextColor(getResources().getColor(R.color.colorMain));
//                mMap.setOnCameraMoveListener(null);
                loadCustomer = false;
                loadCustomersByDistrict(content);
            }
        });
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

    private void inputShopName() {
        CustomInputDialog.inputShopName(coParent, new CustomInputDialog.ShopNameListener() {
            @Override
            public void onShopname(String shopname, String shoptype) {
                openCustomerScreen(shoptype, shopname, null);
            }

        });

    }

    private void inputPhoneNumber() {
        CustomInputDialog.inputPhoneNumber(coParent,new CallbackString() {
            @Override
            public void Result(String s) {
                openCustomerScreen(null, null, s);
            }
        });

    }

    private void openCustomerScreen(final String shopType, final String shopName, final String phone) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                createCustomerFromLocation(location.getLatitude(), location.getLongitude(), shopType, shopName, phone);

//                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    triggerCurrentLocation(latLng, 15, true);
            }
        });


    }

    private void getCurrentLocation(final LocationListener mListener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    mListener.onLocationChanged(location);

                }
            }
        });
    }

    private void createCustomerFromLocation(final double lat, final double lng , final String shopType, final String shopName, final String phone){
        LocationConnect.getAddressFromLocation(lat, lng, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                BaseModel customer = new BaseModel();
                BaseModel objectAdress = MapUtil.getAddressFromMapResult(result);
                if (phone == null && shopType != null && shopName != null) {
                    customer.put("shopType", shopType);
                    customer.put("signBoard", shopName);
                    customer.put("name", "Anh " + shopName.substring(shopName.lastIndexOf(" ") + 1));
                    customer.put("phone", "");
                } else if (shopType == null && shopName == null && phone != null) {
                    customer.put("shopType", Constants.shopType[0]);
                    customer.put("signBoard", "");
                    customer.put("name", "");
                    customer.put("phone", phone);
                }

                customer.put("id", 0);
                customer.put("lat", lat);
                customer.put("lng", lng);
                customer.put("province", objectAdress.getString("province"));
                customer.put("district", objectAdress.getString("district"));
                customer.put("street", objectAdress.getString("street"));
                customer.put("address", objectAdress.getString("address"));
                customer.put("note", "");
                customer.put("debt",0.0);

                CustomSQL.setString(Constants.CUSTOMER, customer.BaseModelstoString());
                Transaction.gotoCustomerActivity( true);
                CustomInputDialog.dismissDialog();
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void loadCustomersByDistrict(String district) {
        String param = "district=" + Util.encodeString(district);
        CustomerConnect.ListCustomer(param, 500, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                List<BaseModel> tempCustomers = new ArrayList<>();

                for (int i = 0; i < results.size(); i++) {
                    BaseModel distributor = new BaseModel(results.get(i).getJsonObject("distributor"));
                    if (distributor.getString("id").equals(Distributor.getDistributorId())) {
                        tempCustomers.add(setCustomerMarker(results.get(i)));

                    }

                }
                addMarkertoMap(true,tempCustomers, true);
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
                for (int i = 0; i < results.size(); i++) {
                    BaseModel distributor = new BaseModel(results.get(i).getJsonObject("distributor"));
                    if (distributor.getString("id").equals(Distributor.getDistributorId())) {
                        tempCustomers.add(setCustomerMarker(results.get(i)));

                    }
                }
                addMarkertoMap(clearMap,tempCustomers, false);

            }

            @Override
            public void onError(String error) {
                progressLoading.setVisibility(View.GONE);
            }

        }, true);
    }

    private BaseModel setCustomerMarker(BaseModel customer) {
        BaseModel currentCustomer = customer;
        JSONArray arrayCheckIns = currentCustomer.getJSONArray("checkIns");
        try {
            int customerStatus = new JSONObject(currentCustomer.getString("status")).getInt("id");
            currentCustomer.put("status", customerStatus);

            if (arrayCheckIns.length() ==0 && customerStatus == 1){
                currentCustomer.put("icon", R.drawable.ico_pin_pink);
                currentCustomer.put("checkincount","N");

            }else if (customerStatus == 1) {
                currentCustomer.put("icon", R.drawable.ico_pin_red);
                currentCustomer.put("checkincount", arrayCheckIns.length());

            } else if (customerStatus == 2) {
                currentCustomer.put("icon", R.drawable.ico_pin_grey);
                currentCustomer.put("checkincount", arrayCheckIns.length());

            } else {
                currentCustomer.put("icon", R.drawable.ico_pin_blue);
                currentCustomer.put("checkincount", arrayCheckIns.length());

            }


        } catch (JSONException e) {

        }

        return currentCustomer;
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        //mMap.setOnCameraMoveListener(null);
//        final Customer customer = new Customer((JSONObject) marker.getTag());
//
//        getDistanceFromCurrent(customer.getDouble("lat"), customer.getDouble("lng"), new CallbackLong() {
//            @Override
//            public void onResponse(final Long value) {
//                String param = customer.getString("id");
//
//                CustomerConnect.GetCustomerDetail(param, new CallbackCustom() {
//                    @Override
//                    public void onResponse(final BaseModel result) {
//                        if (value < Constants.CHECKIN_DISTANCE){
//                            CustomCenterDialog.alertWithButton("Check In", "Vị trí hiện tại của bạn rất gần cửa hàng. Bắt đầu check in", "Tiếp tục", new CallbackBoolean() {
//                                @Override
//                                public void onRespone(Boolean re) {
//                                    Transaction.gotoCustomerActivity(result.toString(), true);
//                                }
//                            });
//                        }else {
//                            Transaction.gotoCustomerActivity(result.toString(), false);
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, true);
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mMap != null){
            Util.getInstance().setCurrentActivity(this);
            mMap.setOnCameraMoveListener(MapsActivity.this);
            if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY) {
                final String responseString = data.getStringExtra(Constants.CUSTOMER);

                if (responseString.startsWith("delete")) {
                    removeMarker(responseString.split("-")[1]);
                    currentMarker =null;
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                setNullButton();

                } else {
                    try {
                        Customer customer = new Customer(new JSONObject(responseString));
                        String param = customer.getString("id");

                        reUpdateMarker(param);


                    } catch (JSONException e) {
                        //e.printStackTrace();
                        onResume();
                    }
                }

            }else {
                onResume();
            }

        }else {
            onResume();
        }

    }

    private void reUpdateMarker(String id){
        CustomerConnect.GetCustomerDetail(id, new CallbackCustom() {
            @Override
            public void onResponse(final BaseModel result) {
                setCurrentMarkerToDefault();
                currentMarker = MapUtil.getCurrentMarker(mMap, setCustomerMarker(result));

                if (currentMarker.getTag() != null) {
                    final BaseModel customer = new BaseModel((JSONObject) currentMarker.getTag());

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
            public void onError(String error) {

            }
        }, true, true);


    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        createCustomerFast(latLng.latitude, latLng.longitude);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.map_filter_all:
                MapUtil.updateCustomerFilter(Constants.MARKER_ALL);

                break;

            case R.id.map_filter_interested:
                MapUtil.updateCustomerFilter(Constants.MARKER_INTERESTED);

                break;

            case R.id.map_filter_ordered:
                MapUtil.updateCustomerFilter(Constants.MARKER_ORDERED);

                break;
        }
    }

    private void addMarkertoMap(Boolean clearMap, List<BaseModel> list, Boolean isBound) {
        MapUtil.addListMarkertoMap(clearMap, mMap,list, getCheckedFilter(), isBound);
        rdAll.setText(MapUtil.countAll());
        rdIntersted.setText(MapUtil.countInterested());
        rdOrdered.setText(MapUtil.countOrdered());

//        tvCount.setText(MapUtil.addListMarkerToMap(clearMap, mMap, list, filter, isBound));

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        final Customer customer = new Customer((JSONObject) marker.getTag());
        currentPhone = customer.getString("phone");

        if (!currentPhone.equals("")) {
            CustomCenterDialog.alertWithCancelButton(null, "Gọi điện thoại cho " + customer.getString("name") + " (" + customer.getString("phone") + ")", "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    openCallScreen(currentPhone);

                }
            });
        } else {
            Util.showSnackbar("Khách hàng chưa có số điện thoại", null, null);
        }


    }

    @Override
    public void onCameraMove() {
        //Util.hideKeyboard(mSearchView);
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
        String s = Constants.MARKER_ALL;
        if (rdFilter.getCheckedRadioButtonId() == R.id.map_filter_all) {
            s = Constants.MARKER_ALL;

        } else if (rdFilter.getCheckedRadioButtonId() == R.id.map_filter_interested) {
            s = Constants.MARKER_INTERESTED;

        } else {
            s = Constants.MARKER_ORDERED;

        }
        return s;
    }

    private void setCurrentMarkerToDefault(){
        if (currentMarker != null){
            final BaseModel cust = new BaseModel((JSONObject) currentMarker.getTag());
            Bitmap bitmap = MapUtil.GetBitmapMarker(this, cust.getInt("icon"), cust.getString("checkincount"), R.color.pin_waiting);
            currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        setCurrentMarkerToDefault();
        currentMarker = marker;

        if (marker.getTag() != null) {
            final BaseModel customer = new BaseModel((JSONObject) marker.getTag());
//            LatLng newLatLng = new LatLng(customer.getDouble("lat") , customer.getDouble("lng"));
//            triggerCurrentLocation(newLatLng ,  false);

            getDistanceFromCurrent(customer.getDouble("lat"), customer.getDouble("lng"), new CallbackLong() {
                @Override
                public void onResponse(Long value) {
                    Bitmap bitmap = MapUtil.GetBitmapMarker(MapsActivity.this, customer.getInt("icon"), customer.getString("checkincount"), R.color.pin_waiting);
                    currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*2,bitmap.getHeight()*2, false)));


                    updateBottomDetail(customer, value);
                }
            });

        }

        return true;
    }


    private void openCallScreen(String phone) {
        if (PermissionChecker.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PHONE_PERMISSION);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(phone)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);

    }

    public void checkGPS() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Util.getInstance().getCurrentActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        //Todo check GPS is enable?
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (!CustomSQL.getString(Constants.CUSTOMER_ID).equals("")){
                                CustomerConnect.GetCustomerDetail(CustomSQL.getString(Constants.CUSTOMER_ID), new CallbackCustom() {
                                    @Override
                                    public void onResponse(BaseModel result) {
                                        showMarker(result);
                                        CustomSQL.setString(Constants.CUSTOMER_ID, "");
                                    }

                                    @Override
                                    public void onError(String error) {
                                        CustomSQL.setString(Constants.CUSTOMER_ID, "");
                                    }
                                }, true, true);

                            }else if (location != null) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                triggerCurrentLocation(latLng,  true);
                            }
                        }
                    });

            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGPS();

            } else {
                Toast.makeText(this, "Cấp quyền truy cập không thành công!", Toast.LENGTH_LONG).show();
                Transaction.gotoHomeActivityRight(true);
            }
        } else if (requestCode == Constants.REQUEST_PHONE_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Util.showSnackbar("Không thể gọi do chưa được cấp quyền", null, null);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + Uri.encode(currentPhone)));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        }

    }


    private void backPress(){
        if (rlSearchLayout.getVisibility() == View.VISIBLE){
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

        setCurrentMarkerToDefault();
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

        Marker mMarker = MapUtil.addMarkerToMap(mMap, setCustomerMarker(customer), Constants.MARKER_ALL);
        currentMarker = mMarker;
        if (mMarker.getTag() != null) {
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

    private void createCustomerFast(final Double lat, final Double lng){
        //Get shopName
        CustomInputDialog.inputShopName(coParent, new CustomInputDialog.ShopNameListener() {
            @Override
            public void onShopname(final String shopname, String shoptype) {
                getAddressFromLatlng(lat, lng, new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result1) {
//createCustomer to Server
                        postCustomerFast(shopname, lat, lng, result1, new CallbackCustom() {
                            @Override
                            public void onResponse(BaseModel result) {
                                reUpdateMarker(result.getString("id"));

                                CustomInputDialog.dismissDialog();
                                Util.showToast("Tạo cửa hàng thành công");
                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, true);
                    }

                    @Override
                    public void onError(String error) {

                    }

                }, false);
            }
      });
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

    private void postCustomerFast(String shopName , Double lat, Double lng, BaseModel objectAdress, final CallbackCustom mListener, Boolean stopLoading){
        BaseModel customer = new BaseModel();
        customer.put("id","0");
        customer.put("name","Anh " + shopName.substring(shopName.lastIndexOf(" ") + 1));
        customer.put("signBoard",shopName);
        customer.put("phone","");
        customer.put("note","");
        customer.put("lat",lat);
        customer.put("lng",lng);
        customer.put("volumeEstimate","10");
        customer.put("shopType",Constants.shopType[0]);
        customer.put("status.id",Status.getStatusList().get(0).getInt("id"));
        customer.put("address",objectAdress.getString("address"));
        customer.put("street",objectAdress.getString("street"));
        customer.put("district",objectAdress.getString("district"));
        customer.put("province",objectAdress.getString("province"));
        customer.put("debt",0.0);

        CustomerConnect.CreateCustomer(createParamCustomer(customer), new CallbackCustom() {
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
                Double distance = MapUtil.distance(location.getLatitude(), location.getLongitude(), lat, lng);

                mListener.onResponse(Math.round(distance));
            }
        });

    }

    @Override
    public void onGlobalLayout() {
        coParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int height = getResources().getDimensionPixelSize(R.dimen._108sdp);
        ViewGroup.LayoutParams params = lnBottomSheet.getLayoutParams();
        params.height = height;
        lnBottomSheet.requestLayout();

        setupBottomSheet();

    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottomSheet);
//        mBottomSheetBehavior.setPeekHeight(Util.convertDp2PxInt(60-8));
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
                        setCurrentMarkerToDefault();
                        btnNewCustomer.setVisibility(View.VISIBLE);

                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        btnNewCustomer.setVisibility(View.GONE);

                        break;

                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                tvBottomUp.setAlpha(1-slideOffset);
            }
        });
    }

    private void updateBottomDetail(final BaseModel customer, final long distance){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        final String title = Constants.getShopTitle(customer.getString("shopType"), null) +" " + customer.getString("signBoard");
        String add = String.format("%s %s, %s -",
                customer.getString("address"),
                customer.getString("street"),
                customer.getString("district"));
        currentPhone = customer.getString("phone");

        tvShopname.setText(title);
        tvAddress.setText(add);
        tvDistance.setText(distance >1000? String.valueOf(distance/1000) +" km" :distance +" m");

        if (distance < Constants.CHECKIN_DISTANCE){
            tvCheckin.setText("Checkin" );
            btnCheckin.setBackground(getResources().getDrawable(R.drawable.btn_round_blue));

        }else {
            tvCheckin.setText("Xem chi tiết");
            btnCheckin.setBackground(getResources().getDrawable(R.drawable.btn_round_grey));
        }


        btnCall.setVisibility(currentPhone.equals("")?View.GONE : View.VISIBLE);

        progressLoadCustomer.setVisibility(View.VISIBLE);
        CustomerConnect.GetCustomerDetail(customer.getString("id"), new CallbackCustom() {
            @Override
            public void onResponse(final BaseModel result) {
                progressLoadCustomer.setVisibility(View.GONE);
                CustomSQL.setString(Constants.CUSTOMER, result.BaseModelstoString());
                //updateView();

            }

            @Override
            public void onError(String error) {
                progressLoadCustomer.setVisibility(View.GONE);
                Util.showSnackbarError(error);
            }
        }, false, false);

        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = customer.getString("id");

//                CustomerConnect.GetCustomerDetail(param, new CallbackCustom() {
//                    @Override
//                    public void onResponse(final BaseModel result) {
//                        //Transaction.gotoCustomerActivity(result.BaseModelstoString(), distance < Constants.CHECKIN_DISTANCE? true : false);
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, true);
            }
        });

        lnSheetBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressLoadCustomer.getVisibility() == View.GONE){
                    Transaction.gotoCustomerActivity( false);
                }
//                CustomerConnect.GetCustomerDetail(customer.getString("id"), new CallbackCustom() {
//                    @Override
//                    public void onResponse(final BaseModel result) {
//
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, true);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCallScreen(currentPhone);
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
                            Transaction.openGoogleMap(title, customer.getDouble("lat"), customer.getDouble("lng"));

                        }

                    }
                });

            }
        });

    }

    private void setNullButton(){
        btnCheckin.setOnClickListener(null);
        btnCall.setOnClickListener(null);
        btnDirection.setOnClickListener(null);
    }





}

