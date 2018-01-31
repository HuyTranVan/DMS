package wolve.dms;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;


import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import wolve.dms.activities.AddProdGroupFragment;
import wolve.dms.activities.AddProductFragment;
import wolve.dms.activities.MapsActivity;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;
import static wolve.dms.utils.Constants.REQUEST_PERMISSION_LOCATION;


public abstract class BaseActivity extends AppCompatActivity {
    private LocationManager mLocationManager;
    private float LOCATION_REFRESH_DISTANCE = 1;
    private long LOCATION_REFRESH_TIME = 100;
    public DialogPlus dialog;
    private boolean doubleBackToExitPressedOnce = false;
    static private BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());
        Util.getInstance().setCurrentActivity(this);
        findViewById();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initialData();

            }
        }, 200);

//        initialData();

        addEvent();
    }

    public abstract int getResourceLayout();

    public abstract int setIdContainer();

    public abstract void findViewById();

    public abstract void initialData();

    public abstract void addEvent();

    //change fragment with bundle
    public void changeFragment(Fragment fragment, Bundle bundle, boolean isAnimation) {
        String tag = fragment.getClass().getSimpleName();
        fragment.setArguments(bundle);
        FragmentTransaction manager = this.getSupportFragmentManager().beginTransaction();

        if (isAnimation) {
//            manager.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            manager.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        }

        manager.replace(setIdContainer(), fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    //change fragment
    public void changeFragment(Fragment fragment, boolean isAnimation) {
        String tag = fragment.getClass().getSimpleName();
        Fragment myFragment = getSupportFragmentManager().findFragmentByTag(tag);
        Fragment fragmentReplace;
        if (myFragment != null) {
            fragmentReplace = myFragment;
        } else {
            fragmentReplace = fragment;
        }
        FragmentTransaction manager = this.getSupportFragmentManager().beginTransaction();
        if (isAnimation) {
            manager.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        }


        manager.replace(setIdContainer(), fragmentReplace, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public Location getCurLocation() {
        return Util.getInstance().getCurrentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.getInstance().setCurrentActivity(this);

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Util.getInstance().stopLoading(true);

            if (Util.getInstance().getCurrentActivity().getLocalClassName().equals("activities.MapsActivity")) {
                if (Util.getInstance().getCurrentLocation() != null) {
                    Util.mapsActivity.animateMarker(
                            new LatLng(Util.getInstance().getCurrentLocation().getLatitude(), Util.getInstance().getCurrentLocation().getLongitude()),
                            new LatLng(location.getLatitude(), location.getLongitude()));

                }
            }
            Util.getInstance().setCurrentLocation(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d(Constants.DMS_LOGS, "LocationProvider.AVAILABLE");

                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d(Constants.DMS_LOGS, "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d(Constants.DMS_LOGS, "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(Constants.DMS_LOGS, "Location enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(Constants.DMS_LOGS, "Location disabled");
        }

    };

    public void checkGPS(CallbackBoolean mListener) {
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
                mListener.onRespone(true);
            }
        }

    }

    @Override
    public void onBackPressed() {
        switch (Util.getInstance().getCurrentActivity().getLocalClassName()){
            case "activities.MapsActivity":
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }else {
                    Transaction.gotoHomeActivityRight(true);
                }

                break;

            case "activities.HomeActivity":
                if (doubleBackToExitPressedOnce) {
                    finish();
                }

                this.doubleBackToExitPressedOnce = true;
                Util.showToast("Ấn Back để thoát khỏi ứng dụng");

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

                break;

            case "activities.StatisticalActivity":
                finish();
                Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;

            case "activities.ProductGroupActivity":
                Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
                if(mFragment != null && mFragment instanceof AddProdGroupFragment
                        ||mFragment != null && mFragment instanceof AddProductFragment) {
                    getSupportFragmentManager().popBackStack();
                }else {
                    Transaction.gotoHomeActivityRight(true);
                }

                break;


            case "activities.ProductActivity":
                mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
                if(mFragment != null && mFragment instanceof AddProdGroupFragment
                        ||mFragment != null && mFragment instanceof AddProductFragment) {
                    getSupportFragmentManager().popBackStack();
                }else {
                    Transaction.gotoHomeActivityRight(true);
                }

                break;

            case "activities.StatusActivity":
                mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
                if(mFragment != null && mFragment instanceof AddProdGroupFragment
                        ||mFragment != null && mFragment instanceof AddProductFragment) {
                    getSupportFragmentManager().popBackStack();
                }else {
                    Transaction.gotoHomeActivityRight(true);
                }

                break;




        }
    }

    @Override
    protected void onDestroy() {
        Util.getInstance().stopLoading(true);
        try {
            if(btsocket!= null){
                outputStream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mBTReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerBluetooth() {
        try {
            if (initDevicesList() != 0) {
                this.finish();
                return;
            }

        } catch (Exception ex) {
            this.finish();
            return;
        }
        IntentFilter btIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTReceiver, btIntentFilter);

    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
                    connectBluetoothDevice(device);
                }

//                listDevice.add(device);

            }
        }
    };

    private Runnable socketErrorRunnable = new Runnable() {

        @Override
        public void run() {
            Util.showToast("Cannot establish connection");
            mBluetoothAdapter.startDiscovery();

        }
    };

    private int initDevicesList() {
        try {
            if (btsocket != null) {
                btsocket.close();

                btsocket = null;
            }

            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }

            finalize();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Util.showToast("Bluetooth not supported!!");

            return -1;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }


        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } catch (Exception ex) {
            return -2;
        }

        //Util.showToast("Getting all available Bluetooth Devices");

        return 0;

    }

    private void connectBluetoothDevice(final BluetoothDevice device){
        if (mBluetoothAdapter == null) {
            return;
        }
        Util.showSnackbar("Connecting to " + device.getName(), null, null);

        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (device.getUuids()!= null){
                        UUID uuid = device.getUuids()[0].getUuid();
                        btsocket = device.createRfcommSocketToServiceRecord(uuid);
                        btsocket.connect();
                    }

                } catch (IOException ex) {
                    runOnUiThread(socketErrorRunnable);
                    try {
                        btsocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btsocket = null;
                    return;
                } finally {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (btsocket != null){
//                                    tvBluetooth.setText(R.string.icon_bluetooth_connected);
//                                    tvBluetooth.setTextColor(getResources().getColor(R.color.colorBlue));
                                    //tvPrinterName.setText("Đang kết nối: "+ btsocket.getRemoteDevice().getName());
                                    //printerProgress.setVisibility(View.GONE);

                                    CustomSQL.setString(Constants.BLUETOOTH_DEVICE, btsocket.getRemoteDevice().getAddress());
                                    outputStream = btsocket.getOutputStream();

                                    mBluetoothAdapter.cancelDiscovery();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            }
        });connectThread.start();
    }



}
