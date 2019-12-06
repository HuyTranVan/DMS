package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.Product;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartPromotionsAdapter extends RecyclerView.Adapter<CartPromotionsAdapter.ProductDialogShopCartAdapterViewHolder> {
    private List<Product> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackDeleteAdapter mDelete;

    public CartPromotionsAdapter(List<Product> list, CallbackDeleteAdapter callbackDeleteAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mDelete = callbackDeleteAdapter;
        this.mData = list;

//        try {
//            for (int i=0; i<list.size(); i++){
//                list.get(i).put("totalMoney",0);
//                list.get(i).put("discount", list.get(i).getDouble("unitPrice"));
//                this.mData.add(list.get(i));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }


    @Override
    public ProductDialogShopCartAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cart_promotions_item, parent, false);
        return new ProductDialogShopCartAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductDialogShopCartAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name") +" ("+Util.FormatMoney(mData.get(position).getDouble("unitPrice")) +")" );
        //holder.tvQuantity.setText(mData.get(position).getInt("quantity").toString());

        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
                    int currentQuantity = mData.get(position).getInt("quantity");
                    if ( currentQuantity > 1){
                        mData.get(position).put("quantity", currentQuantity -1);
                        //mData.get(position).put("totalMoney", (currentQuantity -1)* mData.get(position).getDouble("unitPrice"));
                        notifyItemChanged(position);
                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
                    int currentQuantity = mData.get(position).getInt("quantity");
                    mData.get(position).put("quantity", currentQuantity +1);
                    //mData.get(position).put("totalMoney", (currentQuantity +1)* mData.get(position).getDouble("unitPrice"));
                    notifyItemChanged(position);

//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });

        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CustomCenterDialog.alertWithCancelButton(null, "Xóa " + mData.get(position).getString("name") +" khỏi danh sách" , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        mDelete.onDelete(mData.get(position).ProducttoString(), position);
                        mData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());

                    }
                });

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ProductDialogShopCartAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity ;
        private RelativeLayout lnParent;
        private CircleImageView imgProduct;
        private CTextIcon btnSub, btnPlus;

        public ProductDialogShopCartAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.shopcart_promotions_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.shopcart_promotions_item_name);
            imgProduct = (CircleImageView) itemView.findViewById(R.id.shopcart_promotions_item_image);
            tvQuantity = (TextView) itemView.findViewById(R.id.shopcart_promotions_item_quantity);
            btnSub = (CTextIcon) itemView.findViewById(R.id.shopcart_promotions_item_sub);
            btnPlus = (CTextIcon) itemView.findViewById(R.id.shopcart_promotions_item_plus);

        }

    }

    public void addItemPromotion(Product product){
        mData.add(product);
        notifyDataSetChanged();

    }
    public List<Product> getAllDataPromotion(){
        return mData;
    }


}
