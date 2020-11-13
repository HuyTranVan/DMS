package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductImportAdapter extends RecyclerView.Adapter<ProductImportAdapter.ProductAdapterViewHolder> implements Filterable {
    private List<BaseModel> mData = new ArrayList<>();
    private List<BaseModel> baseData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;

    public ProductImportAdapter(List<BaseModel> list, CallbackObject listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.baseData = list;
        this.mData = baseData;
        this.mListener = listener;

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
        if (mData.get(position).hasKey("currentQuantity") && mData.get(position).getInt("currentQuantity") > 0) {
            holder.tvQuantityLimit.setText(mData.get(position).getString("currentQuantity"));
        } else {
            holder.tvQuantityLimit.setText("");
        }
        if (mData.get(position).hasKey("quantity") && mData.get(position).getInt("quantity") > 0) {
            holder.tvMinus.setVisibility(View.VISIBLE);
            holder.tvPlus.setVisibility(View.GONE);
            holder.edQuantity.setVisibility(View.VISIBLE);
            holder.edQuantity.setText(mData.get(position).getString("quantity"));
            holder.lnParent.setBackgroundResource(R.color.colorLightGrey);

        } else {
            holder.tvMinus.setVisibility(View.GONE);
            holder.tvPlus.setVisibility(View.VISIBLE);
            holder.edQuantity.setVisibility(View.GONE);
            holder.lnParent.setBackgroundResource(R.color.colorWhite);
        }

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tvPlus.setVisibility(View.GONE);
                int quantity = mData.get(position).hasKey("quantity") ? mData.get(position).getInt("quantity") : 0;
                mData.get(position).put("quantity", quantity + 1);
                notifyItemChanged(position);
                mListener.onResponse(mData.get(position));

//                if (!mData.get(position).hasKey("currentQuantity") || mData.get(position).getInt("currentQuantity") > quantity) {
//                    mData.get(position).put("quantity", quantity + 1);
//                    notifyItemChanged(position);
//                    mListener.onResponse(mData.get(position));
//
//                } else {
//                    Util.showToast("Sản phẩm hết tồn kho");
//                }


            }
        });

        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.get(position).put("quantity", mData.get(position).getInt("quantity") - 1);
                notifyItemChanged(position);
                mListener.onResponse(mData.get(position));
            }
        });

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = mData.get(position).hasKey("quantity") ? mData.get(position).getInt("quantity") : 0;

                if (!mData.get(position).hasKey("currentQuantity") || mData.get(position).getInt("currentQuantity") > quantity) {
                    mData.get(position).put("quantity", quantity + 1);
                    notifyItemChanged(position);
                    mListener.onResponse(mData.get(position));

                } else {
                    Util.showToast("Sản phẩm hết tồn kho");
                }


            }
        });





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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mData = baseData;

                } else {
                    List<BaseModel> listTemp = new ArrayList<>();
                    for (BaseModel row : baseData) {
                        if (row.getString("name").toLowerCase().contains(charString)) {
                            listTemp.add(row);
                        }
                    }

                    mData = listTemp;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (ArrayList<BaseModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateData(BaseModel model) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getInt("id") == model.getInt("id")) {
                notifyItemChanged(i);
            }
        }
    }

    public void reloadData() {
        mData = baseData;
        notifyDataSetChanged();
    }

    public void updateData(List<BaseModel> list) {
        baseData = DataUtil.getProductPopular(list);
        mData = baseData;
        notifyDataSetChanged();

    }

    public List<BaseModel> getBaseData(){
        return baseData;
    }

}
