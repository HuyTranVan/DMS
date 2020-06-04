package wolve.dms.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    public BluetoothListAdapter(List<BluetoothDevice> list, CallbackBluetooth callbackBluetooth) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = callbackBluetooth;

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

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class BluetoothListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvMac, tvLine;
        private LinearLayout lnParent;


        public BluetoothListViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.bluetooth_list_item_name);
            tvMac = itemView.findViewById(R.id.bluetooth_list_item_mac);
            tvLine = itemView.findViewById(R.id.bluetooth_list_item_line);
            lnParent = itemView.findViewById(R.id.bluetooth_list_item_parent);

        }

    }

    public interface CallbackBluetooth {
        void OnDevice(BluetoothDevice device);
    }


}
