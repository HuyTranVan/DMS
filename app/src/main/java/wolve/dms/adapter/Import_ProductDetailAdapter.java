package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Import_ProductDetailAdapter extends RecyclerView.Adapter<Import_ProductDetailAdapter.Import_ProductDetailViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public Import_ProductDetailAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        //this.mListener = callback;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = data;

    }

    @Override
    public Import_ProductDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_import_product_detail_item, parent, false);
        return new Import_ProductDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Import_ProductDetailViewHolder holder, final int pos) {
        holder.tvName.setText(mData.get(pos).getString("productName"));
        holder.tvQuantity.setText(String.valueOf(mData.get(pos).getInt("quantity")));


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Import_ProductDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQuantity;
        private TextView tvName;
        private RelativeLayout lnParent;

        public Import_ProductDetailViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.import_product_detail_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.import_product_detail_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.import_product_detail_item_quantity);

        }

    }

    private int getSumProduct() {
        int sum = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getBoolean("isChecked")) {
                sum += mData.get(i).getInt("sumquantity");
            }
        }
        return sum;
    }


}
