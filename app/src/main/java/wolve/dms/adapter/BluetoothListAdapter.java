package wolve.dms.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetoothListViewHolder> {
    private List<BluetoothDevice> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBluetooth mListener;
    private BluetoothDevice mDevice;

    public BluetoothListAdapter(List<BluetoothDevice> list, BluetoothDevice currentdevice, CallbackBluetooth callbackBluetooth) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = callbackBluetooth;
        this.mDevice = currentdevice;

    }

    @Override
    public BluetoothListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_bluetooth_list_item, parent, false);
        return new BluetoothListViewHolder(itemView);
    }

    public void addItems(BluetoothDevice item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void reloadList(List<BluetoothDevice> list) {
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final BluetoothListViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getName());
        holder.tvMac.setText(mData.get(position).getAddress());
        holder.tvLine.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnDevice(mData.get(position));
            }
        });

        if (mDevice != null){
            if (mData.get(position).getName() != null &&
                    mData.get(position).getName().equals(mDevice.getName()) &&
                    mData.get(position).getAddress().equals(mDevice.getAddress()) ){
                holder.tvName.setTypeface(null, Typeface.BOLD);
                holder.tvConnected.setVisibility(View.VISIBLE);

            }else {
                holder.tvName.setTypeface(null, Typeface.NORMAL);
                holder.tvConnected.setVisibility(View.GONE);

            }

        }else {
            holder.tvName.setTypeface(null, Typeface.NORMAL);
            holder.tvConnected.setVisibility(View.GONE);

        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class BluetoothListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvMac, tvLine, tvConnected;
        private RelativeLayout lnParent;


        public BluetoothListViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.bluetooth_list_item_name);
            tvMac = itemView.findViewById(R.id.bluetooth_list_item_mac);
            tvLine = itemView.findViewById(R.id.bluetooth_list_item_line);
            lnParent = itemView.findViewById(R.id.bluetooth_list_item_parent);
            tvConnected = itemView.findViewById(R.id.bluetooth_list_item_connected);

        }

    }

    public void updateItem(BluetoothDevice device, boolean connected){
        mDevice = connected? device: null;
        notifyDataSetChanged();

//        for (int i=0; i<mData.size(); i++){
//            if (mData.get(i).getName() != null &&
//                mData.get(i).getName().equals(device.getName()) &&
//                mData.get(i).getAddress().equals(device.getAddress()) ){
//
//                holder.tvName.setTypeface(null, Typeface.BOLD);
//                holder.tvConnected.setVisibility(View.VISIBLE);
//
//            }else {
//                holder.tvName.setTypeface(null, Typeface.NORMAL);
//                holder.tvConnected.setVisibility(View.GONE);
//
//            }
//
//
//
//
//        }

    }
    public interface CallbackBluetooth {
        void OnDevice(BluetoothDevice device);
    }


}
