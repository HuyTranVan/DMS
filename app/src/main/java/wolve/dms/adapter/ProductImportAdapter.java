package wolve.dms.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.CounterHandler;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
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
        if (mData.get(position).hasKey("currentQuantity") && mData.get(position).getInt("currentQuantity") >0){
            holder.tvQuantityLimit.setText(mData.get(position).getString("currentQuantity"));
        }else {
            holder.tvQuantityLimit.setText("");
        }
        if (mData.get(position).hasKey("quantity") && mData.get(position).getInt("quantity") >0 ){
            holder.tvMinus.setVisibility(View.VISIBLE);
            holder.edQuantity.setVisibility(View.VISIBLE);
            holder.edQuantity.setText(mData.get(position).getString("quantity"));
            holder.lnParent.setBackgroundResource(R.color.colorLightGrey);

        }else {
            holder.tvMinus.setVisibility(View.GONE);
            holder.edQuantity.setVisibility(View.GONE);
            holder.lnParent.setBackgroundResource(R.color.colorWhite);
        }

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = mData.get(position).hasKey("quantity")? mData.get(position).getInt("quantity") : 0;

                if (!mData.get(position).hasKey("currentQuantity") || mData.get(position).getInt("currentQuantity") > quantity ){
                    mData.get(position).put("quantity", quantity +1);
                    notifyItemChanged(position);
                    mListener.onResponse(mData.get(position));

                }else {
                    Util.showToast("Sản phẩm hết tồn kho");
                }


            }
        });

        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.get(position).put("quantity", mData.get(position).getInt("quantity") -1);
                notifyItemChanged(position);
                mListener.onResponse(mData.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvGroup, tvQuantityLimit;
        private CTextIcon tvPlus, tvMinus;
        private EditText edQuantity;
        private LinearLayout lnParent;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.product_import_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.product_import_item_name);
            tvGroup = (TextView) itemView.findViewById(R.id.product_import_item_group);
            tvMinus =  itemView.findViewById(R.id.product_import_item_minus);
            tvPlus =  itemView.findViewById(R.id.product_import_item_plus);
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
                        if (row.getString("name").toLowerCase().contains(charString)){
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

    public void updateData(BaseModel model){
        for (int i=0; i<mData.size(); i++){
            if (mData.get(i).getInt("id") == model.getInt("id")){
                notifyItemChanged(i);
            }
        }
    }

    public void reloadData(){
        mData = baseData;
        notifyDataSetChanged();
    }

    public void updateData(List<BaseModel> list){
        baseData = list;
        mData = baseData;
        notifyDataSetChanged();

    }

}
