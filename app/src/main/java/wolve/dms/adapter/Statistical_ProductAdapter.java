package wolve.dms.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistical_ProductAdapter extends RecyclerView.Adapter<Statistical_ProductAdapter.StatisticalProductGroupViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public Statistical_ProductAdapter(List<BaseModel> data, CallbackString callback) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mListener = callback;
        this.mContext = Util.getInstance().getCurrentActivity();

        for (BaseModel model : data) {
            model.put("isChecked", false);
        }
        this.mData = data;

    }

    @Override
    public StatisticalProductGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_product_item, parent, false);
        return new StatisticalProductGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalProductGroupViewHolder holder, final int pos) {
        String averagePrice = String.format("(~%s)", Util.FormatMoney((double) Math.round(mData.get(pos).getDouble("total") / mData.get(pos).getDouble("quantity"))));
        holder.tvName.setText(mData.get(pos).getString("productName"));
        holder.tvAveragePrice.setText(averagePrice);
        holder.tvQuantity.setText(String.valueOf(mData.get(pos).getInt("quantity")));

        if (mData.get(pos).getBoolean("isChecked")) {
            holder.cbCheck.setVisibility(View.VISIBLE);
            holder.tvName.setTypeface(null, Typeface.BOLD);
            holder.tvQuantity.setTypeface(null, Typeface.BOLD);

        } else {
            holder.cbCheck.setVisibility(View.GONE);
            holder.tvName.setTypeface(null, Typeface.NORMAL);
            holder.tvQuantity.setTypeface(null, Typeface.NORMAL);

        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = mData.get(pos).getBoolean("isChecked");
                mData.get(pos).put("isChecked", check ? false : true);
                mListener.Result(String.valueOf(getSumProduct()));
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalProductGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQuantity, tvName, tvAveragePrice;
        private CheckBox cbCheck;
        private View line;
        private RecyclerView rvGroup;
        private RelativeLayout lnParent;

        public StatisticalProductGroupViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.statistical_product_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.statistical_product_item_quantity);
            rvGroup = (RecyclerView) itemView.findViewById(R.id.statistical_productgroup_item_rvproduct);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.statistical_product_item_parent);
            tvAveragePrice = itemView.findViewById(R.id.statistical_product_item_price);
            line = (View) itemView.findViewById(R.id.statistical_product_item_line);
            cbCheck = itemView.findViewById(R.id.statistical_product_item_check);

        }

    }

    private int getSumProduct() {
        int sum = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getBoolean("isChecked")) {
                sum += mData.get(i).getInt("quantity");
            }
        }
        return sum;
    }


}
