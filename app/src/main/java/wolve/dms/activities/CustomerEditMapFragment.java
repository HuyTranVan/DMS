package wolve.dms.activities;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Province;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerEditMapFragment extends Fragment implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener{
    private View view, vCover;
    private TextView tvAddress, tvTitle;
    private ImageView btnBack, iconPin;
    private CTextIcon btnLocation;
    private CTextIcon tvEdit;
    private CardView lnEditDetail;
    private LinearLayout rlBottom;
    private EditText tvAdd, tvStreet, tvDistrict, tvCity;
    private Button btnSubmit, btnEditsubmit;
    public SupportMapFragment mapFragment;
    private GoogleMap mMap;


    private CustomerActivity mActivity;
    private Handler mHandlerMoveMap = new Handler();
    private FusedLocationProviderClient mFusedLocationClient;
    private BaseModel objectAdress = new BaseModel();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_map,container,false);

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    public void intitialData() {
        tvTitle.setText(String.format("Sửa vị trí %s",mActivity.tvTitle.getText().toString().toUpperCase()));
        showAddress(mActivity.currentCustomer);

        tvAdd.setText(objectAdress.getString("address"));
        tvStreet.setText(objectAdress.getString("street"));
        tvDistrict.setText(objectAdress.getString("district"));
        tvCity.setText(objectAdress.getString("province"));


    }

    private void addEvent() {
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        vCover.setOnClickListener(this);
        //btnEditsubmit.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        streetEvent();
        addressEvent();

        KeyboardVisibilityEvent.setEventListener(mActivity, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                rlBottom.setVisibility(isOpen? View.GONE : View.VISIBLE);
            }
        });



    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.edit_map_map);
        tvAddress = view.findViewById(R.id.edit_map_address);
        tvTitle = view.findViewById(R.id.edit_map_title);
        btnBack = view.findViewById(R.id.icon_back);
        btnSubmit = view.findViewById(R.id.edit_map_submit);
        iconPin = view.findViewById(R.id.edit_map_pin);
        btnLocation = view.findViewById(R.id.edit_map_location);
        tvEdit = view.findViewById(R.id.edit_map_edit);
        vCover = view.findViewById(R.id.edit_map_cover);
        lnEditDetail = view.findViewById(R.id.edit_map_detail);
        tvAdd = view.findViewById(R.id.edit_map_add);
        tvStreet = view.findViewById(R.id.edit_map_street);
        tvDistrict = view.findViewById(R.id.edit_map_district);
        tvCity = view.findViewById(R.id.edit_map_city);
//        btnEditsubmit = view.findViewById(R.id.edit_map_edit_submit);
        rlBottom = view.findViewById(R.id.edit_map_bottom);
//        rlAdd = view.findViewById(R.id.edit_map_address_parent);

    }

    private void backEvent(){
        if (lnEditDetail.getVisibility() == View.VISIBLE){
            lnEditDetail.setVisibility(View.GONE);
            vCover.setVisibility(View.GONE);

        }else {
            mActivity.onBackPressed();
        }


    }

    private void addressEvent(){
        Util.textEvent(tvAdd, new CallbackString() {
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

    private void streetEvent(){
        Util.textEvent(tvStreet, new CallbackString() {
            @Override
            public void Result(String s) {
                objectAdress.put("street", s);
                tvAddress.setText(String.format(Constants.addressFormat,
                        objectAdress.getString("address"),
                        objectAdress.getString("street"),
                        objectAdress.getString("district"),
                        objectAdress.getString("province")));
            }
        });
    }

    private void showAddress(BaseModel add){
        objectAdress.put("address",add.getString("address"));
        objectAdress.put("street", add.getString("street"));
        objectAdress.put("district", add.getString("district"));
        objectAdress.put("province", add.getString("province"));


        tvAddress.setText(String.format(Constants.addressFormat,
                objectAdress.getString("address"),
                objectAdress.getString("street"),
                objectAdress.getString("district"),
                objectAdress.getString("province")));

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.edit_map_submit:
                LatLng center = mMap.getCameraPosition().target;
                mActivity.saveCustomerToLocal("lat", center.latitude);
                mActivity.saveCustomerToLocal("lng", center.longitude);
                mActivity.saveCustomerToLocal("address", objectAdress.getString("address"));
                mActivity.saveCustomerToLocal("street", objectAdress.getString("street"));
                mActivity.saveCustomerToLocal("district", objectAdress.getString("district"));
                mActivity.saveCustomerToLocal("province", objectAdress.getString("province"));

                mActivity.infoFragment.reshowAddress(objectAdress);

                backEvent();

                break;

            case R.id.edit_map_location:
                animateToCurrentLocation();

                break;

            case R.id.edit_map_edit:
                tvEdit.setRotation(tvEdit.getRotation() + 180);
                if (lnEditDetail.getVisibility() == View.VISIBLE){
                    lnEditDetail.setVisibility(View.GONE);
                    vCover.setVisibility(View.GONE);


                }else {
                    lnEditDetail.setVisibility(View.VISIBLE);
                    vCover.setVisibility(View.VISIBLE);
                    //tvAdd.setFocusableInTouchMode(true);

                    tvAdd.setSelection(objectAdress.getString("address").length());
                    Util.showKeyboardEditTextDelay(tvAdd);
                }

//                rlAdd.setVisibility(View.GONE);
//                lnEditParent.setVisibility(View.VISIBLE);
//                vCover.setVisibility(View.VISIBLE);

                break;

            case R.id.edit_map_cover:
                Util.hideKeyboard(v);
                lnEditDetail.setVisibility(View.GONE);
                vCover.setVisibility(View.GONE);
                break;


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showPinDelay(1500);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mActivity, R.raw.style_map));


    }

    private void animateToCurrentLocation(){
        getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float zoom = mMap.getCameraPosition().zoom < 15 ? 15 : mMap.getCameraPosition().zoom;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom), 800, null);
            }
        });


    }

    private void showPinDelay(int milisec){
        new Handler().postDelayed (new Runnable() {
            @Override
            public void run() {

                LatLng latLng = new LatLng(mActivity.currentCustomer.getDouble("lat"),mActivity.currentCustomer.getDouble("lng") );
                float zoom = mMap.getCameraPosition().zoom < 15 ? 15 : mMap.getCameraPosition().zoom;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 400, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        iconPin.setVisibility(View.VISIBLE);
                        new Handler().postDelayed (new Runnable() {
                            @Override
                            public void run() {
                                mMap.setOnCameraMoveListener(CustomerEditMapFragment.this);

                            }}, 1);

                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }}, milisec);
    }


    @Override
    public void onCameraMove() {
        btnSubmit.setVisibility(View.GONE);

        mHandlerMoveMap.removeCallbacks(mFilterTask);
        mHandlerMoveMap.postDelayed(mFilterTask, 1000);
    }

    private Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            btnSubmit.setVisibility(View.VISIBLE);

            LatLng center = mMap.getCameraPosition().target;
            updateLocation(center.latitude , center.longitude);


        }
    };

    private void updateLocation( double lat, double lng){
        LocationConnect.getAddressFromLocation(lat, lng, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                showAddress(MapUtil.getAddressFromMapResult(result));
            }

            @Override
            public void onError(String error) {

            }
        }, true);
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

//    private void shopNameEvent(){
//        edShopName.addTextChangeListenter(new CInputForm.OnQueryTextListener() {
//            @Override
//            public boolean textChanged(String query) {
//                if (!firstName.contains("Anh ")) {
//                    edName.setText("Anh " + query.substring(query.trim().lastIndexOf(" ") + 1));
//                }
//                tvTitle.setText(edShopType.getText().toString() + " - " + query);
//                return true;
//            }
//        });
//    }
//
//    private void setProvinceEvent(){
//        edCity.setDropdown(true, new CInputForm.ClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String curentProvince = edCity.getText().toString();
//
//                CustomBottomDialog.choiceList("CHỌN TỈNH", Province.getListProvince(), new CustomBottomDialog.StringListener() {
//                    @Override
//                    public void onResponse(String content) {
//                        edCity.setText(content);
//                        if (!edCity.getText().toString().equals(curentProvince)){
//                            edDistrict.setText("");
//                        }
//                    }
//                });
//
//            }
//        });
//
//    }

//    private void setDistrictEvent(){
//        edDistrict.setDropdown(true, new CInputForm.ClickListener() {
//            @Override
//            public void onClick(View view) {
//                SystemConnect.getDistrict(Province.getDistrictId(edCity.getText().toString()), new CallbackCustomList() {
//                    @Override
//                    public void onResponse(List<BaseModel> results) {
//                        List<String> list = new ArrayList<>();
//                        for (int i=0; i<results.size(); i++){
//                            //JSONObject object = result.getJSONObject(i);
//                            if (results.get(i).getString("name").matches(Util.DETECT_NUMBER)){
//                                list.add(String.format("%s %s",results.get(i).getString("type"),results.get(i).getString("name")));
//                            }else {
//                                list.add(results.get(i).getString("name"));
//                            }
//
//                        }
//
//                        CustomBottomDialog.choiceList("CHỌN QUẬN/HUYỆN",list , new CustomBottomDialog.StringListener() {
//                            @Override
//                            public void onResponse(String content) {
//                                edDistrict.setText(content);
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        Util.showToast("Không thể lấy danh sách quận/ huyện");
//
//                    }
//                });
//
//            }
//        });
//
//    }

}
