package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductInventoryAdapter extends RecyclerView.Adapter<ProductInventoryAdapter.ProductAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private List<BaseModel> baseData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;

    public ProductInventoryAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.baseData = list;
        this.mData = baseData;


    }

    @Override
    public ProductAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_import_item, parent, false);
        return new ProductAdapterViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ProductAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvGroup.setText(mData.get(position).getBaseModel("productGroup").getString("name"));
        holder.edQuantity.setText(mData.get(position).getString("currentQuantity"));

        holder.edQuantity.setVisibility(View.VISIBLE);
        holder.tvPlus.setVisibility(View.GONE);
        holder.tvMinus.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvGroup, tvQuantityLimit, tvPlus, tvMinus;
        private EditText edQuantity;
        private LinearLayout lnParent;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.product_import_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.product_import_item_name);
            tvGroup = (TextView) itemView.findViewById(R.id.product_import_item_group);
            tvMinus = itemView.findViewById(R.id.product_import_item_minus);
            tvPlus = itemView.findViewById(R.id.product_import_item_plus);
            edQuantity = itemView.findViewById(R.id.product_import_item_number);
            tvQuantityLimit = itemView.findViewById(R.id.product_import_item_currentquantity);

        }

    }


    public void updateData(BaseModel model) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getInt("id") == model.getInt("id")) {
                notifyItemChanged(i);
            }
        }
    }

    public void updateData(List<BaseModel> list) {
        mData = list;
        notifyDataSetChanged();
    }

}
