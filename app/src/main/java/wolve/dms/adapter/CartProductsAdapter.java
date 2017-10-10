package wolve.dms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CTextView;
import wolve.dms.models.Product;
import wolve.dms.utils.CustomDialog;
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
        holder.tvName.setText(mData.get(position).getString("name") +" ("+Util.FormatMoney(mData.get(position).getDouble("unitPrice")) +")" );
        holder.tvUnitPrice.setText(Util.FormatMoney(mData.get(position).getDouble("totalMoney")));
        holder.tvDiscount.setText(Util.FormatMoney(mData.get(position).getDouble("discount")));
        holder.tvQuantity.setText(mData.get(position).getInt("quantity").toString());

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.showDialogEditProduct(mData.get(position), new CallbackClickProduct() {
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
                CustomDialog.alertWithCancelButton(null, "Xóa " + mData.get(position).getString("name") +" khỏi danh sách" , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
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
        private CTextView btnSub, btnPlus;

        public ProductDialogShopCartAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.shopcart_products_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.shopcart_products_item_name);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.shopcart_products_item_unitprice);
            imgProduct = (CircleImageView) itemView.findViewById(R.id.shopcart_products_item_image);
            tvQuantity = (TextView) itemView.findViewById(R.id.shopcart_products_item_quantity);
            btnSub = (CTextView) itemView.findViewById(R.id.shopcart_products_item_sub);
            btnPlus = (CTextView) itemView.findViewById(R.id.shopcart_products_item_plus);
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
