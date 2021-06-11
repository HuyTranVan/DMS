package wolve.dms.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.warehouseOptions;


/**
 * Created by tranhuy on 5/24/17.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private WarehouseAdapter.CallbackWarehouseOption mWarehouseOption;
    private int inventoryQuantity;

    public HomeAdapter(CallbackClickAdapter callbackClickAdapter, WarehouseAdapter.CallbackWarehouseOption morelistener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = Constants.HomeItemList();
        this.mListener = callbackClickAdapter;
        this.mWarehouseOption = morelistener;
        this.inventoryQuantity = 0;
    }

    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_home_item, parent, false);
        //itemView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return new ItemAdapterViewHolder(itemView);
    }

    public void addItems(List<BaseModel> list) {
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ItemAdapterViewHolder holder, final int position) {
        if (mData.get(position).getBoolean("isDistributor")){
            holder.tvIcon.setVisibility(View.GONE);
            holder.imgItem.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(Distributor.getName());
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            Glide.with(mContext).load(Distributor.getImage()).placeholder(R.drawable.lub_logo_grey).fitCenter().into(holder.imgItem);

        }else {
            holder.tvIcon.setVisibility(View.VISIBLE);
            holder.imgItem.setVisibility(View.GONE);
            holder.tvIcon.setText(mData.get(position).getString("icon"));
            holder.tvTitle.setText(mData.get(position).getString("text"));
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.black_text_color));

        }

//        holder.tvMore.setVisibility(mData.get(position).getBoolean("haveDetail")
//                && mData.get(position).hasKey("more_text")
//                && inventoryQuantity > 0
//                ? View.VISIBLE : View.GONE);
        if (mData.get(position).hasKey("more_text")){
            holder.tvMore.setVisibility(View.VISIBLE);
            holder.tvMore.setText(mData.get(position).getString("more_text"));
        }else {
            holder.tvMore.setVisibility(View.GONE);
        }

        if (mData.get(position).hasKey("notify_text")){
            holder.tvNotify.setVisibility(View.VISIBLE);
            holder.tvNotify.setText(mData.get(position).getString("notify_text"));
        }else {
            holder.tvNotify.setVisibility(View.GONE);
        }

//        holder.tvMore.setBackground(mData.get(position).hasKey("none_background")?
//                mContext.getResources().getDrawable(R.drawable.btn_round_transparent_border_purple):
//                mContext.getResources().getDrawable(R.drawable.bg_round_transparent_border_grey));

        holder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (User.checkUserWarehouse()){
                    if (position == 2){
                        CustomDropdow.createListDropdown(holder.tvMore,
                                WarehouseAdapter.listMenu(false,
                                        User.getCurrentUser().getBaseModel("warehouse"),
                                        inventoryQuantity ),
                                        new CallbackString() {
                            @Override
                            public void Result(String s) {
                                if (s.equals(warehouseOptions[0])){
                                    BaseModel warehouse = User.getCurrentUser().getBaseModel("warehouse");
                                    warehouse.putBaseModel("user", User.getCurrentUser() );
                                    mWarehouseOption.onInfo(warehouse);

                                }else if (s.equals(warehouseOptions[1])){
                                    mWarehouseOption.onInventory(mData.get(position));

                                }else if (s.equals(warehouseOptions[2])){
                                    mWarehouseOption.onReturn(mData.get(position));
                                }

                            }
                        });

                    }

                }


            }
        });

        holder.rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRespone(holder.tvTitle.getText().toString(), position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ItemAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvIcon, tvMore, tvNotify;
        private RelativeLayout rlParent;
        private CircleImageView imgItem;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);
            tvIcon = (TextView) itemView.findViewById(R.id.item_home_icon);
            tvTitle = (TextView) itemView.findViewById(R.id.item_home_text);
            tvMore = (TextView) itemView.findViewById(R.id.item_home_more);
            tvNotify = (TextView) itemView.findViewById(R.id.item_home_notify);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.item_home_parent);
            imgItem = itemView.findViewById(R.id.item_home_image);

        }

    }

    public void reloadItem(){
        notifyDataSetChanged();
    }

    public void updateInventoryDetail(int inventory_no){
        if (inventory_no >0){
            mData.get(2).put("more_text",Util.getIcon(R.string.icon_menu));
        }

        inventoryQuantity = inventory_no;
        notifyItemChanged(2);

    }

    public void updateWaitingListDetail(int size){
        if (size >0){
            mData.get(0).put("notify_text", size);
        }
        //inventoryQuantity = inventory_no;
        notifyItemChanged(0);

    }


}
