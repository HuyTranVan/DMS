package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Product;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductReturnAdapter extends RecyclerView.Adapter<ProductReturnAdapter.CartProductsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ProductReturnAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;

        for (int i=0; i<mData.size(); i++){
            try {
                mData.get(i).put("quantityReturn", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public CartProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_return_item, parent, false);
        return new CartProductsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartProductsViewHolder holder, final int position) {
            holder.tvName.setText(mData.get(position).getString("productName"));
            holder.tvQuantity.setText(mData.get(position).getString("quantityReturn"));
            holder.btnSub.setVisibility(View.GONE);

            holder.btnSub.setVisibility(mData.get(position).getInt("quantityReturn") >0 ? View.VISIBLE :View.GONE);
            holder.btnPlus.setVisibility(mData.get(position).getInt("quantityReturn") >= mData.get(position).getInt("quantity") ?View.INVISIBLE :View.VISIBLE);

        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int currentQuantity = mData.get(position).getInt("quantityReturn");
                    if ( currentQuantity > 0){
                        mData.get(position).put("quantityReturn", currentQuantity -1);
                        notifyItemChanged(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnPlus.setVisibility(View.INVISIBLE);
                try {
                    int currentQuantity = mData.get(position).getInt("quantityReturn");
                    mData.get(position).put("quantityReturn", currentQuantity +1);
                    notifyItemChanged(position);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CartProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity ;
        private RelativeLayout lnParent;
        private CTextIcon btnSub, btnPlus;

        public CartProductsViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.product_return_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.product_return_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.product_return_item_quantity);
            btnSub = (CTextIcon) itemView.findViewById(R.id.product_return_item_sub);
            btnPlus = (CTextIcon) itemView.findViewById(R.id.product_return_item_plus);

        }

    }

//    public List<BillDetail> getListSelected(){
//        List<BillDetail> listResult = new ArrayList<>();
//        for (int i=0; i<mData.size(); i++){
//            if (mData.get(i).getInt("quantityReturn") >0){
//                listResult.add(mData.get(i));
//            }
//        }
//
//        return listResult;
//    }

    public List<JSONObject> getListSelected(){
        List<JSONObject> listResult = new ArrayList<>();
        for (int i=0; i<mData.size(); i++){
            try {
                if (mData.get(i).getInt("quantityReturn") >0){
                    mData.get(i).put("quantity", mData.get(i).getInt("quantityReturn")*-1);
                    mData.get(i).put("id",mData.get(i).getInt("productId"));
                    listResult.add(mData.get(i).BaseModelJSONObject());
                }
            } catch (JSONException e) {
//                    e.printStackTrace();
            }
        }

        return listResult;
    }



}
