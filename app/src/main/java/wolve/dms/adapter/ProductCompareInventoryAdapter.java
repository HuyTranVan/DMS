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

public class ProductCompareInventoryAdapter extends RecyclerView.Adapter<ProductCompareInventoryAdapter.ProductCompareInventoryViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private Context mContext;

    public ProductCompareInventoryAdapter(List<BaseModel> data) {
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();

        BaseModel title = new BaseModel();
        title.put("productName", "Tên sản phẩm");
        title.put("productQuantity", "SL");
        title.put("inventoryQuantity", "Tồn kho");
        title.put("differenceQuantity", "##");
        mData.add(0, title);

    }

    @Override
    public ProductCompareInventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_compare_product_item, parent, false);
        return new ProductCompareInventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductCompareInventoryViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("productName"));
        holder.tvQuantity1.setText(mData.get(position).getString("productQuantity"));
        holder.tvQuantity2.setText(mData.get(position).getString("inventoryQuantity"));
        holder.tvQuantity3.setText(mData.get(position).getString("differenceQuantity"));

        holder.tvQuantity3.setTextColor(mData.get(position).getInt("differenceQuantity") >= 0 ?
                mContext.getResources().getColor(R.color.black_text_color) :
                mContext.getResources().getColor(R.color.orange_dark));

        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ProductCompareInventoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity1, tvQuantity2, tvQuantity3;
        private LinearLayout lnParent;
        private View line;

        public ProductCompareInventoryViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.compare_product_item_name);
            tvQuantity1 = (TextView) itemView.findViewById(R.id.compare_product_item_num1);
            tvQuantity2 = (TextView) itemView.findViewById(R.id.compare_product_item_num2);
            tvQuantity3 = (TextView) itemView.findViewById(R.id.compare_product_item_num3);
            line = itemView.findViewById(R.id.line);
//            lnParent = (LinearLayout) itemView.findViewById(R.id.customer_billdetail_item_parent);

        }

    }


}
