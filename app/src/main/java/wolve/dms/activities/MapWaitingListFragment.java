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

import wolve.dms.R;
import wolve.dms.apiconnect.LocationConnect;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class MapWaitingListFragment extends Fragment implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener{
    private View view;
    private TextView tvTitle, btnLocation;
    private ImageView btnBack;
    private LinearLayout rlBottom;
    public SupportMapFragment mapFragment;
    private GoogleMap mMap;


    private MapsActivity mActivity;
    private Handler mHandlerMoveMap = new Handler();
    private FusedLocationProviderClient mFusedLocationClient;
    private BaseModel objectAdress = new BaseModel();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show_waitinglist_map,container,false);

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    public void intitialData() {



    }

    private void addEvent() {
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        btnBack.setOnClickListener(this);
        btnLocation.setOnClickListener(this);


    }

    private void initializeView() {
        mActivity = (MapsActivity) getActivity();
        btnBack = view.findViewById(R.id.icon_back);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.waitinglist_map);
        tvTitle = view.findViewById(R.id.waitinglist_title);
        btnLocation = view.findViewById(R.id.waitinglist_location);

    }

    private void backEvent(){
        mActivity.onBackPressed();



    }


    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                backEvent();

                break;


            case R.id.waitinglist_location:
                animateToCurrentLocation();

                break;


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showPinDelay(1500);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mActivity, R.raw.style_map));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        //mActivity.addListMarkertoMap(mMap, true, mActivity.listCustomerWaiting, true);

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

//                LatLng latLng = new LatLng(mActivity.currentCustomer.getDouble("lat"),mActivity.currentCustomer.getDouble("lng") );
//                float zoom = mMap.getCameraPosition().zoom < 15 ? 15 : mMap.getCameraPosition().zoom;
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 400, new GoogleMap.CancelableCallback() {
//                    @Override
//                    public void onFinish() {
//                        iconPin.setVisibility(View.VISIBLE);
//                        new Handler().postDelayed (new Runnable() {
//                            @Override
//                            public void run() {
//                                mMap.setOnCameraMoveListener(MapWaitingListFragment.this);
//
//                            }}, 1);
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });

            }}, milisec);
    }


    @Override
    public void onCameraMove() {
//        btnSubmit.setVisibility(View.GONE);
//
//        mHandlerMoveMap.removeCallbacks(mFilterTask);
//        mHandlerMoveMap.postDelayed(mFilterTask, 1000);
    }

    private Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
//            btnSubmit.setVisibility(View.VISIBLE);
//
//            LatLng center = mMap.getCameraPosition().target;
//            updateLocation(center.latitude , center.longitude);


        }
    };

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

}
