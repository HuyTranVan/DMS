package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.StatisticalBillsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackInt mListener;

    public WaitingListAdapter(List<BaseModel> data, CallbackInt listener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        mData = data;


    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_waiting_list_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        holder.tvNumber.setText(mData.size() >0? String.valueOf(mData.size() -position) : "");
        BaseModel customer = mData.get(position).getBaseModel("customer");
        holder.tvsignBoard.setText(Constants.getShopName(customer.getString("shopType") ) + " " + customer.getString("signBoard"));
        holder.tvAddress.setText(String.format("%s %s, %s",
                customer.getString("address"),
                customer.getString("street"),
                customer.getString("district")));


        holder.vLine.setVisibility(position == mData.size()-1? View.GONE:View.VISIBLE);
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = String.format(Api_link.CUSTOMER_TEMP_NEW_PARAM, customer.getInt("id"), User.getId());
                CustomerConnect.CreateCustomerWaitingList(param, new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {
                        if (result.getBoolean("success")){
                            mData.remove(position);
                            mListener.onResponse(mData.size());
                            notifyDataSetChanged();

                        }

                    }

                    @Override
                    public void onError(String error) {

                    }
                }, false);



            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvAddress, tvDelete;
        private View vLine;
        private LinearLayout lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.waiting_list_item_number);
            tvsignBoard = itemView.findViewById(R.id.waiting_list_item_signboard);
            tvAddress = itemView.findViewById(R.id.waiting_list_item_address);
            tvDelete = itemView.findViewById(R.id.waiting_list_item_delete);
            vLine =itemView.findViewById(R.id.waiting_list_item_line);

        }

    }


}
