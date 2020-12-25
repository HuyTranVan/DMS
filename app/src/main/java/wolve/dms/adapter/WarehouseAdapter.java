package wolve.dms.adapter;

import android.content.Context;
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
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseAdapter.WarehouseAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListenerImport, mListenerInfo, mListenerReturn, mListenerEdit;
    public static String[] menu = new String[]{
            Util.getIconString(R.string.icon_info, "    ", "Thông tin"),
            Util.getIconString(R.string.icon_depot, "   ", "Tồn kho"),
            Util.getIconString(R.string.icon_reply, "    ", "Trả hàng")};

    public WarehouseAdapter(List<BaseModel> list,
                            CallbackObject listener,
                            CallbackObject listenerinfo,
                            CallbackObject listenerreturn,
                            CallbackObject listeneredit) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListenerImport = listener;
        this.mListenerInfo = listenerinfo;
        this.mListenerReturn = listenerreturn;
        this.mListenerEdit = listeneredit;

        DataUtil.sortbyStringKey("isMaster", mData, false);
    }

    @Override
    public WarehouseAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_warehouse_item, parent, false);
        return new WarehouseAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<ProductGroup> list) {
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();

    }

    @Override
    public void onBindViewHolder(final WarehouseAdapterViewHolder holder, final int position) {
        if (mData.get(position).getInt("isMaster") == 1){
            holder.tvGroupName.setText(mData.get(position).getString("name"));
        }else {
            holder.tvGroupName.setText(String.format("%s (%d)", mData.get(position).getString("name"),
                    mData.get(position).getInt("quantity")));
        }

        holder.tvUsername.setText(Util.getIconString(R.string.icon_username, "   ", mData.get(position).getBaseModel("user").getString("displayName")));

        if (Util.isAdmin() || mData.get(position).getInt("user_id") == User.getId()) {
            holder.tvMenu.setVisibility(View.VISIBLE);
        } else {
            holder.tvMenu.setVisibility(View.GONE);
        }
        if (mData.get(position).getInt("number_temp_import") > 0) {
            holder.tvTempImport.setVisibility(View.VISIBLE);
            holder.tvTempImport.setText(String.format("(%d) Chờ nhập kho", mData.get(position).getInt("number_temp_import")));

        } else {
            holder.tvTempImport.setVisibility(View.GONE);
        }



        switch (mData.get(position).getInt("isMaster")) {
            case 1:
                holder.tvType.setVisibility(View.VISIBLE);
                holder.tvType.setText(Util.getIcon(R.string.icon_home));
//                holder.tvReturn.setVisibility(View.GONE);
                break;

            case 2:
                holder.tvType.setVisibility(View.VISIBLE);
                holder.tvType.setText(Util.getIcon(R.string.icon_depot));
//                if (Util.isAdmin() && mData.get(position).getInt("quantity") > 0) {
//                    holder.tvReturn.setVisibility(View.VISIBLE);
//                    holder.tvReturn.setText(Util.getIcon(R.string.icon_edit_pen));
//                    holder.tvReturn.setRotation(0);
//
//                } else {
//                    holder.tvReturn.setVisibility(View.GONE);
//                }

                break;

            case 3:
                holder.tvType.setVisibility(View.GONE);
//                if (mData.get(position).getInt("quantity") > 0) {
//                    if (Util.isAdmin() || User.getId() == mData.get(position).getInt("user_id")) {
//                        holder.tvReturn.setVisibility(View.VISIBLE);
//                        holder.tvReturn.setText(Util.getIcon(R.string.icon_logout));
//                    } else {
//                        holder.tvReturn.setVisibility(View.GONE);
//                    }
//
//                } else {
//                    holder.tvReturn.setVisibility(View.GONE);
//                }

                break;
        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListenerImport.onResponse(mData.get(position));

            }
        });

//        holder.tvReturn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mData.get(position).getInt("isMaster") == 2) {
//                    mListenerEdit.onResponse(mData.get(position));
//
//                } else {
//                    mListenerReturn.onResponse(mData.get(position));
//                }
//
//            }
//        });

        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDropdow.createListDropdown(holder.tvMenu, listMenu(position), new CallbackString() {
                    @Override
                    public void Result(String s) {
                        if (s.equals(menu[0])){
                            mListenerInfo.onResponse(mData.get(position));

                        }else if (s.equals(menu[1])){
                            mListenerEdit.onResponse(mData.get(position));

                        }else if (s.equals(menu[2])){
                            mListenerReturn.onResponse(mData.get(position));
                        }

                    }
                });

            }
        });

        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class WarehouseAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvType, tvTempImport, tvMenu, tvUsername;
        private LinearLayout lnParent;
        private View line;

        public WarehouseAdapterViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.depot_item_name);
            tvUsername = (TextView) itemView.findViewById(R.id.depot_item_user);
            tvType = (TextView) itemView.findViewById(R.id.depot_item_depottype);
//            tvReturn = (TextView) itemView.findViewById(R.id.depot_item_depotreturn);
            tvMenu = itemView.findViewById(R.id.depot_item_depotmenu);
            lnParent = (LinearLayout) itemView.findViewById(R.id.depot_item_content);
            line = itemView.findViewById(R.id.depot_item_seperateline);
            tvTempImport = itemView.findViewById(R.id.depot_item_number_temp_import);

        }
    }

    public List<BaseModel> getAllTempWarehouse() {
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model : mData) {
            if (model.getInt("isMaster") == 2) {
                results.add(model);
            }
        }

        return results;
    }

    private List<String> listMenu(int pos){
        List<String> list = new ArrayList<>();
        list.add(menu[0]);
        if (mData.get(pos).getInt("isMaster") == 2 || mData.get(pos).getInt("quantity") > 0){
            list.add(menu[1]);

        }

        if (mData.get(pos).getInt("isMaster") == 3 &&  mData.get(pos).getInt("quantity") > 0){
            list.add(menu[2]);

        }

        return  list;
    }

}
