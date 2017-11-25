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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.controls.CTextIcon;
import wolve.dms.models.Product;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.ProductDialogShopCartAdapterViewHolder> {
    private List<Product> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackChangePrice mChangePrice;

    public CartProductsAdapter(List<Product> list, CallbackChangePrice callbackChangePrice) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mChangePrice = callbackChangePrice;
        this.mData = list;

    }


    @Override
    public ProductDialogShopCartAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cart_products_item, parent, false);
        return new ProductDialogShopCartAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductDialogShopCartAdapterViewHolder holder, final int position) {
        holder.tvName.setText(String.format("%s (%s)",mData.get(position).getString("name"),Util.FormatMoney(mData.get(position).getDouble("unitPrice")) ));
        holder.tvUnitPrice.setText(Util.FormatMoney(mData.get(position).getDouble("totalMoney")));
        holder.tvDiscount.setText(Util.FormatMoney(mData.get(position).getDouble("discount")));
        holder.tvQuantity.setText(mData.get(position).getInt("quantity").toString());

        if (mData.get(position).getString("image") != null && !mData.get(position).getString("image").equals("null") && !mData.get(position).getString("image").equals("http://lubsolution.com/mydms/staticnull")){
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imgProduct);

        }else {
            Glide.with(mContext).load( R.drawable.ic_wolver).centerCrop().into(holder.imgProduct);

        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCenterDialog.showDialogEditProduct(mData.get(position), new CallbackClickProduct() {
                    @Override
                    public void ProductChoice(Product product) {
                        //mData.remove(position);
                        try {
                            mData.get(position).put("quantity", product.getInt("quantity"));
                            mData.get(position).put("discount", product.getDouble("discount"));
                            mData.get(position).put("totalMoney", product.getDouble("totalMoney"));

//                            mData.add(position, product);
                            mChangePrice.NewPrice(updatePrice(mData));
                            notifyItemChanged(position);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int currentQuantity = mData.get(position).getInt("quantity");
                    if ( currentQuantity > 1){
                        mData.get(position).put("quantity", currentQuantity -1);
                        mData.get(position).put("totalMoney", (currentQuantity -1)* mData.get(position).getDouble("unitPrice"));
                        mChangePrice.NewPrice(updatePrice(mData));
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
                try {
                    int currentQuantity = mData.get(position).getInt("quantity");
                    mData.get(position).put("quantity", currentQuantity +1);
                    mData.get(position).put("totalMoney", (currentQuantity +1)* mData.get(position).getDouble("unitPrice"));
                    mChangePrice.NewPrice(updatePrice(mData));
                    notifyItemChanged(position);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CustomCenterDialog.alertWithCancelButton(null, "Xóa " + mData.get(position).getString("name") +" khỏi danh sách" , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        mData.remove(position);
                        mChangePrice.NewPrice(updatePrice(mData));
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
        private TextView tvName, tvUnitPrice, tvQuantity ,tvDiscount;
        private RelativeLayout lnParent;
        private CircleImageView imgProduct;
        private CTextIcon btnSub, btnPlus;

        public ProductDialogShopCartAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.shopcart_products_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.shopcart_products_item_name);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.shopcart_products_item_unitprice);
            imgProduct = (CircleImageView) itemView.findViewById(R.id.shopcart_products_item_image);
            tvQuantity = (TextView) itemView.findViewById(R.id.shopcart_products_item_quantity);
            btnSub = (CTextIcon) itemView.findViewById(R.id.shopcart_products_item_sub);
            btnPlus = (CTextIcon) itemView.findViewById(R.id.shopcart_products_item_plus);
            tvDiscount = (TextView) itemView.findViewById(R.id.shopcart_products_item_discount);

        }

    }

    public void addItemProduct(Product product){
        mData.add(product);
//        notifyDataSetChanged();
        notifyItemInserted(mData.size());
        mChangePrice.NewPrice(updatePrice(mData));
    }

    public List<Product> getAllDataProduct(){
        return mData;
    }

    private Double updatePrice(List<Product> list){
        Double sum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getDouble("totalMoney");
        }
        return sum;
    }


}
