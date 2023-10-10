package wolve.dms.adapter;

import static wolve.dms.utils.Constants.warehouseOptions;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class MapMenuAdapter extends RecyclerView.Adapter<MapMenuAdapter.ItemAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackInt mListener;
    private int parentWidth =0;
    private boolean loading = true;


    public MapMenuAdapter(CallbackInt listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        createItemList();
        this.mListener = listener;
    }

    public void updateLoading(boolean load){
        loading = load;
    }



    public void updateAllItems(){
        createItemList();
        notifyDataSetChanged();
    }

    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_map_menu_item, parent, false);
        parentWidth = parent.getWidth();
        return new ItemAdapterViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ItemAdapterViewHolder holder, int position) {
        holder.lnParent.getLayoutParams().width = parentWidth/ mData.size();
        holder.tvIcon.setText(mData.get(holder.getAdapterPosition()).getString("icon"));
        holder.tvTitle.setText(mData.get(holder.getAdapterPosition()).getString("text"));
        if (mData.get(holder.getAdapterPosition()).getBoolean("check")){
            holder.tvIcon.setTextColor(mContext.getColor(R.color.colorBlue));
            holder.tvTitle.setTextColor(mContext.getColor(R.color.colorBlue));

        }else {
            holder.tvIcon.setTextColor(mContext.getColor(R.color.black_text_color));
            holder.tvTitle.setTextColor(mContext.getColor(R.color.black_text_color));

        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loading){
                    holder.tvIcon.setTextColor(mContext.getColor(R.color.colorBlue));
                    holder.tvTitle.setTextColor(mContext.getColor(R.color.colorBlue));
                    mData.get(holder.getAdapterPosition()).put("check", true);

                    updateUnCheck(holder.getAdapterPosition());
                    mListener.onResponse(holder.getAdapterPosition());

                }




            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ItemAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvIcon;
        private LinearLayout lnParent;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);
            tvIcon = (TextView) itemView.findViewById(R.id.item_mapmenu_icon);
            tvTitle = (TextView) itemView.findViewById(R.id.item_mapmenu_text);
            lnParent = (LinearLayout) itemView.findViewById(R.id.item_mapmenu_parent);

        }

    }


    private void updateUnCheck(int position){
        for (int i=0; i<mData.size(); i++){
            if (i != position){
                mData.get(i).put("check", false);
            }

            notifyItemChanged(i);

        }

    }

    public void updateCheck(int position){
//        holder.tvIcon.setTextColor(mContext.getColor(R.color.colorBlue));
//        holder.tvTitle.setTextColor(mContext.getColor(R.color.colorBlue));
        mData.get(position).put("check", true);
        mListener.onResponse(position);
        notifyItemChanged(position);

        updateUnCheck(position);
//        mListener.onResponse(holder.getAdapterPosition());

    }

    private void createItemList(){
        mData = new ArrayList<>();
        mData.add(0, itemAll());
        mData.add(1, itemInterest());
        mData.add(2, itemOrder());
        mData.add(3, itemCare());


    }

    public void updateTextNumber(BaseModel count){
        mData.get(0).put("text", count.getInt(Constants.MARKER_ALL) > 0 ? String.format("Tất cả %d", count.getInt(Constants.MARKER_ALL)) : Constants.ALL);
        mData.get(1).put("text", count.getInt(Constants.MARKER_INTERESTED) > 0 ? String.format("Quan tâm %d", count.getInt(Constants.MARKER_INTERESTED)) : Constants.INTEREST);
        mData.get(2).put("text", count.getInt(Constants.MARKER_ORDERED) > 0 ? String.format("Đã mua %d", count.getInt(Constants.MARKER_ORDERED)) : Constants.ORDERED);
        notifyDataSetChanged();

    }

    public void updateCareNumber(int count){
        mData.get(3).put("text", count > 0 ? String.format("Chăm sóc %d", count) : Constants.CUSTOMER_CARE);
        notifyItemChanged(3);

    }

    public String getChecked(){
        String filter =null;
        for (int i=0; i<mData.size(); i++){
            if (mData.get(i).getBoolean("check")){
                if (i == 0) {
                    filter = Constants.MARKER_ALL;

                } else if (i == 1) {
                    filter = Constants.MARKER_INTERESTED;

                } else if (i == 2){
                    filter = Constants.MARKER_ORDERED;

                }else {
                    filter = Constants.OTHERS;
                }
            }


        }
        return filter;


    }

    private BaseModel itemAll(){
        BaseModel item = new BaseModel();
        item.put("icon", Util.getIcon(R.string.icon_list_check));
        item.put("text", Constants.ALL);
        item.put("check", true);
        return item;

    }
    private BaseModel itemInterest(){
        BaseModel item = new BaseModel();
        item.put("icon", Util.getIcon(R.string.icon_heart));
        item.put("text", Constants.INTEREST);
        item.put("check", false);
        return item;

    }
    private BaseModel itemOrder(){
        BaseModel item = new BaseModel();
        item.put("icon", Util.getIcon(R.string.icon_hand_on_money));
        item.put("text", Constants.ORDERED);
        item.put("check", false);
        return item;

    }
    private BaseModel itemCare(){
        BaseModel item = new BaseModel();
        item.put("icon", Util.getIcon(R.string.icon_district));
        item.put("text", Constants.CUSTOMER_CARE);
        item.put("check", false);
        return item;

    }





}
