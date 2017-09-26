package wolve.dms.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.Customer;
import wolve.dms.utils.Constants;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class MapsActivity extends BaseActivity implements OnMapReadyCallback,View.OnClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener, RadioGroup.OnCheckedChangeListener , GoogleMap.OnInfoWindowLongClickListener{
    public GoogleMap mMap;
    public Marker currentMarker;
    private FloatingActionMenu btnNewCustomer;
    private FloatingActionButton btnLocation, btnRepair, btnWash, btnAccesary, btnMaintain;
    public SupportMapFragment mapFragment;
    private DialogPlus dialog;
    private Spinner mSpinner;
    private RadioGroup rdFilter;

    public Boolean isStarted = true;
    private String currentDistrict = "Quận 2";
    private int firstDistrictPosition = 9;
    private ArrayList<Customer> listCustomer = new ArrayList<>();
    private LatLng currentLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.mapsActivity = this;

    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_map;
    }

    @Override
    public int setIdContainer() {
        return R.id.map_parent;
    }

    @Override
    public void initializeView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btnLocation = (FloatingActionButton) findViewById(R.id.map_current_location);
        btnNewCustomer = (FloatingActionMenu) findViewById(R.id.map_new_customer);
        btnRepair = (FloatingActionButton) findViewById(R.id.map_new_repair);
        btnWash = (FloatingActionButton) findViewById(R.id.map_new_wash);
        btnAccesary = (FloatingActionButton) findViewById(R.id.map_new_accessary);
        btnMaintain = (FloatingActionButton) findViewById(R.id.map_new_maintain);
        mSpinner = (Spinner) findViewById(R.id.map_spinner);
        rdFilter = (RadioGroup) findViewById(R.id.map_filter);

        btnLocation.setColorNormalResId(R.color.colorWhite);
        btnLocation.setColorPressedResId(R.color.colorGray);
        btnNewCustomer.setMenuButtonColorNormalResId(R.color.colorBlue);
        btnNewCustomer.setMenuButtonColorPressedResId(R.color.colorBlueDark);


        setupSpinner();


    }

    private void setupSpinner(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.view_spinner_item, Util.mListDistricts);

        dataAdapter.setDropDownViewResource(R.layout.view_spinner_item);
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDistrict = parent.getItemAtPosition(position).toString();
                currentLatlng = null;
                loadAllCustomer(currentDistrict, rdFilter.getCheckedRadioButtonId() == R.id.map_filter_all ? true : false);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinner.setSelection(firstDistrictPosition);
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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.map_current_location:
                triggerCurrentLocation(new LatLng(getCurLocation().getLatitude(), getCurLocation().getLongitude()), 16);
//                loadAllCustomerDependLocation(getCurLocation().getLatitude(), getCurLocation().getLongitude(), true);
                break;

            case R.id.map_new_repair:
                choiceShopType(Constants.shopType[0]);

                break;

            case R.id.map_new_wash:
                choiceShopType(Constants.shopType[1]);
                break;

            case R.id.map_new_maintain:
                choiceShopType(Constants.shopType[2]);

                break;

            case R.id.map_new_accessary:
                choiceShopType(Constants.shopType[3]);
                break;

            case R.id.map_new_customer:

                break;


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.getInstance().currentLocation == null) {
            checkLocationPermission();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//        final Handler h = new Handler();
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (Util.getInstance().getCurrentLocation() != null) {
//                    triggerCurrentLocation();
//
//                } else {
//                    h.postDelayed(this, 1000);
//                }
//            }
//        }, 10);
        if (getCurLocation() != null){
            LocationConnect.getAddressFromLocation(getCurLocation().getLatitude(), getCurLocation().getLongitude(), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    JSONObject objectAdress = MapUtil.getAddressFromMapResult(result);
                    try {
                        currentDistrict = objectAdress.getString("district");
                        loadAllCustomer(currentDistrict, rdFilter.getCheckedRadioButtonId() == R.id.map_filter_all ? true : false);
                        //set Value for Spinner
                        for (int i=0; i<Util.mListDistricts.size(); i++){
                            if (currentDistrict.equals(Util.mListDistricts.get(i))){
                                mSpinner.setSelection(i);
                                break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String error) {
                    loadAllCustomer(currentDistrict, rdFilter.getCheckedRadioButtonId() == R.id.map_filter_all ? true : false);
                }
            }, true);
        }

        mMap.setOnCameraIdleListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                btnNewCustomer.close(true);
            }
        });

    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                checkIsEnabledGPS();

            }else {
                Toast.makeText(this, "Cấp quyền truy cập không thành công!", Toast.LENGTH_LONG).show();
                Transaction.gotoHomeActivityRight(true);
            }
        }else  if (requestCode == Constants.REQUEST_PHONE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Util.quickMessage("Không thể gọi do chưa được cấp quyền", null, null);

            }
        }

    }

    @Override
    public void onCameraIdle() {

    }

    public void triggerCurrentLocation(LatLng latLng, int zomm) {
        if (latLng != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zomm), 200, null);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }else {
                Transaction.gotoHomeActivityRight(false);
            }
        }
        return true;
    }

    private void choiceShopType(final String shop_type){
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
        LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.input_shopnname_parent);
        TextView tvShopType = (TextView) dialog.findViewById(R.id.input_shopnname_type);
        final EditText edName = (EditText) dialog.findViewById(R.id.input_shopnname_name);
        ImageView btnSubmit = (ImageView) dialog.findViewById(R.id.input_shopnname_submit);

        tvShopType.setText(shop_type);

        edName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    openCustomerScreen(shop_type, edName.getText().toString().trim());
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
                    openCustomerScreen(shop_type, edName.getText().toString().trim());
                }
            }
        });

        dialog.show();
        edName.requestFocus();
        Util.showKeyboard(edName);
    }

    private void openCustomerScreen(final String shopType, final String shopName){
        LocationConnect.getAddressFromLocation(getCurLocation().getLatitude(), getCurLocation().getLongitude(), new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Customer customer = new Customer(new JSONObject());
                try {
                    customer.put("id",0);
                    customer.put("shopType", shopType);
                    customer.put("signBoard", shopName);
                    customer.put("name","Anh "+ shopName.substring(shopName.lastIndexOf(" ")+1));
                    customer.put("lat", Util.getInstance().getCurrentLocation().getLatitude());
                    customer.put("lng", Util.getInstance().getCurrentLocation().getLongitude());

                    JSONObject objectAdress = MapUtil.getAddressFromMapResult(result);
                    customer.put("province", objectAdress.getString("province"));
                    customer.put("district", objectAdress.getString("district"));
                    customer.put("street", objectAdress.getString("street"));
                    customer.put("address", objectAdress.getString("address"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Transaction.gotoCustomerActivity(customer.CustomertoString());

                currentLatlng = new LatLng(Util.getInstance().getCurrentLocation().getLatitude(), Util.getInstance().getCurrentLocation().getLongitude());

                dialog.dismiss();
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void loadAllCustomer(String district, final Boolean isAll){
        String param = "district="+ Util.encodeString(district);
        CustomerConnect.ListCustomer(param, new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                try {
                    listCustomer = new ArrayList<Customer>();
                    for (int i=0; i<result.length(); i++){
                        listCustomer.add(new Customer(result.getJSONObject(i)));

                    }
                    addMarkertoMap(listCustomer, isAll);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void loadAllCustomerDependLocation(Double lat , Double lng, final Boolean isAll){

        CustomerConnect.ListCustomerLocation(Util.encodeString(String.valueOf(lat)), Util.encodeString(String.valueOf(lng)), new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                try {
                    listCustomer = new ArrayList<Customer>();
                    for (int i=0; i<result.length(); i++){
                        listCustomer.add(new Customer(result.getJSONObject(i)));

                    }
                    addMarkertoMap(listCustomer, isAll);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }



    @Override
    public void onInfoWindowClick(final Marker marker) {
        final Customer customer = new Customer((JSONObject) marker.getTag());
        String param = customer.getString("id");
        CustomerConnect.GetCustomerDetail(param, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Transaction.gotoCustomerActivity(result.toString());

                currentLatlng = marker.getPosition();

            }

            @Override
            public void onError(String error) {

            }
        }, true);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadAllCustomer(currentDistrict, rdFilter.getCheckedRadioButtonId() == R.id.map_filter_all ? true : false);

    }

    public  void animateMarker(final LatLng startPosition, final LatLng toPosition) {
        final double duration = 1000;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(startPosition);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) (elapsed / duration));
                Log.d(Constants.DMS_LOGS, "interpolator t:" + t + "start :" + start + " elapsed :" + elapsed);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                LatLng newLatLng = new LatLng(lat, lng);
                Log.d(Constants.DMS_LOGS, "Moving to" + newLatLng);
                if (currentMarker != null && currentMarker.isVisible()){
                    currentMarker.setPosition(newLatLng);
                }


                if (t < 1.0) {
                    handler.postDelayed(this, 5000);
                }

            }
        });
    }

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
                currentLatlng = latLng;

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
                addMarkertoMap(listCustomer, true);
                break;

            case R.id.map_filter_interested:
                addMarkertoMap(listCustomer, false);
                break;
        }
    }

    private void addMarkertoMap(ArrayList<Customer> list, Boolean isAll){
        mMap.clear();
        LatLng currentPoint = new LatLng(Util.getInstance().getCurrentLocation().getLatitude(), Util.getInstance().getCurrentLocation().getLongitude());
        currentMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(currentPoint).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)));
        MapUtil.getInstance().addMarkerToMap(mMap, list,currentLatlng , isAll);

        if (currentLatlng != null){
            triggerCurrentLocation(currentLatlng, 15);
        }



    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        final Customer customer = new Customer((JSONObject) marker.getTag());
        final String phone = customer.getString("phone");

        if (!phone.equals("")){
            Util.alertWithCancelButton(null, "Gọi điện thoại cho " + customer.getString("name") + " (" + customer.getString("phone") +")", "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PHONE_PERMISSION);

                        return;
                    }

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Uri.encode(phone)));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }
            });
        }else {
            Util.quickMessage("Khách hàng chưa có số điện thoại", null, null);
        }


    }
}
