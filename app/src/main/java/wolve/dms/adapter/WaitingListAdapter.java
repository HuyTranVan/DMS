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
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.StatisticalBillsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackInt mCountListener;
    private CallbackObject mItemListener;
    private int count;

    public WaitingListAdapter(List<BaseModel> data, CallbackInt listener, CallbackObject objectlistener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mCountListener = listener;
        this.mData = data;
        this.count = 0;
        this.mItemListener = objectlistener;
        //DataUtil.sortbyDoubleKey("distance", mData, false);

    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_waiting_list_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        holder.vLine.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        holder.tvsignBoard.setText(Constants.shopName[mData.get(position).getInt("shopType")] + " " + mData.get(position).getString("signBoard"));
        holder.tvAddress.setText(String.format("%s %s, %s",
                mData.get(position).getString("address"),
                mData.get(position).getString("street"),
                mData.get(position).getString("district")));
        String distance = mData.get(position).getDouble("distance") > 1000 ?
                Math.round(mData.get(position).getDouble("distance") / 1000) + " km" :
                Math.round(mData.get(position).getDouble("distance")) + " m";

        holder.tvDistance.setText(distance);
        holder.tvTime.setText(String.format("Check-in %d ngày",
                Util.countDay(mData.get(position).getBaseModel("waiting").getLong("updateAt"))
                ));

        switch (mData.get(position).getInt("status_id")){
            case 0:
                holder.tvNumber.setBackground(
                        mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_pink):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_pink));

                holder.tvIcon.setTextColor(mContext.getResources().getColor(R.color.colorPink));

                break;

            case 1:
                holder.tvNumber.setBackground(
                        mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_orange):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_orange));
                holder.tvIcon.setTextColor(mContext.getResources().getColor(R.color.orange));

                break;

            case 3:
                holder.tvNumber.setBackground(
                        mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_blue):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_blue));
                holder.tvIcon.setTextColor(mContext.getResources().getColor(R.color.colorBlue));

                break;

            default:
                holder.tvNumber.setBackground(
                        mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_grey):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_grey));
                holder.tvIcon.setTextColor(mContext.getResources().getColor(R.color.colorGrey));

        }

        holder.lnNumberCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked") ){
                    mData.get(position).put("checked",false );
                    count -= 1;

                }else {
                    mData.get(position).put("checked",true);
                    count += 1;
                }


                switch (mData.get(position).getInt("status_id")){
                    case 0:
                        holder.tvNumber.setBackground(
                                mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_pink):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_pink));


                        break;

                    case 1:
                        holder.tvNumber.setBackground(
                                mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_orange):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_orange));

                        break;

                    case 3:
                        holder.tvNumber.setBackground(
                                mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_blue):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_blue));

                        break;

                    default:
                        holder.tvNumber.setBackground(
                                mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")?
                                mContext.getResources().getDrawable(R.drawable.btn_round_grey):
                                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_grey));

                }

                mCountListener.onResponse(count);
            }
        });

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onResponse(mData.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvAddress, tvDistance, tvTime, tvIcon;
        private View vLine;
        private LinearLayout lnParent;
        private RelativeLayout lnNumberCover;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.waiting_list_item_number);
            tvsignBoard = itemView.findViewById(R.id.waiting_list_item_signboard);
            tvAddress = itemView.findViewById(R.id.waiting_list_item_address);
            tvTime = itemView.findViewById(R.id.waiting_list_item_time);
            tvDistance = itemView.findViewById(R.id.waiting_list_item_distance);
            tvIcon = itemView.findViewById(R.id.waiting_list_item_icon);
            vLine = itemView.findViewById(R.id.waiting_list_item_line);
            lnNumberCover = itemView.findViewById(R.id.waiting_list_item_number_cover);
            lnParent= itemView.findViewById(R.id.waiting_list_item_parent);

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

}
