package wolve.dms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.orhanobut.dialogplus.DialogPlus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import wolve.dms.activities.AddProdGroupFragment;
import wolve.dms.activities.AddProductFragment;
import wolve.dms.callback.CallbackProcess;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;


public abstract class BaseActivity extends AppCompatActivity {
    protected LocationManager mLocationManager;
    public DialogPlus dialog;
    private boolean doubleBackToExitPressedOnce = false;
    public static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothSocket btsocket;
    public static OutputStream outputStream;

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

        addEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.getInstance().setCurrentActivity(this);

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
//            manager.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            manager.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        }


        manager.replace(setIdContainer(), fragmentReplace, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void showFragmentDialog(DialogFragment fragment){
        String tag = fragment.getClass().getSimpleName();
        DialogFragment myFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        DialogFragment fragmentReplace;
        if (myFragment != null) {
            fragmentReplace = myFragment;
        } else {
            fragmentReplace = fragment;
        }

        FragmentTransaction manager = this.getSupportFragmentManager().beginTransaction();
        fragmentReplace.show(manager, tag);
    }

    //Todo Location SETUP
    public Location getCurLocation() {
        return Util.getInstance().getCurrentLocation();
    }

    protected final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Util.getInstance().stopLoading(true);

            if (Util.getInstance().getCurrentActivity().getLocalClassName().equals("activities.MapsActivity")) {
                if (Util.getInstance().getCurrentLocation() != null) {
//                    Util.mapsActivity.animateMarker(
//                            new LatLng(Util.getInstance().getCurrentLocation().getLatitude(), Util.getInstance().getCurrentLocation().getLongitude()),
//                            new LatLng(location.getLatitude(), location.getLongitude()));

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

    //Todo Bluetooth SETUP
    @Override
    protected void onDestroy() {
        Util.getInstance().stopLoading(true);
        try {
            if(btsocket!= null){
                if (outputStream != null)
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
            //e.printStackTrace();
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

    public final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
                    connectBluetoothDevice(device, new CallbackProcess() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onSuccess(String name) {

                        }
                    });
                }

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

    public int initDevicesList() {
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
//        Util.showToast("Getting all available Bluetooth Devices");

        return 0;

    }

    public void  connectBluetoothDevice(final BluetoothDevice device, final CallbackProcess mListener){
        mListener.onStart();
        if (mBluetoothAdapter == null) {
            mListener.onError();
            return;

        }

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
                        if (btsocket != null)
                            btsocket.close();
                    } catch (IOException e) {
                        mListener.onError();
//                        e.printStackTrace();
                    }
                    btsocket = null;

                    return;
                } finally {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                if (btsocket != null){
                                    if (Util.getInstance().getCurrentActivity().getLocalClassName().equals("activities.ShopCartActivity")){
//                                        Util.shopCartActivity.tvBluetooth.setText(R.string.icon_bluetooth_connected);
//                                        Util.shopCartActivity.tvBluetooth.setTextColor(getResources().getColor(R.color.colorBlue));
                                    }
                                    CustomSQL.setString(Constants.BLUETOOTH_DEVICE, btsocket.getRemoteDevice().getAddress());
                                    outputStream = btsocket.getOutputStream();
                                    mBluetoothAdapter.cancelDiscovery();

                                    mListener.onSuccess(device.getName());
                                }else {
                                    mListener.onError();
                                }


                            } catch (IOException e) {
                                mListener.onError();
//                                e.printStackTrace();
                            }


                        }
                    });
                }
            }
        });connectThread.start();
    }



}
