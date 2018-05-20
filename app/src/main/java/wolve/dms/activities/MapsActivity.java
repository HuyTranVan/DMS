package wolve.dms.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CustomWindowAdapter;
import wolve.dms.adapter.MapListCustomerAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.District;
import wolve.dms.utils.Constants;
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
        GoogleMap.OnMarkerClickListener, ViewTreeObserver.OnGlobalLayoutListener {
    public GoogleMap mMap;
//    public Marker currentMarker;
    private FloatingActionMenu btnNewCustomer;
    private FloatingActionButton btnLocation, btnRepair, btnWash, btnAccesary, btnMaintain, btnPhoneNumber;
    public SupportMapFragment mapFragment;
    private Spinner mSpinner;
    private RadioGroup rdFilter;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout lnBottomSheet;
    private CoordinatorLayout coParent;
    private RecyclerView rvCustomer;
    private TextView tvCount;
    private ImageView btnBack;
    private ProgressBar progressLoading;
//    private CTextIcon btnLocation;

    private List<Customer> listCustomer = new ArrayList<>();
    private MapListCustomerAdapter adapter;
    private float bottomSheetHeight;
    private Handler mHandler = new Handler();
    private String currentPhone;

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
        mSpinner = (Spinner) findViewById(R.id.map_spinner);
        rdFilter = (RadioGroup) findViewById(R.id.map_filter);
        coParent = (CoordinatorLayout) findViewById(R.id.map_parent);
        lnBottomSheet = (LinearLayout) findViewById(R.id.map_bottom_sheet);
        rvCustomer = (RecyclerView) findViewById(R.id.map_rvcustomer);
        tvCount = (TextView) findViewById(R.id.map_countcustomer);
        progressLoading = findViewById(R.id.map_loading);
        btnBack = findViewById(R.id.map_back);

        btnLocation.setColorNormalResId(R.color.white_text_color_hint);
        btnLocation.setColorPressedResId(R.color.colorGrey);
        btnNewCustomer.setMenuButtonColorNormalResId(R.color.colorBlue);
        btnNewCustomer.setMenuButtonColorPressedResId(R.color.colorBlueDark);

    }

    @Override
    public void initialData() {
        Util.mapsActivity = this;
        setupSpinner();

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
        coParent.getViewTreeObserver().addOnGlobalLayoutListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_current_location:
                mMap.setOnCameraMoveListener(MapsActivity.this);
                //MapUtil.resetMarker();
                triggerCurrentLocation(new LatLng(getCurLocation().getLatitude(), getCurLocation().getLongitude()), 16);
                mSpinner.setSelection(0);

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

            case R.id.map_back:
                onBackPressed();
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
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Util.getInstance().getCurrentActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        checkGPS();


    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                checkGPS();

            }else {
                Toast.makeText(this, "Cấp quyền truy cập không thành công!", Toast.LENGTH_LONG).show();
                Transaction.gotoHomeActivityRight(true);
            }
        }else  if (requestCode == Constants.REQUEST_PHONE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Util.showSnackbar("Không thể gọi do chưa được cấp quyền", null, null);

            }else {

            }
        }

    }

    @Override
    public void onCameraIdle() {

    }

    private void setupBottomSheet() {
        bottomSheetHeight = Util.convertDp2Px(57);
        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) bottomSheetHeight);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            int lastState = BottomSheetBehavior.STATE_COLLAPSED;
//
//            @Override
//            public void onStateChanged(View bottomSheet, int newState) {
//
////                int windowHeight = Util.getWindowSize().heightPixels;
////                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
////                    MapUtil.changeFragmentHeight(mapFragment, windowHeight - (int) Util.convertDp2Px(57));
////                    MapUtil.reboundMap(mMap);
////
////                } else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
////                    MapUtil.changeFragmentHeight(mapFragment, windowHeight - Math.round(bottomSheetHeight));
////                    MapUtil.reboundMap(mMap);
////
////                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
////                    MapUtil.changeFragmentHeight(mapFragment, windowHeight);
//////                    MapUtil.getInstance().reboundMap();
////                }
//            }
//
//            @Override
//            public void onSlide(View bottomSheet, float slideOffset) {
////                Util.changeFragmentHeight(mapFragment, Math.round(Util.getWindowSize().heightPixels - bottomSheetHeight * slideOffset));
////                MapUtil.getInstance().reboundMap(mMap);
//
//            }
//        });
    }

    private void setupSpinner(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.view_spinner_item, District.getDistrictList());

        dataAdapter.setDropDownViewResource(R.layout.view_spinner_item);
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    mMap.setOnCameraMoveListener(null);
                    loadAllCustomer(parent.getItemAtPosition(position).toString(), getCheckedFilter());

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void triggerCurrentLocation(LatLng latLng, int zomm){
        if (latLng != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zomm), 400, null);

        }

    }

    private void inputShopName(final String shop_type){
        btnNewCustomer.close(true);
        dialog = DialogPlus.newDialog(this)
                                            .setContentHolder(new ViewHolder(R.layout.view_input_shopname))
                                            .setGravity(Gravity.CENTER)
                                            .setBackgroundColorResId(R.drawable.colorwhite_corner)
                                            .setMargin(10,10,10,10)
                                            .setPadding(20,30,20,20)
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
                if (!edName.getText().toString().trim().equals("")){
                    openCustomerScreen(shop_type, edName.getText().toString().trim(), null);
                }
            }
        });

        dialog.show();
        edName.requestFocus();
        Util.showKeyboard(edName);
    }

    private void inputPhoneNumber(){
        btnNewCustomer.close(true);
        dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.view_input_phonenumber))
                .setGravity(Gravity.CENTER)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10,10,10,10)
                .setPadding(20,30,20,20)
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
                if (!edPhone.getText().toString().trim().equals("")){
                    openCustomerScreen(null, null, edPhone.getText().toString().trim());
                }
            }
        });

        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);
    }

    private void openCustomerScreen(final String shopType, final String shopName, final String phone){
        LocationConnect.getAddressFromLocation(getCurLocation().getLatitude(), getCurLocation().getLongitude(), new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Customer customer = new Customer(new JSONObject());
                JSONObject objectAdress = MapUtil.getAddressFromMapResult(result);
                try {
                    if (phone == null && shopType != null && shopName != null){
                        customer.put("shopType", shopType);
                        customer.put("signBoard", shopName);
                        customer.put("name","Anh "+ shopName.substring(shopName.lastIndexOf(" ")+1));
                        customer.put("phone", "");
                    }else if (shopType == null && shopName == null && phone != null ){
                        customer.put("shopType", Constants.shopType[0]);
                        customer.put("signBoard", "");
                        customer.put("name","");
                        customer.put("phone", phone);
                    }

                    customer.put("id",0);
                    customer.put("lat", Util.getInstance().getCurrentLocation().getLatitude());
                    customer.put("lng", Util.getInstance().getCurrentLocation().getLongitude());
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
    }

    private void loadAllCustomer(String district, final String isAll){
        progressLoading.setVisibility(View.VISIBLE);
        String param = "district="+ Util.encodeString(district);
        CustomerConnect.ListCustomer(param, new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                progressLoading.setVisibility(View.GONE);
                try {
                    listCustomer = new ArrayList<Customer>();
                    for (int i=0; i<result.length(); i++){
                        JSONObject object = result.getJSONObject(i);
                        if (object.getJSONObject("distributor").getString("id").equals(Distributor.getDistributorId()) || Distributor.getLocation().contains(object.getString("district"))){
                            listCustomer.add(setCustomerMarker(new Customer(object)));

                        }

                    }

                    MapUtil.resetMarker();
                    addMarkertoMap(true,listCustomer, isAll, true);
                    createRVCustomer(listCustomer);

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

    private Customer setCustomerMarker(Customer customer){
        Customer currentCustomer = customer;
        try {
            int customerStatus = new JSONObject(currentCustomer.getString("status")).getInt("id");
//            currentCustomer.put("checkincount", new JSONArray(currentCustomer.getString("checkIns")).length());
            currentCustomer.put("checkincount", currentCustomer.getInt("checkinCount"));
            currentCustomer.put("status", customerStatus);

            if (customerStatus ==1){
                currentCustomer.put("icon", R.drawable.ico_pin_red);
            }else if (customerStatus == 2){
                currentCustomer.put("icon", R.drawable.ico_pin_grey);
            }else {
                currentCustomer.put("icon", R.drawable.ico_pin_blue);

            }


        } catch (JSONException e) {

        }

        return currentCustomer;
    }

    private void loadAllCustomerDependLocation(final Boolean clearMap, Double lat , Double lng){
        progressLoading.setVisibility(View.VISIBLE);
        CustomerConnect.ListCustomerLocation(Util.encodeString(String.valueOf(lat)), Util.encodeString(String.valueOf(lng)), new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                progressLoading.setVisibility(View.GONE);
                try {
                    listCustomer = new ArrayList<Customer>();
                    for (int i=0; i<result.length(); i++){
                        JSONObject object = result.getJSONObject(i);
                        if (object.getJSONObject("distributor").getString("id").equals(Distributor.getDistributorId()) || Distributor.getLocation().contains(object.getString("district"))){
                            listCustomer.add(setCustomerMarker(new Customer(object)));

                        }
                    }
                    addMarkertoMap(clearMap,listCustomer, getCheckedFilter(), false);
                    createRVCustomer(listCustomer);

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

    @Override
    public void onInfoWindowClick(final Marker marker) {
        //mMap.setOnCameraMoveListener(this);
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
        mMap.setOnCameraMoveListener(MapsActivity.this);
        if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY){
            String responseString = data.getStringExtra(Constants.CUSTOMER);

            if (responseString.startsWith("delete")){
                removeMarker(responseString.split("-")[1]);
            }else {
                try {
                    Customer customer =new Customer(new JSONObject(responseString));
                    MapUtil.showUpdatedMarker(mMap, setCustomerMarker(customer));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

//    update Current marker when move
//    public  void animateMarker(final LatLng startPosition, final LatLng toPosition) {
//        final double duration = 1000;
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj = mMap.getProjection();
//        Point startPoint = proj.toScreenLocation(startPosition);
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) (elapsed / duration));
//                //Log.d(Constants.DMS_LOGS, "interpolator t:" + t + "start :" + start + " elapsed :" + elapsed);
//                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
//                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
//                LatLng newLatLng = new LatLng(lat, lng);
//                Log.e(Constants.DMS_LOGS, "Moving to" + newLatLng);
//                if (currentMarker != null && currentMarker.isVisible()){
//                    currentMarker.setPosition(newLatLng);
//                }
//
//
//                if (t < 1.0) {
//                    handler.postDelayed(this, 16);
//                }
//
//            }
//        });
//    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        LocationConnect.getAddressFromLocation(latLng.latitude, latLng.longitude, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Customer customer = new Customer(new JSONObject());
                try {
                    customer.put("id",0);
                    customer.put("shopType", Constants.shopType[0]);
                    customer.put("signBoard", "");
                    customer.put("name","");
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
        switch (checkedId){
            case R.id.map_filter_all:
                tvCount.setText(MapUtil.updateCustomerFilter(Constants.MARKER_ALL));

                break;

            case R.id.map_filter_interested:
                tvCount.setText(MapUtil.updateCustomerFilter(Constants.MARKER_INTERESTED));

                break;

            case R.id.map_filter_ordered:
                tvCount.setText(MapUtil.updateCustomerFilter(Constants.MARKER_ORDERED));

                break;
        }
    }

    private void addMarkertoMap(Boolean clearMap, List<Customer> list, String filter, Boolean isBound){
        tvCount.setText(MapUtil.addListMarkerToMap(clearMap, mMap, list, filter, isBound));

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        final Customer customer = new Customer((JSONObject) marker.getTag());
        currentPhone = customer.getString("phone");

        if (!currentPhone.equals("")){
            CustomCenterDialog.alertWithCancelButton(null, "Gọi điện thoại cho " + customer.getString("name") + " (" + customer.getString("phone") +")", "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    openCallScreen(currentPhone);

                }
            });
        }else {
            Util.showSnackbar("Khách hàng chưa có số điện thoại", null, null);
        }


    }

    @Override
    public void onCameraMove() {
        mHandler.removeCallbacks(mFilterTask);
        mHandler.postDelayed(mFilterTask, 1000);

    }

    private Runnable mFilterTask = new Runnable() {
        @Override public void run() {
            LatLng curLocation =  mMap.getCameraPosition().target;
            loadAllCustomerDependLocation(false, curLocation.latitude, curLocation.longitude);
        }
    };

    private String getCheckedFilter(){
        String s = Constants.MARKER_ALL;
        if (rdFilter.getCheckedRadioButtonId() == R.id.map_filter_all){
            s= Constants.MARKER_ALL;
        }else if (rdFilter.getCheckedRadioButtonId() == R.id.map_filter_interested){
            s= Constants.MARKER_INTERESTED;
        }else {
            s= Constants.MARKER_ORDERED;
        }
        return s;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.setOnCameraMoveListener(null);
        if (marker.getTag() != null){
            marker.showInfoWindow();
        }

        return true;
    }

    @Override
    public void onGlobalLayout() {
        coParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int height = coParent.getHeight()/2 ;
        ViewGroup.LayoutParams params = lnBottomSheet.getLayoutParams();
        params.height = height;
        lnBottomSheet.requestLayout();

        setupBottomSheet();
    }

    public void createRVCustomer(List<Customer> list){

        adapter = new MapListCustomerAdapter(list, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {

            }
        });
        rvCustomer.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCustomer.setLayoutManager(layoutManager);
        rvCustomer.setNestedScrollingEnabled(true);
    }

    private void openCallScreen(String phone){
        if (PermissionChecker.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PHONE_PERMISSION);
            mapFragment.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PHONE_PERMISSION);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(phone)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }

    public void checkGPS() {
        mMap.setMyLocationEnabled(true);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        Util.getInstance().showLoading("Kiểm tra thông tin vị trí");
        gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gps_enabled) {
            CustomCenterDialog.alertWithButton("Xác thực quyền truy cập vị trí", "Bạn cần mở GPS truy cập vị trí để sử dụng toàn bộ tính năng phần mềm", "Bật GPS", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    Util.getInstance().stopLoading(true);

                }
            });
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
            Util.getInstance().stopLoading(true);

            //UPDATE LAST LOCATION
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            Location curLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, true));
            if(curLocation != null) {
                Util.getInstance().setCurrentLocation(curLocation);

                MapUtil.resetMarker();
                LatLng latLng = new LatLng(getCurLocation().getLatitude(), getCurLocation().getLongitude());
//                currentMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)));

                //addCurrentMarker();
                triggerCurrentLocation(latLng, 15);

            }
        }

    }

}

