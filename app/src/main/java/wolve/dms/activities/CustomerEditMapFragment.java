package wolve.dms.activities;

import static wolve.dms.activities.BaseActivity.createGetParam;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GMapGetMethod;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.Province;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerEditMapFragment extends Fragment implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener {
    private View view ;
    private TextView tvTitle ;
    private CardView btnLocation;
    private ImageView btnBack, iconPin;
    private LinearLayout lnBottom;
    private CInputForm tvAddress, tvDistrict, tvCity;
    private Button btnSubmit ;
    public SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private BottomSheetBehavior mBottomSheetBehavior;
    private List<BaseModel> mProvinces = new ArrayList<>();


    private CustomerActivity mActivity;
    private Handler mHandlerMoveMap = new Handler();
    private FusedLocationProviderClient mFusedLocationClient;
    private BaseModel objectAdress = new BaseModel();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_map, container, false);

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    public void intitialData() {
        tvTitle.setText(String.format("Sửa vị trí %s", mActivity.tvTitle.getText().toString().toUpperCase()));
        showAddress(mActivity.currentCustomer);
        setupBottomSheet();
        cityEvent();


    }

    private void addEvent() {
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        tvDistrict.setFocusable(false);
        tvCity.setFocusable(false);
        tvAddress.textEvent();
//        vCover.setOnClickListener(this);
        //btnEditsubmit.setOnClickListener(this);

        //tvEdit.setOnClickListener(this);

//        addressEvent();


//        KeyboardVisibilityEvent.setEventListener(mActivity, new KeyboardVisibilityEventListener() {
//            @Override
//            public void onVisibilityChanged(boolean isOpen) {
//                rlBottom.setVisibility(isOpen ? View.GONE : View.VISIBLE);
//            }
//        });


    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.edit_map_map);

        tvTitle = view.findViewById(R.id.edit_map_title);
        btnBack = view.findViewById(R.id.icon_back);
        btnSubmit = view.findViewById(R.id.edit_map_submit);
        iconPin = view.findViewById(R.id.edit_map_pin);
        btnLocation = view.findViewById(R.id.edit_map_location);
        //tvEdit = view.findViewById(R.id.edit_map_edit);
//        vCover = view.findViewById(R.id.edit_map_cover);
//        tvStreet = view.findViewById(R.id.edit_map_address);
        tvAddress = view.findViewById(R.id.edit_map_address);
        tvDistrict = view.findViewById(R.id.edit_map_district);
        tvCity = view.findViewById(R.id.edit_map_province);
//        btnEditsubmit = view.findViewById(R.id.edit_map_edit_submit);
        lnBottom = view.findViewById(R.id.edit_map_bottomsheet);
//        rlAdd = view.findViewById(R.id.edit_map_address_parent);

    }

    private void backEvent() {
        mActivity.onBackPressed();

    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottom);
        mBottomSheetBehavior.setPeekHeight(15);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {


            }
        });
    }

    private void districtEvent() {
        Util.textEvent(tvAddress.getEdInput(), new CallbackString() {
            @Override
            public void Result(String s) {
                objectAdress.put("address", s);
                tvAddress.setText(String.format(Constants.addressFormat,
                        objectAdress.getString("address"),
                        objectAdress.getString("street"),
                        objectAdress.getString("district"),
                        objectAdress.getString("province")));
            }
        });
    }

    private void cityEvent() {
        if (objectAdress.getString("province").equals("")){
            tvCity.setDropdown(true, new CInputForm.ClickListener() {
                @Override
                public void onClick(View view) {
                    if (mProvinces.size() >0){
                        chooseProvince(mProvinces);

                    }else {{
                        getListProvince(new CallbackListObject() {
                            @Override
                            public void onResponse(List<BaseModel> list) {
                                mProvinces = list;
                                chooseProvince(mProvinces);
                            }
                        });

                    }}


                }
            });

        }
    }

    private void showAddress(BaseModel add) {
        objectAdress.put("address", add != null? add.getString("address"):"");
        objectAdress.put("district", add != null? add.getString("district"):"");
        objectAdress.put("province", add != null? add.getString("province"): "");

        tvAddress.setText( objectAdress.getString("address"));
        tvDistrict.setText(objectAdress.getString("district"));
        tvCity.setText(objectAdress.getString("province"));

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.edit_map_submit:
                LatLng center = mMap.getCameraPosition().target;
                mActivity.saveCustomerToLocal("lat", center.latitude);
                mActivity.saveCustomerToLocal("lng", center.longitude);
                mActivity.saveCustomerToLocal("address", tvAddress.getText().toString());
                mActivity.saveCustomerToLocal("district", tvDistrict.getText().toString());
                mActivity.saveCustomerToLocal("province", tvCity.getText().toString());

                mActivity.infoFragment.reshowAddress(mActivity.currentCustomer);

                backEvent();

                break;

            case R.id.edit_map_location:
                animateToCurrentLocation();

                break;



        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showPinDelay(500);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mActivity, R.raw.style_map));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Util.hideKeyboard(tvAddress);
            }
        });


    }

    private void animateToCurrentLocation() {
        getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float zoom = mMap.getCameraPosition().zoom < 15 ? 15 : mMap.getCameraPosition().zoom;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom), 800, null);

            }
        });


    }

    private void showPinDelay(int milisec) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){

                LatLng latLng = new LatLng(mActivity.currentCustomer.getDouble("lat"), mActivity.currentCustomer.getDouble("lng"));
                float zoom = mMap.getCameraPosition().zoom < 15 ? 15 : mMap.getCameraPosition().zoom;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 400, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        iconPin.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMap.setOnCameraMoveListener(CustomerEditMapFragment.this);

                            }
                        }, 1);

                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }
        }, milisec);
    }


    @Override
    public void onCameraMove() {
        //btnSubmit.setVisibility(View.GONE);

        mHandlerMoveMap.removeCallbacks(mFilterTask);
        mHandlerMoveMap.postDelayed(mFilterTask, 1000);
    }

    private Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            //btnSubmit.setVisibility(View.VISIBLE);

            LatLng center = mMap.getCameraPosition().target;
            updateLocation(center.latitude, center.longitude);


        }
    };

    private void updateLocation(double lat, double lng) {
        new GMapGetMethod(lat, lng, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                if (result != null){
                    showAddress(MapUtil.getAddressFromMapResult(result));

                }else {
                    showAddress(null);

                }


            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }

    private void getCurrentLocation(final LocationListener mListener) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        mFusedLocationClient.getLastLocation().addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    mListener.onLocationChanged(location);

                }
            }
        });
    }

    private void getListProvince(CallbackListObject listener){
        BaseModel param = createGetParam(ApiUtil.PROVINCES(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                DataUtil.sortbyStringKey("type", list, false);
                listener.onResponse(list);

            }

            @Override
            public void onError(String error){

            }
        }, 0).execute();

    }

    private void chooseProvince(List<BaseModel> list){
        CustomBottomDialog.choiceListObject("CHỌN TỈNH / THÀNH PHỐ", list, "name", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                tvCity.setText(object.getString("name"));


            }
        }, null);
    }


}
