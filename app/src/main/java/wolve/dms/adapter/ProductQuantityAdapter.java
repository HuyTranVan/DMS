package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductQuantityAdapter extends RecyclerView.Adapter<ProductQuantityAdapter.CustomerBillsDetailAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private Context mContext;

    public ProductQuantityAdapter(List<BaseModel> data) {
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();

    }

    @Override
    public CustomerBillsDetailAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_product_item, parent, false);
        return new CustomerBillsDetailAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerBillsDetailAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("productName"));
        holder.tvQuantity.setText(mData.get(position).getString("quantity"));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerBillsDetailAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity, tvTotal;
        private LinearLayout lnParent;

        public CustomerBillsDetailAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.customer_billdetail_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.customer_billdetail_item_quantity);
            lnParent = (LinearLayout) itemView.findViewById(R.id.customer_billdetail_item_parent);

        }

    }



}
