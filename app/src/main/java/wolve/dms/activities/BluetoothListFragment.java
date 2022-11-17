package wolve.dms.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.BluetoothListAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;

//import static wolve.dms.activities.BaseActivity.btsocket;
//import static wolve.dms.activities.BaseActivity.mBluetoothAdapter;


public class BluetoothListFragment extends DialogFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private Button btnCancel;
    private RecyclerView rvBluetooth;
    private View view;
    private RadioGroup rdSize;
    private RadioButton rd58, rd80;
    private RelativeLayout lnParent;

    //private List<BluetoothDevice> listDevice = new ArrayList<>();
    private BluetoothListAdapter adapter;
    private BluetoothActivity mActivity;
    private CallbackObject mListener;
//    protected OnDataPass mListener;

    public interface OnDataPass {
        void dataReturnFromFragment(String data1, String data2);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CallbackObject) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_dialog_bluetooth_list, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void initializeView() {
        mActivity = (BluetoothActivity) getActivity();
        lnParent =  view.findViewById(R.id.bluetooth_list_parent);
        btnCancel = view.findViewById(R.id.btn_cancel);
        rvBluetooth = view.findViewById(R.id.dialog_bluetooth_list_rv);
        rdSize = view.findViewById(R.id.bluetooth_list_size_all);
        rd58 = view.findViewById(R.id.bluetooth_list_size_58);
        rd80 = view.findViewById(R.id.bluetooth_list_size_80);


    }

    private void intitialData(){
        createRVBluetoothList(mActivity.listDevice);
        if (CustomSQL.getString(Constants.PRINTER_SIZE).equals("") || CustomSQL.getString(Constants.PRINTER_SIZE).equals(Constants.PRINTER_80)) {
            rd80.setChecked(true);
        } else {
            rd58.setChecked(true);
        }
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.bg_corner5_white);


        //registerBluetooth();
    }


    private void addEvent() {
        btnCancel.setOnClickListener(this);
        rdSize.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    public void updateList() {
        adapter.reloadList(mActivity.listDevice);
    }

    private void createRVBluetoothList(List<BluetoothDevice> list) {
        adapter = new BluetoothListAdapter(mActivity.listDevice, mActivity.currentBluetooth,  new BluetoothListAdapter.CallbackBluetooth() {
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        CustomSQL.setString(Constants.PRINTER_SIZE, i == R.id.bluetooth_list_size_80? Constants.PRINTER_80: Constants.PRINTER_57);
    }

    public void updateItem(BluetoothDevice device, boolean connected){
        adapter.updateItem(device, connected);

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
    protected void finish() {
        this.dismiss();
    }
}
