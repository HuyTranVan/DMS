package wolve.dms.activities;

import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackProcess;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomFixSQL;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.CustomTopDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;


public abstract class BluetoothActivity extends BaseActivity implements CallbackObject {
    protected BluetoothListFragment bluFragment = null;
    protected BluetoothAdapter mBluetoothAdapter = null;
    protected BluetoothSocket btsocket;
    protected OutputStream outputStream;
    protected BluetoothDevice currentBluetooth = null;
    protected List<BluetoothDevice> listDevice = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(getResourceLayout());
//        Util.getInstance().setCurrentActivity(this);
//        findViewById();
//
//        //Handler handler = new Handler();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initialData();
//
//            }
//        }, 300);
//
//        addEvent();
        if (!Util.getDeviceName().equals(Constants.currentEmulatorDevice)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                checkPermission();

            } else {
                registerBluetooth();
            }


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

    private void checkPermission() {
        Activity context = Util.getInstance().getCurrentActivity();
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PermissionChecker.PERMISSION_GRANTED ){

            CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                    "Ứng dụng cần bạn đồng ý quyền truy cập bluetooth để tiếp tục",
                    "đồng ý",
                    "hủy",
                    new CallbackBoolean() {
                        @RequiresApi(api = Build.VERSION_CODES.S)
                        @Override
                        public void onRespone(Boolean result) {
                            if (result) {
                                ActivityCompat.requestPermissions(context, new String[]{
                                        Manifest.permission.BLUETOOTH_SCAN,
                                        Manifest.permission.BLUETOOTH_CONNECT

                                }, Constants.REQUEST_PERMISSION);

                            }

//                            else {
//                                returnPreActivity(false);
//
//                            }
                        }

                    });

        } else {
            registerBluetooth();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION) {
            if (grantResults.length > 0) {

                boolean hasDenied = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        hasDenied = true;
                        break;
                    }
                }

                if (!hasDenied) {
                    registerBluetooth();

                } else {
                    Util.showToast("Cấp quyền truy cập không thành công!");
                    CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                            "Ứng dụng chưa được cấp quyền đầy đủ",
                            "Cấp lại",
                            "hủy",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result) {
                                        checkPermission();

                                    }

//                                    else {
//                                        returnPreActivity(false);
//                                    }

                                }

                            });

                }

            }
        }

    }

    protected void addBluetoothItem2List(BluetoothDevice device) {
        Boolean exist = false;
        for (int i = 0; i < listDevice.size(); i++) {
            if (listDevice.get(i).getAddress().equals(device.getAddress())) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            listDevice.add(device);

            if (bluFragment != null) {
                bluFragment.updateList();
            }

        }
    }

    public void connectBluetoothDevice(final BluetoothDevice device, final CallbackProcess mListener) {
        mListener.onStart();
        if (mBluetoothAdapter == null) {
            mListener.onError();
            return;

        }

        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    if (device.getUuids() != null) {
                        UUID uuid = device.getUuids()[0].getUuid();
                        btsocket = null;
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
                                if (btsocket != null) {
                                    CustomSQL.setString(Constants.BLUETOOTH_DEVICE, btsocket.getRemoteDevice().getAddress());
                                    outputStream = btsocket.getOutputStream();
                                    if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    mBluetoothAdapter.cancelDiscovery();


                                    mListener.onSuccess(device.getName());
                                } else {
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
        });
        connectThread.start();
    }

    public int initDevicesList() {
        try {
            if (btsocket != null) {
                btsocket.close();

                btsocket = null;
            }

            if (mBluetoothAdapter != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return ;
                }
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

        return 0;

    }

    public final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                addBluetoothItem2List(device);

                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))) {
                    //updateViewWhileConnectBlu(device, false);

                }

            }
        }
    };

    private Runnable socketErrorRunnable = new Runnable() {

        @Override
        public void run() {
            Util.showToast("Cannot establish connection");

            if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothAdapter.startDiscovery();

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mBTReceiver);

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Util.getInstance().stopLoading(true);
        try {
            if (btsocket != null) {
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

    protected Boolean isPrinterConnected() {
        if (btsocket != null && btsocket.isConnected())
            return true;

        return false;

    }

    protected void openBluetoothSeclectFragment(){
        bluFragment = new BluetoothListFragment();
        showFragmentDialog(bluFragment);
    }

    protected void updateViewWhileConnectBlu(BluetoothDevice device, boolean showloading) {
        Util.getInstance().showLoading(showloading);
        connectBluetoothDevice(device, new CallbackProcess() {
            @Override
            public void onStart() {
//                tvPrinterName.setText(Constants.CONNECTING_PRINTER);
//                lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
//                currentBluetooth = null;
//                if (bluFragment != null) {
//                    bluFragment.updateItem(currentBluetooth, false);
//
//                }


            }

            @Override
            public void onError() {
//                Util.getInstance().stopLoading(true);
//                tvPrinterName.setText("Chưa kết nối được máy in");
//                Util.showToast(Constants.CONNECTED_PRINTER_ERROR);
//                lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
//                currentBluetooth = null;
//                if (bluFragment != null) {
//                    bluFragment.updateItem(currentBluetooth, false);
//
//                }


            }

            @Override
            public void onSuccess(String name) {
                Util.getInstance().stopLoading(true);

                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
//                tvPrinterName.setText(String.format(Constants.CONNECTED_PRINTER,
//                        CustomSQL.getString(Constants.PRINTER_SIZE),
//                        device.getName(),
//                        device.getAddress()));
//                lnBottom.setBackgroundColor(getResources().getColor(R.color.colorBlue));
//                currentBluetooth = device;
//                //bluFragment.updateItem(currentBluetooth, false);
//
//                if (bluFragment != null) {
//                    bluFragment.finish();
//
//                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothAdapter.startDiscovery();


        }
    }

//    @Override
//    public void onResponse(BaseModel object) {
//
//    }


}
