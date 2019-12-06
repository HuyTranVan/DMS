package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.models.Customer;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class MapListCustomerAdapter extends RecyclerView.Adapter<MapListCustomerAdapter.MapListCustomerViewHolder> {
    private List<Customer> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;

    public MapListCustomerAdapter(List<Customer> data, CallbackClickAdapter callbackClickAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = callbackClickAdapter;
        Collections.reverse(mData);
    }

    @Override
    public MapListCustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_map_customers_item, parent, false);
        return new MapListCustomerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MapListCustomerViewHolder holder, final int position) {
//        try {

        holder.tvShopName.setText(Constants.getShopName(mData.get(position).getString("shopType") ) + " " + mData.get(position).getString("signBoard"));
        holder.tvAddress.setText(mData.get(position).getString("street") + " - " + mData.get(position).getString("district"));

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MapListCustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvShopName, tvAddress;
        private RelativeLayout lnParent;

        public MapListCustomerViewHolder(View itemView) {
            super(itemView);

            tvShopName = (TextView) itemView.findViewById(R.id.map_customers_item_shopname);
            tvAddress = (TextView) itemView.findViewById(R.id.map_customers_item_address);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.map_customers_item_parent);

        }

    }



}
