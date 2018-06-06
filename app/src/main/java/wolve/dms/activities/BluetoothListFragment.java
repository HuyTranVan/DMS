package wolve.dms.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.BluetoothListAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.BaseActivity.btsocket;
import static wolve.dms.BaseActivity.mBluetoothAdapter;
import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;

public class BluetoothListFragment extends DialogFragment implements View.OnClickListener {
    private Button btnCancel;
    private RecyclerView rvBluetooth;
    private View view;

//    private static BluetoothAdapter mBluetoothAdapter = null;
//    private static BluetoothSocket btsocket;
//    private static OutputStream outputStream;
    private List<BluetoothDevice> listDevice = new ArrayList<>();
    private BluetoothListAdapter adapter;
    private BaseActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_dialog_bluetooth_list,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void initializeView() {
        mActivity = (BaseActivity) getActivity();
        btnCancel = view.findViewById(R.id.btn_cancel);
        rvBluetooth = view.findViewById(R.id.dialog_bluetooth_list_rv);

    }

    private void intitialData() {
        createRVBluetoothList(listDevice);
        mActivity.registerBluetooth();
    }


    private void addEvent() {
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    private void createRVBluetoothList(List<BluetoothDevice> list){
        adapter = new BluetoothListAdapter(listDevice, new BluetoothListAdapter.CallbackBluetooth() {
            @Override
            public void OnDevice(BluetoothDevice device) {
                mActivity.connectBluetoothDevice(device, new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        finish();
                    }
                });

            }
        });
        rvBluetooth.setAdapter(adapter);
        rvBluetooth.setHasFixedSize(true);
        rvBluetooth.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvBluetooth.setLayoutManager(layoutManager);
    }


//    public void registerBluetooth() {
//        try {
//            if (initDevicesList() != 0) {
//                finish();
//                return;
//            }
//
//        } catch (Exception ex) {
//            finish();
//            return;
//        }
//        IntentFilter btIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        mActivity.registerReceiver(mActivity.mBTReceiver, btIntentFilter);
//
//    }

//    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
////                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
////                    connectBluetoothDevice(device);
////                }
//                addBluetoothItem2List(device);
//
//            }
//        }
//    };

//    private Runnable socketErrorRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            Util.showToast("Cannot establish connection");
//            mBluetoothAdapter.startDiscovery();
//
//        }
//    };

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

//    protected void connectBluetoothDevice(final BluetoothDevice device){
//        if (mBluetoothAdapter == null) {
//            return;
//        }
//        //Util.showSnackbar("Connecting to " + device.getName(), null, null);
//
//        Thread connectThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (device.getUuids()!= null){
//                        UUID uuid = device.getUuids()[0].getUuid();
//                        btsocket = device.createRfcommSocketToServiceRecord(uuid);
//                        btsocket.connect();
//                    }
//
//                } catch (IOException ex) {
//                    mActivity.runOnUiThread(socketErrorRunnable);
//                    try {
//                        btsocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    btsocket = null;
//                    return;
//                } finally {
//                    mActivity.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                if (btsocket != null){
////                                    tvBluetooth.setText(R.string.icon_bluetooth_connected);
////                                    tvBluetooth.setTextColor(getResources().getColor(R.color.colorBlue));
//                                    //tvPrinterName.setText("Đang kết nối: "+ btsocket.getRemoteDevice().getName());
//                                    //printerProgress.setVisibility(View.GONE);
//
//                                    Util.showToast("Đã kết nối");
//
//                                    CustomSQL.setString(Constants.BLUETOOTH_DEVICE, btsocket.getRemoteDevice().getAddress());
//                                    outputStream = btsocket.getOutputStream();
//
//                                    mBluetoothAdapter.cancelDiscovery();
//                                    finish();
//                                }
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//                    });
//                }
//            }
//        });connectThread.start();
//    }

//    public void showListBluetooth(List<BluetoothDevice> list) {
//        CustomCenterDialog.showBluetoothDevices(list, new BluetoothListAdapter.CallbackBluetooth() {
//            @Override
//            public void OnDevice(BluetoothDevice device) {
//                try {
//                    mActivity.connectBluetoothDevice(device);
//                    if (btsocket != null) {
//                        btsocket.close();
//                        btsocket = null;
//                    }
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
            try {
                Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
                if (btDeviceList.size() > 0) {
                    for (BluetoothDevice device : btDeviceList) {
                        addBluetoothItem2List(device);
//                        if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
//                            connectBluetoothDevice(device);
//                        }
//                            if (btDeviceList.contains(device) == false) {
//                                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
////                                    connectBluetoothDevice(device);
//                                }
//
//                                listDevice.add(device);
//
//                            }
                    }
                }
            } catch (Exception ex) {
            }
            mBluetoothAdapter.startDiscovery();
        }

    }

    protected void addBluetoothItem2List(BluetoothDevice device){
        Boolean exist = false;
        for (int i=0; i<listDevice.size(); i++){
            if (listDevice.get(i).getAddress().equals(device.getAddress()) ){
                exist = true;
                break;
            }
        }
        if (!exist){
            adapter.addItems(device);

        }
    }

    private void finish(){
        this.dismiss();
    }
}
