package wolve.dms.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import wolve.dms.adapter.CustomerSearchAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackLong;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.LatlngListener;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.District;
import wolve.dms.models.Status;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
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
        GoogleMap.OnMarkerClickListener {
    public GoogleMap mMap;
    private FloatingActionMenu btnNewCustomer;
    private FloatingActionButton btnLocation, btnRepair, btnWash, btnAccesary, btnMaintain, btnPhoneNumber, btnFastCustomner;
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

    private Handler mHandlerMoveMap = new Handler();
    private Handler mHandlerSearch = new Handler();
    private String currentPhone;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean recheckGPS = false;
    private String mSearchText = "";
    private CustomerSearchAdapter mSearchAdapter;
    private Boolean loadCustomer = false;
    private CustomWindowAdapter adapterInfoMarker;
    private Marker currentMarker;


    @Override
    public int getResourceLayout() {
        return R.layout.activity_map;
    }

    @Override
    public int setIdContainer() {
        return R.id.map_parent;
    }

    @Override
    public void findViewById() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btnLocation = (FloatingActionButton) findViewById(R.id.map_current_location);
        btnNewCustomer = (FloatingActionMenu) findViewById(R.id.map_new_customer);
        btnRepair = (FloatingActionButton) findViewById(R.id.map_new_repair);
        btnWash = (FloatingActionButton) findViewById(R.id.map_new_wash);
        btnAccesary = (FloatingActionButton) findViewById(R.id.map_new_accessary);
        btnFastCustomner = (FloatingActionButton) findViewById(R.id.map_new_fast);
        btnMaintain = (FloatingActionButton) findViewById(R.id.map_new_maintain);
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
        btnLocation.setColorNormalResId(R.color.white_text_color);
        btnLocation.setColorPressedResId(R.color.colorGrey);
        btnNewCustomer.setMenuButtonColorNormalResId(R.color.colorBlue);
        btnNewCustomer.setMenuButtonColorPressedResId(R.color.colorBlueDark);
        rvSearch = findViewById(R.id.map_rvsearch);

    }

    @Override
    public void initialData() {
        Util.hideKeyboard(edSearch);
        Util.mapsActivity = this;
        MapUtil.customers = new ArrayList<>();
        MapUtil.markers = new ArrayList<>();
        edSearch.setFocusable(false);
        tvReload.setVisibility(loadCustomer?View.GONE:View.VISIBLE);


    }

    @Override
    public void addEvent() {
        mapFragment.getMapAsync(this);
        btnLocation.setOnClickListener(this);
        btnRepair.setOnClickListener(this);
        btnWash.setOnClickListener(this);
        btnAccesary.setOnClickListener(this);
        btnMaintain.setOnClickListener(this);
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



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recheckGPS)
            checkGPS();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_current_location:
//                mMap.setOnCameraMoveListener(MapsActivity.this);
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
                inputShopName(Constants.shopType[0]);

                break;

            case R.id.map_new_wash:
                btnNewCustomer.close(true);
                inputShopName(Constants.shopType[1]);
                break;

            case R.id.map_new_accessary:
                btnNewCustomer.close(true);
                inputShopName(Constants.shopType[2]);
                break;

            case R.id.map_new_maintain:
                btnNewCustomer.close(true);
                inputShopName(Constants.shopType[3]);

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
                showListDistrict();
                break;

            case R.id.maintext:
                //changeFragment(new SearchFragment(), true);
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
                tvReload.setVisibility(loadCustomer?View.GONE:View.VISIBLE);
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

            }
        }

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
                loadCustomersByDistrict(content, getCheckedFilter());
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

    private void inputShopName(final String shop_type) {
        CustomInputDialog.inputShopName(null, shop_type, new CallbackString() {
            @Override
            public void Result(String s) {
                openCustomerScreen(shop_type, s, null);
            }
        });

    }

    private void inputPhoneNumber() {
        CustomInputDialog.inputPhoneNumber(new CallbackString() {
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
        LocationConnect.getAddressFromLocation(lat, lng, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Customer customer = new Customer(new JSONObject());
                JSONObject objectAdress = MapUtil.getAddressFromMapResult(result);
                try {
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Transaction.gotoCustomerActivity(customer.CustomertoString(), true);
                CustomInputDialog.dismissDialog();
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void loadCustomersByDistrict(String district, final String isAll) {
        String param = "district=" + Util.encodeString(district);
        CustomerConnect.ListCustomer(param, new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                try {
                    List<Customer> tempCustomers = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        if (object.getJSONObject("distributor").getString("id").equals(Distributor.getDistributorId())) {
                            tempCustomers.add(setCustomerMarker(new Customer(object)));

                        }

                    }
                    addMarkertoMap(true,tempCustomers, true);
//                    MapUtil.addListMarkertoMap(true, mMap,tempCustomers, getCheckedFilter(), true);
//                    createRVCustomer(listCustomer);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
            }
        }, true);
    }

    private void loadCustomersByLocation(final Boolean clearMap, Double lat, Double lng) {
        progressLoading.setVisibility(View.VISIBLE);

        CustomerConnect.ListCustomerLocation(Util.encodeString(String.valueOf(lat)), Util.encodeString(String.valueOf(lng)), new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                progressLoading.setVisibility(View.GONE);
                try {
                    List<Customer> tempCustomers = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        if (object.getJSONObject("distributor").getString("id").equals(Distributor.getDistributorId())) {
                            tempCustomers.add(setCustomerMarker(new Customer(object)));

                        }
                    }
                    addMarkertoMap(clearMap,tempCustomers, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                progressLoading.setVisibility(View.GONE);
            }

        }, true);
    }

    private Customer setCustomerMarker(Customer customer) {
        Customer currentCustomer = customer;
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
        final Customer customer = new Customer((JSONObject) marker.getTag());

        getDistanceFromCurrent(customer.getDouble("lat"), customer.getDouble("lng"), new CallbackLong() {
            @Override
            public void onResponse(final Long value) {
                String param = customer.getString("id");

                CustomerConnect.GetCustomerDetail(param, new CallbackJSONObject() {
                    @Override
                    public void onResponse(final JSONObject result) {
                        if (value < Constants.CHECKIN_DISTANCE){
                            CustomCenterDialog.alertWithButton("Check In", "Vị trí hiện tại của bạn rất gần cửa hàng. Bắt đầu check in", "Tiếp tục", new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean re) {
                                    Transaction.gotoCustomerActivity(result.toString(), true);
                                }
                            });
                        }else {
                            Transaction.gotoCustomerActivity(result.toString(), false);
                        }


                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        mMap.setOnCameraMoveListener(MapsActivity.this);
        if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY) {
            final String responseString = data.getStringExtra(Constants.CUSTOMER);

            if (responseString.startsWith("delete")) {
                removeMarker(responseString.split("-")[1]);
            } else {
                try {
                    Customer customer = new Customer(new JSONObject(responseString));
                    String param = customer.getString("id");

                    CustomerConnect.GetCustomerDetail(param, new CallbackJSONObject() {
                        @Override
                        public void onResponse(final JSONObject result) {
                            MapUtil.showUpdatedMarker(mMap, setCustomerMarker(new Customer(result)));

                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }else {
            onResume();
        }
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

    private void addMarkertoMap(Boolean clearMap, List<Customer> list, Boolean isBound) {
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

    @Override
    public boolean onMarkerClick(final Marker marker) {
        currentMarker = marker;
        if (marker.getTag() != null) {
            final Customer customer = new Customer((JSONObject) marker.getTag());
            getDistanceFromCurrent(customer.getDouble("lat"), customer.getDouble("lng"), new CallbackLong() {
                @Override
                public void onResponse(Long value) {
                    TextView textView = adapterInfoMarker.getCurrentView().findViewById(R.id.map_infowindow_distance);
                    String distance = value >1000? String.valueOf(value/1000) +" km" :value +" m";
                    textView.setText(String.format("Khoảng cách: %s", distance));

                    marker.showInfoWindow();
                }
            });


        }

        return true;
    }


    private void openCallScreen(String phone) {
        if (PermissionChecker.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            mapFragment.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PHONE_PERMISSION);
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
                            if (location != null) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                triggerCurrentLocation(latLng,  true);
                            }
                        }
                    });

            }
    }

    private void backPress(){
//        if (dialog != null && dialog.isShowing()){
//            dialog.dismiss();
//
//        }else
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
        edSearch.setSelection(0);
        Util.showKeyboard(edSearch);
        tvLocation.setVisibility(View.GONE);
        tvReload.setVisibility(View.GONE);
    }

    private void closeSearch(){
        edSearch.setText("");
        rlSearchLayout.setVisibility(View.GONE);
        Util.hideKeyboard(edSearch);
        edSearch.setFocusable(false);
        tvLocation.setVisibility(View.VISIBLE);
        tvReload.setVisibility(loadCustomer?View.VISIBLE:View.GONE);
    }

    private void createRVList(List<JSONObject> list){
        mSearchAdapter = new CustomerSearchAdapter(list, new CustomerSearchAdapter.CallbackObject() {
            @Override
            public void onResponse(JSONObject customer) {
                    Customer cust = new Customer(customer);
                    LatLng newLatLng = new LatLng(cust.getDouble("lat") , cust.getDouble("lng"));
                    triggerCurrentLocation(newLatLng ,  false);

                    Marker mMarker = MapUtil.addMarkerToMap(mMap, setCustomerMarker(cust), Constants.MARKER_ALL);
                    if (mMarker.getTag() != null) {
                        mMarker.showInfoWindow();
                    }

                    closeSearch();


            }
        });
        Util.createLinearRV(rvSearch, mSearchAdapter);
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

            CustomerConnect.ListCustomerSearch(param, new CallbackJSONArray() {
                @Override
                public void onResponse(JSONArray result) {
                    List<JSONObject> listResult = new ArrayList<>();
                    if (result.length() >0){
                        for (int i=0; i<result.length(); i++){
                            try {
                                listResult.add(result.getJSONObject(i));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    createRVList(listResult);

                }

                @Override
                public void onError(String error) {

                }
            }, true);
        }
    };

    private void createCustomerFast(final Double lat, final Double lng){
        //Get shopName
        CustomInputDialog.inputShopName("Tạo nhanh cửa hàng", Constants.shopType[0], new CallbackString() {
            @Override
            public void Result(final String s) {
                //getAddress
                getAddressFromLatlng(lat, lng, new CallbackObject() {
                    @Override
                    public void onResponse(JSONObject result) {
                        //createCustomer to Server
                        postCustomerFast(s, lat, lng, result, new CallbackJSONObject() {
                            @Override
                            public void onResponse(JSONObject result1) {
                                Customer customer = new Customer(result1);
                                MapUtil.showUpdatedMarker(mMap, setCustomerMarker(customer));

                                CustomInputDialog.dismissDialog();
                                Util.showToast("Tạo cửa hàng thành công");

                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, true);


                    }

                }, false);



            }
        });
    }

    private void getAddressFromLatlng(Double lat, Double lng, final CallbackObject mListener, Boolean stopLoading){
        LocationConnect.getAddressFromLocation(lat, lng, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                mListener.onResponse(MapUtil.getAddressFromMapResult(result));

            }

            @Override
            public void onError(String error) {

            }
        }, stopLoading);
    }

    private void postCustomerFast(String shopName , Double lat, Double lng, JSONObject objectAdress, final CallbackJSONObject mListener, Boolean stopLoading){
        Customer customer = new Customer();
        try {
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

            CustomerConnect.CreateCustomer(createParamCustomer(customer), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    mListener.onResponse(result);

                }

                @Override
                public void onError(String error) {
                    mListener.onError(error);
                }
            }, stopLoading);



        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}

