package wolve.dms.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.BluetoothListAdapter;
import wolve.dms.callback.CallbackProcess;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
//import static wolve.dms.BaseActivity.btsocket;
//import static wolve.dms.BaseActivity.mBluetoothAdapter;
import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;

public class BluetoothListFragment extends DialogFragment implements View.OnClickListener {
    private Button btnCancel;
    private RecyclerView rvBluetooth;
    private View view;

    //private List<BluetoothDevice> listDevice = new ArrayList<>();
    private BluetoothListAdapter adapter;
    private PrintBillActivity mActivity;
    private OnDataPass mListener;

    public interface OnDataPass{
        void dataReturnFromFragment(String data1, String data2);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnDataPass) context;
    }

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
        mActivity = (PrintBillActivity) getActivity();
        btnCancel = view.findViewById(R.id.btn_cancel);
        rvBluetooth = view.findViewById(R.id.dialog_bluetooth_list_rv);

    }

    private void intitialData() {
        createRVBluetoothList(mActivity.listDevice);
        //registerBluetooth();
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

    public void updateList(){
        adapter.reloadList(mActivity.listDevice);
    }

    private void createRVBluetoothList(List<BluetoothDevice> list){
        adapter = new BluetoothListAdapter(mActivity.listDevice, new BluetoothListAdapter.CallbackBluetooth() {
            @Override
            public void OnDevice(final BluetoothDevice device) {
                if (mActivity.btsocket != null) {
                    try {

                        mActivity.btsocket.close();
                        mActivity.btsocket = null;


                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
                mActivity.updateViewWhileConnectBlu(device, true);
                //finish();

            }
        });
        Util.createLinearRV(rvBluetooth, adapter);

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
//        baseActivity.registerReceiver(mBTReceiver, btIntentFilter);
//
//    }
//
//    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                addBluetoothItem2List(device);
//
//            }
//        }
//    };
//
//    private int initDevicesList() {
//        try {
//            if (btsocket != null) {
//                btsocket.close();
//
//                btsocket = null;
//            }
//
//            if (mBluetoothAdapter != null) {
//                mBluetoothAdapter.cancelDiscovery();
//            }
//
//            finalize();
//
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            Util.showToast("Bluetooth not supported!!");
//
//            return -1;
//        }
//
//        if (mBluetoothAdapter.isDiscovering()) {
//            mBluetoothAdapter.cancelDiscovery();
//        }
//
//
//        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        try {
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        } catch (Exception ex) {
//            return -2;
//        }
//
//        //Util.showToast("Getting all available Bluetooth Devices");
//
//        return 0;
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
//            try {
//                Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
//                if (btDeviceList.size() > 0) {
//                    for (BluetoothDevice device : btDeviceList) {
//                        addBluetoothItem2List(device);
//
//                    }
//                }
//            } catch (Exception ex) {
//            }
//            mBluetoothAdapter.startDiscovery();
//        }
//
//    }
//
//    protected void addBluetoothItem2List(BluetoothDevice device){
//        Boolean exist = false;
//        for (int i=0; i<listDevice.size(); i++){
//            if (listDevice.get(i).getAddress().equals(device.getAddress()) ){
//                exist = true;
//                break;
//            }
//        }
//        if (!exist){
//            adapter.addItems(device);
//
//        }
//    }
//
    protected void finish(){
        this.dismiss();
    }
}
