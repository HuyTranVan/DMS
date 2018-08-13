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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CustomWindowAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.District;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

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
    private FloatingActionButton btnLocation, btnRepair, btnWash, btnAccesary, btnMaintain, btnPhoneNumber;
    public SupportMapFragment mapFragment;
    private RadioGroup rdFilter;
    private RadioButton rdAll, rdIntersted, rdOrdered;

    private CoordinatorLayout coParent;
    private SmoothProgressBar progressLoading;
    private MaterialSearchView mSearchView;

    private Handler mHandlerMoveMap = new Handler();
    private Handler mHandlerSearch = new Handler();
    private String currentPhone;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean recheckGPS = false;
    private String mSearchText = "";

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
        btnMaintain = (FloatingActionButton) findViewById(R.id.map_new_maintain);
        btnPhoneNumber = (FloatingActionButton) findViewById(R.id.map_new_addphone);
        rdFilter = (RadioGroup) findViewById(R.id.map_filter);
        rdAll = findViewById(R.id.map_filter_all);
        rdIntersted = findViewById(R.id.map_filter_interested);
        rdOrdered = findViewById(R.id.map_filter_ordered);
        coParent = (CoordinatorLayout) findViewById(R.id.map_parent);
        progressLoading = findViewById(R.id.map_loading);
        mSearchView = findViewById(R.id.map_searchview);

        btnLocation.setColorNormalResId(R.color.white_text_color);
        btnLocation.setColorPressedResId(R.color.colorGrey);
        btnNewCustomer.setMenuButtonColorNormalResId(R.color.colorBlue);
        btnNewCustomer.setMenuButtonColorPressedResId(R.color.colorBlueDark);

    }

    @Override
    public void initialData() {
        Util.mapsActivity = this;
        MapUtil.customers = new ArrayList<>();
        MapUtil.markers = new ArrayList<>();

    }

    @Override
    public void addEvent() {
        mapFragment.getMapAsync(this);
        btnLocation.setOnClickListener(this);
        btnRepair.setOnClickListener(this);
        btnWash.setOnClickListener(this);
        btnAccesary.setOnClickListener(this);
        btnMaintain.setOnClickListener(this);
        rdFilter.setOnCheckedChangeListener(this);
        btnPhoneNumber.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        onSearchViewEvent();
    }

    private void onSearchViewEvent() {
        mSearchView.onClickEvent(new MaterialSearchView.CallbackClick() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onClear() {

            }

            @Override
            public void onLocation() {
                showListDistrict();
            }
        });

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty() && newText.length()>1){
                    mSearchText = newText;
                    mHandlerSearch.removeCallbacks(delayForSerch);
                    mHandlerSearch.postDelayed(delayForSerch, 500);

                }
                return true;
            }
        });
    }

    private Runnable delayForSerch = new Runnable() {
        @Override
        public void run() {
            String param = "name=" + Util.encodeString(mSearchText);
            CustomerConnect.ListCustomerSearch(param, new CallbackJSONArray() {
                @Override
                public void onResponse(JSONArray result) {
                    mSearchView.setSuggestions(result);
                }

                @Override
                public void onError(String error) {

                }
            }, true);
        }
    };

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
                mMap.setOnCameraMoveListener(MapsActivity.this);
//                MapUtil.resetMarker();
                LatLng latLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                triggerCurrentLocation(latLng, 16);

                break;

            case R.id.map_new_repair:
                inputShopName(Constants.shopType[0]);

                break;

            case R.id.map_new_wash:
                inputShopName(Constants.shopType[1]);
                break;

            case R.id.map_new_accessary:
                inputShopName(Constants.shopType[2]);
                break;

            case R.id.map_new_maintain:
                inputShopName(Constants.shopType[3]);

                break;

            case R.id.map_new_addphone:
                inputPhoneNumber();
                break;

            case R.id.map_new_customer:

                break;


        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map));
        mMap.setInfoWindowAdapter(new CustomWindowAdapter());
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
            }
        });

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

    private void showListDistrict(){
        CustomBottomDialog.choiceList("Chọn quận / huyện", District.getDistrictList(), new CustomBottomDialog.StringListener() {
            @Override
            public void onResponse(String content) {
                mSearchView.setText(content);
                mMap.setOnCameraMoveListener(null);
                loadCustomersByDistrict(content, getCheckedFilter());
            }
        });
    }

    public void triggerCurrentLocation(LatLng latLng, int zomm) {
        if (latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zomm), 800, null);

        }

    }

    private void inputShopName(final String shop_type) {
        btnNewCustomer.close(true);
        dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.view_input_shopname))
                .setGravity(Gravity.CENTER)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10, 10, 10, 10)
                .setPadding(20, 30, 20, 20)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();
//        LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.input_shopnname_parent);
        TextView tvShopType = (TextView) dialog.findViewById(R.id.input_shopnname_type);
        final EditText edName = (EditText) dialog.findViewById(R.id.input_shopnname_name);
        ImageView btnSubmit = (ImageView) dialog.findViewById(R.id.input_shopnname_submit);

        tvShopType.setText(Constants.getShopInfo(shop_type, null));

        edName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    openCustomerScreen(shop_type, edName.getText().toString().trim(), null);
                    handled = true;
                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                if (!edName.getText().toString().trim().equals("")) {
                    openCustomerScreen(shop_type, edName.getText().toString().trim(), null);
                }
            }
        });

        dialog.show();
        edName.requestFocus();
        Util.showKeyboard(edName);
    }

    private void inputPhoneNumber() {
        btnNewCustomer.close(true);
        dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.view_input_phonenumber))
                .setGravity(Gravity.CENTER)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10, 10, 10, 10)
                .setPadding(20, 30, 20, 20)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialog) {
                        Util.hideKeyboard(btnNewCustomer);
                    }
                })
                .create();
        LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.input_phonenumber_parent);
        final EditText edPhone = (EditText) dialog.findViewById(R.id.input_phonenumber_phone);
        ImageView btnSubmit = (ImageView) dialog.findViewById(R.id.input_phonenumber_submit);

        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    openCustomerScreen(null, null, edPhone.getText().toString().trim());
                    handled = true;
                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                if (!edPhone.getText().toString().trim().equals("")) {
                    openCustomerScreen(null, null, edPhone.getText().toString().trim());
                }
            }
        });

        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);
    }

    private void openCustomerScreen(final String shopType, final String shopName, final String phone) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    LocationConnect.getAddressFromLocation(location.getLatitude(), location.getLongitude(), new CallbackJSONObject() {
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
                                customer.put("lat", location.getLatitude());
                                customer.put("lng", location.getLongitude());
                                customer.put("province", objectAdress.getString("province"));
                                customer.put("district", objectAdress.getString("district"));
                                customer.put("street", objectAdress.getString("street"));
                                customer.put("address", objectAdress.getString("address"));
                                customer.put("note", "");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Transaction.gotoCustomerActivity(customer.CustomertoString());
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);


                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    triggerCurrentLocation(latLng, 15);
                }
            }
        });

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
                        if (object.getJSONObject("distributor").getString("id").equals(Distributor.getDistributorId()) || Distributor.getLocation().contains(object.getString("district"))) {
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
                        if (object.getJSONObject("distributor").getString("id").equals(Distributor.getDistributorId()) || Distributor.getLocation().contains(object.getString("district"))) {
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
        try {
            int customerStatus = new JSONObject(currentCustomer.getString("status")).getInt("id");
//            currentCustomer.put("checkincount", new JSONArray(currentCustomer.getString("checkIns")).length());
            currentCustomer.put("checkincount", currentCustomer.getInt("checkinCount"));
            currentCustomer.put("status", customerStatus);

            if (customerStatus == 1) {
                currentCustomer.put("icon", R.drawable.ico_pin_red);
            } else if (customerStatus == 2) {
                currentCustomer.put("icon", R.drawable.ico_pin_grey);
            } else {
                currentCustomer.put("icon", R.drawable.ico_pin_blue);

            }


        } catch (JSONException e) {

        }

        return currentCustomer;
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        mMap.setOnCameraMoveListener(null);
        final Customer customer = new Customer((JSONObject) marker.getTag());
        String param = customer.getString("id");
        CustomerConnect.GetCustomerDetail(param, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Transaction.gotoCustomerActivity(result.toString());

            }

            @Override
            public void onError(String error) {

            }
        }, true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mMap.setOnCameraMoveListener(MapsActivity.this);
        if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY) {
            String responseString = data.getStringExtra(Constants.CUSTOMER);

            if (responseString.startsWith("delete")) {
                removeMarker(responseString.split("-")[1]);
            } else {
                try {
                    Customer customer = new Customer(new JSONObject(responseString));
                    MapUtil.showUpdatedMarker(mMap, setCustomerMarker(customer));

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
        LocationConnect.getAddressFromLocation(latLng.latitude, latLng.longitude, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Customer customer = new Customer(new JSONObject());
                try {
                    customer.put("id", 0);
                    customer.put("shopType", Constants.shopType[0]);
                    customer.put("signBoard", "");
                    customer.put("name", "");
                    customer.put("lat", latLng.latitude);
                    customer.put("lng", latLng.longitude);

                    JSONObject objectAdress = MapUtil.getAddressFromMapResult(result);
                    customer.put("province", objectAdress.getString("province"));
                    customer.put("district", objectAdress.getString("district"));
                    customer.put("street", objectAdress.getString("street"));
                    customer.put("address", objectAdress.getString("address"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Transaction.gotoCustomerActivity(customer.CustomertoString());
                //currentLatlng = latLng;

            }

            @Override
            public void onError(String error) {

            }
        }, true);
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
        Util.hideKeyboard(mSearchView);
        mHandlerMoveMap.removeCallbacks(mFilterTask);
        mHandlerMoveMap.postDelayed(mFilterTask, 1000);

    }

    private Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            LatLng curLocation = mMap.getCameraPosition().target;
            loadCustomersByLocation(false, curLocation.latitude, curLocation.longitude);
        }
    };

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
    public boolean onMarkerClick(Marker marker) {
//        mMap.setOnCameraMoveListener(null);
        if (marker.getTag() != null) {
            marker.showInfoWindow();
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
                                triggerCurrentLocation(latLng, 15);
                            }
                        }
                    });

            }
        }

}

