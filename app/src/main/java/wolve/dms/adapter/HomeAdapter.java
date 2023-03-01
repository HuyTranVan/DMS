package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.warehouseOptions;


/**
 * Created by tranhuy on 5/24/17.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private WarehouseAdapter.CallbackWarehouseOption mWarehouseOption;
    private int inventoryQuantity;

    public HomeAdapter(CallbackObject callbackClickAdapter, WarehouseAdapter.CallbackWarehouseOption morelistener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        createItemList();
        this.mListener = callbackClickAdapter;
        this.mWarehouseOption = morelistener;
        this.inventoryQuantity = 0;
    }

    private void createItemList(){
        mData = new ArrayList<>();
        mData.add(Constants.HomeSaleItem());
        mData.add(Constants.HomeStatisticalItem());
        if (Distributor.getImportFunction() > 0)
            mData.add(Constants.HomeImportItem());
        mData.add(Constants.HomeCashFlowItem());
        mData.add(Constants.HomeCategoriesItem());
        mData.add(Constants.HomeDistributorItem());

    }

    public void updateAllItems(){
        createItemList();
        notifyDataSetChanged();
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
    public void onBindViewHolder(final ItemAdapterViewHolder holder, int position) {
        if (mData.get(holder.getAdapterPosition()).getBoolean("isDistributor")){
            holder.tvIcon.setVisibility(View.GONE);
            holder.imgItem.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(Distributor.getName());
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            Glide.with(mContext).load(Distributor.getImage()).placeholder(R.drawable.lub_logo_grey).fitCenter().into(holder.imgItem);

        }else {
            holder.tvIcon.setVisibility(View.VISIBLE);
            holder.imgItem.setVisibility(View.GONE);
            holder.tvIcon.setText(mData.get(holder.getAdapterPosition()).getString("icon"));
            holder.tvTitle.setText(mData.get(holder.getAdapterPosition()).getString("text"));
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.black_text_color));

        }

//        holder.tvMore.setVisibility(mData.get(position).getBoolean("haveDetail")
//                && mData.get(position).hasKey("more_text")
//                && inventoryQuantity > 0
//                ? View.VISIBLE : View.GONE);
        if (mData.get(holder.getAdapterPosition()).hasKey("more_text")){
            holder.tvMore.setVisibility(View.VISIBLE);
            holder.tvMore.setText(mData.get(holder.getAdapterPosition()).getString("more_text"));
        }else {
            holder.tvMore.setVisibility(View.GONE);
        }

        if (mData.get(holder.getAdapterPosition()).hasKey("notify_text")){
            holder.tvNotify.setVisibility(View.VISIBLE);
            holder.tvNotify.setText(mData.get(holder.getAdapterPosition()).getString("notify_text"));
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
                    if (holder.getAdapterPosition() == 2){
                        CustomDropdow.createListDropdown(holder.tvMore,
                                WarehouseAdapter.listMenu(false,
                                        User.getCurrentUser().getBaseModel("warehouse"),
                                        inventoryQuantity ),
                                        0,
                                        false,
                                        new CallbackClickAdapter() {
                                            @Override
                                            public void onRespone(String data, int pos) {
                                                if (data.equals(warehouseOptions[0])){
                                                    BaseModel warehouse = User.getCurrentUser().getBaseModel("warehouse");
                                                    warehouse.putBaseModel("user", User.getCurrentUser() );
                                                    mWarehouseOption.onInfo(warehouse);

                                                }else if (data.equals(warehouseOptions[1])){
                                                    mWarehouseOption.onInventory(mData.get(holder.getAdapterPosition()));

                                                }else if (data.equals(warehouseOptions[2])){
                                                    mWarehouseOption.onReturn(mData.get(holder.getAdapterPosition()));
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
                mListener.onResponse(mData.get(holder.getAdapterPosition()));

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

    public void updateImportShorcut(int inventory_no){
        if (Distributor.getImportFunction() > 0 && inventory_no >0){
            for (int i=0; i<mData.size(); i++){
                if (mData.get(i).getString("text").equals(Constants.IMPORT)){
                    mData.get(i).put("more_text",Util.getIcon(R.string.icon_menu));
                    mData.get(i).put("importFunction", Distributor.getImportFunction());
                    notifyItemChanged(i);
                    break;
                }
            }

        }

        inventoryQuantity = inventory_no;
        //notifyItemChanged(i);

    }

    public void updateWaitingListDetail(int size){
        if (size >0){
            for (int i=0; i<mData.size(); i++){
                if (mData.get(i).getString("text").equals(Constants.SALE)){
                    mData.get(i).put("notify_text", size);
                    notifyItemChanged(i);
                    break;
                }

            }

        }

    }


}
