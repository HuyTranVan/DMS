package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseAdapter.WarehouseAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener, mListenerInfo;
    //private CallbackDeleteAdapter mDeleteListener;

    public WarehouseAdapter(List<BaseModel> list, CallbackObject listener,  CallbackObject listenerinfo) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = listener;
        this.mListenerInfo = listenerinfo;

        DataUtil.sortbyStringKey("isMaster",mData, false);
    }

    @Override
    public WarehouseAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_warehouse_item, parent, false);
        return new WarehouseAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<ProductGroup> list){
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final WarehouseAdapterViewHolder holder, final int position) {
        holder.tvGroupName.setText(String.format("%s (%d)",mData.get(position).getString("name"),
                                                                    mData.get(position).getInt("quantity")));
        holder.tvUsername.setText(Util.getIconString(R.string.icon_username, "   ", mData.get(position).getBaseModel("user").getString("displayName")));

        if (Util.isAdmin() || mData.get(position).getInt("user_id") == User.getId()){
            holder.tvInfo.setVisibility(View.VISIBLE);
        }else {
            holder.tvInfo.setVisibility(View.GONE);
        }
        if (mData.get(position).getInt("number_temp_import") >0){
            holder.tvTempImport.setVisibility(View.VISIBLE);
            holder.tvTempImport.setText(String.format("(%d) Chờ nhập kho", mData.get(position).getInt("number_temp_import")));

        }else {
            holder.tvTempImport.setVisibility(View.GONE);
        }
        switch (mData.get(position).getInt("isMaster")){
            case 1:
                holder.tvType.setText("Kho tổng");
                break;

            case 2:
                holder.tvType.setText("Kho tạm");
                break;

            case 3:
                holder.tvType.setText("Kho nhân viên");
                break;
        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResponse(mData.get(position));

            }
        });

        holder.tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListenerInfo.onResponse(mData.get(position));
            }
        });

        holder.line.setVisibility(position == mData.size()-1? View.GONE: View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class WarehouseAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvType, tvTempImport,  tvInfo, tvUsername;
        private RelativeLayout lnParent;
        private View line;

        public WarehouseAdapterViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.depot_item_name);
            tvUsername = (TextView) itemView.findViewById(R.id.depot_item_user);
            tvType = (TextView) itemView.findViewById(R.id.depot_item_depottype);
            tvInfo = itemView.findViewById(R.id.depot_item_depotinfo);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.depot_item_parent);
            line = itemView.findViewById(R.id.depot_item_seperateline);
            tvTempImport = itemView.findViewById(R.id.depot_item_number_temp_import);

        }

    }

}
