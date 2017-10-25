package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class StatisticalProductAdapter extends RecyclerView.Adapter<StatisticalProductAdapter.StatisticalProductGroupViewHolder> {
    private List<Product> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public StatisticalProductAdapter(List<Product> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();

    }

    @Override
    public StatisticalProductGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_product_item, parent, false);
        return new StatisticalProductGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalProductGroupViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvQuantity.setText(String.valueOf(mData.get(position).getInt("sumquantity")));

        holder.line.setVisibility(position == mData.size() -1 ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalProductGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity;
        private View line;
        private RecyclerView rvGroup;
        private RelativeLayout lnParent;

        public StatisticalProductGroupViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.statistical_product_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.statistical_product_item_quantity);
            rvGroup = (RecyclerView) itemView.findViewById(R.id.statistical_productgroup_item_rvproduct);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.statistical_product_item_parent);
            line = (View) itemView.findViewById(R.id.statistical_product_item_line);

        }

    }



}
