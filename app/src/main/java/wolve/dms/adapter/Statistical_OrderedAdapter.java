package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistical_OrderedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private TextView tvSum;
    private CallbackString mListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public Statistical_OrderedAdapter(String user, List<BaseModel> data, CallbackString listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mData = data;

        DataUtil.sortbyDoubleKey("debt", mData, true);

    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void addItem(BaseModel model) {
        mData.add(model);
        notifyItemInserted(mData.size() - 1);
    }

    public void removeItem(int pos) {
        mData.remove(pos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_statistical_ordered_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            setItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {

    }

    private void setItemRows(ItemViewHolder holder, int position) {
        holder.tvNumber.setText(String.valueOf(position + 1));
        holder.tvsignBoard.setText(Constants.shopName[mData.get(position).getInt("shopType")] + " " + mData.get(position).getString("signBoard"));
        holder.tvDistrict.setText(mData.get(position).getString("street") + " - " + mData.get(position).getString("district"));
        holder.tvUser.setText(Util.getIconString(R.string.icon_username, "  ", mData.get(position).getBaseModel("user").getString("displayName")));
        holder.tvDebt.setVisibility(mData.get(position).getDouble("debt") > 0.0 ? View.VISIBLE : View.GONE);
        holder.tvDebt.setText(Util.FormatMoney(mData.get(position).getDouble("debt")));
        holder.tvTime.setText(String.format("Mua %d ngày trước", Util.countDay(mData.get(position).getLong("last_bill"))));

        holder.vLine.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);

        holder.tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.Result(mData.get(position).getString("id"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvDistrict, tvUser, tvTime, tvDebt;
        private View vLine;
        private LinearLayout lnParent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.statistical_ordered_item_number);
            tvsignBoard = itemView.findViewById(R.id.statistical_ordered_item_signboard);
            tvDistrict = itemView.findViewById(R.id.statistical_ordered_item_district);
            tvUser = itemView.findViewById(R.id.statistical_ordered_item_user);
            tvTime = itemView.findViewById(R.id.statistical_ordered_item_last_debt);
            tvDebt = itemView.findViewById(R.id.statistical_ordered_item_debt);
            vLine = itemView.findViewById(R.id.statistical_ordered_item_line);


        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }


}
