package wolve.dms.adapter;

import static wolve.dms.activities.BaseActivity.createGetParam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class MapPinListAdapter extends RecyclerView.Adapter<MapPinListAdapter.CustomerPinViewHolder> implements Filterable {
    private List<BaseModel> mData = new ArrayList<>();
    private List<BaseModel> mBaseData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackInt mCountListener, mDelete;
    private CallbackObject mItemListener;
    private int count;

    public MapPinListAdapter(List<BaseModel> data, String username, CallbackInt listener, CallbackInt deletelistener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mCountListener = listener;
        this.mDelete = deletelistener;
        this.mBaseData = data;
        this.count = 0;

        if (username.equals(Constants.ALL_FILTER)){
            mData = mBaseData;
        }else {
            for (BaseModel item: mBaseData){
                if (item.getBaseModel("user").getString("displayName").equals(username))
                    mData.add(item);
            }

        }


        DataUtil.sortbyLongKey("pinCreateAt", mData, true);

    }

    @Override
    public CustomerPinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_pin_list_item, parent, false);
        return new CustomerPinViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerPinViewHolder holder, final int position) {
//        holder.vLine.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        holder.tvsignBoard.setText(Constants.shopNameShortened[mData.get(position).getInt("shopType")] + " " + mData.get(position).getString("signBoard"));
        holder.tvAddress.setText(String.format("%s %s, %s",
                mData.get(position).getString("address"),
                mData.get(position).getString("street"),
                mData.get(position).getString("district")));
        String distance = mData.get(position).getDouble("distance") > 1000 ?
                Math.round(mData.get(position).getDouble("distance") / 1000) + " km" :
                Math.round(mData.get(position).getDouble("distance")) + " m";

        holder.tvDistance.setText(Util.getIconString(R.string.icon_street, " ", distance));
        holder.tvTime.setText(Util.countDay(mData.get(position).getBaseModel("pin").getLong("createAt")) +" ngày");
        holder.tvUser.setText(Util.getIconString(R.string.icon_username,
                                            " ",
                                            mData.get(holder.getAdapterPosition()).getBaseModel("user").getString("displayName")));
        if (mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked") ){
            holder.tvNumber.setText(Util.getIcon(R.string.icon_circle_check));
            holder.lnParent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLightGrey));

        }else {
            holder.tvNumber.setText(Util.getIcon(R.string.icon_circle_border));
            holder.lnParent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        }

        switch (mData.get(position).getInt("status_id")){
            case 0:
                holder.tvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.colorPink));

                break;

            case 1:
                holder.tvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.orange));

                break;

            case 3:
                holder.tvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlue));

                break;

            default:
                holder.tvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.black_text_color));

        }


        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked") ){
                    mData.get(position).put("checked",false );
                    count -= 1;

                }else {
                    mData.get(position).put("checked",true);
                    count += 1;
                }

                notifyItemChanged(position);
                mCountListener.onResponse(count);

            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseModel param = createGetParam(ApiUtil.CUSTOMER_PIN_DELETE() + mData.get(holder.getAdapterPosition()).getBaseModel("pin").getString("id"), false);
                new GetPostMethod(param, new NewCallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result, List<BaseModel> list) {
                        Util.getInstance().stopLoading(true);
                        if (result.getBoolean("deleted")){
                            mData.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            mDelete.onResponse(result.getInt("count"));


                        }


                    }

                    @Override
                    public void onError(String error) {

                    }
                }, 1).execute();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerPinViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvAddress, tvDistance, tvTime, tvDelete, tvUser;
        private View vLine;
        private LinearLayout lnParent;
        private RelativeLayout lnNumberCover;

        public CustomerPinViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.pin_list_item_number);
            tvsignBoard = itemView.findViewById(R.id.pin_list_item_signboard);
            tvAddress = itemView.findViewById(R.id.pin_list_item_address);
            tvTime = itemView.findViewById(R.id.pin_list_item_time);
            tvDistance = itemView.findViewById(R.id.pin_list_item_distance);
            tvDelete = itemView.findViewById(R.id.pin_list_item_delete);
            vLine = itemView.findViewById(R.id.pin_list_item_line);
            tvUser = itemView.findViewById(R.id.pin_list_item_user);
            lnParent= itemView.findViewById(R.id.pin_list_item_parent);

        }

    }


    public void CheckAll(){
        for (BaseModel item: mData){
            item.put("checked", true);

        }
        count = mData.size();
        mCountListener.onResponse(count);
        notifyDataSetChanged();
    }

    public void unCheckAll(){
        for (BaseModel item: mData){
            item.put("checked", false);

        }
        count = 0;
        mCountListener.onResponse(count);
        notifyDataSetChanged();
    }

    public List<BaseModel> getAllChecked(){
        List<BaseModel> list = new ArrayList<>();
        for (BaseModel item: mData){
            if (item.hasKey("checked") && item.getBoolean("checked")){
                list.add(item);
            }
        }

        return list;
    }

    public void sort(int i){
//        mData = new ArrayList<>();
//        for (BaseModel item: mBaseData){
//            mData.add(item);
//        }
        switch (i){
            case 0:
                DataUtil.sortbyLongKey("distance", mData, false);
                break;
            case 1:
                DataUtil.sortbyLongKey("distance", mData, true);
                break;

            case 2:
                DataUtil.sortbyDoubleKey("pinCreateAt", mData, true);
                break;

            case 3:
                DataUtil.sortbyDoubleKey("pinCreateAt", mData, false);
                break;

        }

        notifyDataSetChanged();

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charSequence.toString().equals(Constants.ALL_FILTER)) {
                    mData = mBaseData;

                } else {
                    List<BaseModel> listTemp = new ArrayList<>();
                    for (BaseModel row : mBaseData) {
                        if (row.getBaseModel("user").getString("displayName").equals(charSequence.toString())) {
                            listTemp.add(row);
                        }
                    }

                    mData = listTemp;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (ArrayList<BaseModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
