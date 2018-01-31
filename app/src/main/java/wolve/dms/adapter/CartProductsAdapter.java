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
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.Product;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder> {
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
    public CartProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cart_products_item, parent, false);
        return new CartProductsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartProductsViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvUnitPrice.setText(String.format("đ  %s",Util.FormatMoney(mData.get(position).getDouble("unitPrice"))));
        holder.tvTotal.setText(String.format("%s đ",Util.FormatMoney(mData.get(position).getDouble("totalMoney"))));

        Double discount = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
        holder.tvQuantityDisplay.setText(String.format("%s x %s",mData.get(position).getString("quantity"), Util.FormatMoney(discount) ));

        holder.tvQuantity.setText(mData.get(position).getString("quantity"));
        holder.btnSub.setVisibility(mData.get(position).getInt("quantity") >1 ? View.VISIBLE :View.GONE);

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

                        Double discount = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
                        mData.get(position).put("totalMoney", (currentQuantity -1)* discount);
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
                    Double discount = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
                    mData.get(position).put("totalMoney", (currentQuantity + 1)* discount);
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

    public class CartProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvUnitPrice, tvQuantity ,tvQuantityDisplay, tvTotal;
        private RelativeLayout lnParent;
        private CircleImageView imgProduct;
        private CTextIcon btnSub, btnPlus;

        public CartProductsViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.shopcart_products_item_parent);
            imgProduct = (CircleImageView) itemView.findViewById(R.id.shopcart_products_item_image);
            tvName = (TextView) itemView.findViewById(R.id.shopcart_products_item_name);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.shopcart_products_item_unitprice);
            tvQuantityDisplay = itemView.findViewById(R.id.shopcart_products_item_quantity_display);

            tvQuantity = (TextView) itemView.findViewById(R.id.shopcart_products_item_quantity);
            btnSub = (CTextIcon) itemView.findViewById(R.id.shopcart_products_item_sub);
            btnPlus = (CTextIcon) itemView.findViewById(R.id.shopcart_products_item_plus);
            tvTotal = (TextView) itemView.findViewById(R.id.shopcart_products_item_total);


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
