package wolve.dms.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private List<BaseModel> mBillDetail = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackChangePrice mChangePrice;

    public CartProductsAdapter(List<BaseModel> list, List<BaseModel> listbilldetail, CallbackChangePrice callbackChangePrice) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mChangePrice = callbackChangePrice;
        this.mData = list;
        this.mBillDetail = listbilldetail;

    }

    @Override
    public CartProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cart_products_item, parent, false);
        return new CartProductsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartProductsViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvCount.setText(String.valueOf(position +1));

        Double discount = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
        holder.tvQuantityDisplay.setText(String.format("%s x %s",mData.get(position).getString("quantity"), Util.FormatMoney(discount) ));

        holder.tvQuantity.setText(mData.get(position).getString("quantity"));

        holder.coParent.setBackgroundColor(mData.get(position).getDouble("totalMoney") ==0?
                mContext.getResources().getColor(R.color.colorLightGrey) :
                mContext.getResources().getColor(R.color.colorWhite));

        if (mData.get(position).hasKey("hasDouble") && mData.get(position).getBoolean("hasDouble")){
            holder.btnCopy.setVisibility(View.GONE);
        }else {
            //holder.btnCopy.setVisibility(View.VISIBLE);
            holder.btnCopy.setVisibility(mData.get(position).getDouble("totalMoney") ==0?View.GONE:View.VISIBLE);
        }

        holder.tvRibbon.setVisibility(mData.get(position).getDouble("totalMoney") ==0?View.VISIBLE:View.GONE);
        holder.btnPromotion.setVisibility(mData.get(position).getDouble("totalMoney") ==0?View.GONE:View.VISIBLE);

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCenterDialog.showDialogEditProduct(mData.get(position), listPriceSuggest(mData.get(position).getInt("id")), new CallbackClickProduct() {
                    @Override
                    public void ProductChoice(BaseModel product) {
                            mData.get(position).put("quantity", product.getInt("quantity"));
                            mData.get(position).put("discount", product.getDouble("discount"));
                            mData.get(position).put("totalMoney", product.getDouble("totalMoney"));

                            mChangePrice.NewPrice(totalPrice());
                            notifyItemChanged(position);

                    }

                    @Override
                    public void ProductAdded(BaseModel newProduct) {
                        mData.add(newProduct);
                        mChangePrice.NewPrice(totalPrice());
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = mData.get(position).getInt("quantity");
                if ( currentQuantity > 1){
                    mData.get(position).put("quantity", currentQuantity -1);

                    Double discount = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
                    mData.get(position).put("totalMoney", (currentQuantity -1)* discount);
                    mChangePrice.NewPrice(totalPrice());
                    notifyItemChanged(position);
                }else {
                    removeItem(position);


                }

            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = mData.get(position).getInt("quantity");
                mData.get(position).put("quantity", currentQuantity +1);
                Double discount = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
                mData.get(position).put("totalMoney", (currentQuantity + 1)* discount);
                mChangePrice.NewPrice(totalPrice());
                notifyItemChanged(position);

            }
        });

        holder.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyNewProduct(position);
            }
        });

        holder.btnPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.get(position).put("discount", mData.get(position).getDouble("unitPrice"));
                mData.get(position).put("totalMoney", "0");

                notifyItemMoved(position, mData.size()-1);
                mData.add(mData.get(position));
                mData.remove(position);
                new Handler().postDelayed (new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();

                    }}, 200);


                mChangePrice.NewPrice(totalPrice());
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CartProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity ,tvQuantityDisplay, tvRibbon, tvCount;
        private LinearLayout lnParent;
        private RelativeLayout coParent;
        //private CircleImageView imgProduct;
        private CTextIcon btnSub, btnPlus, btnCopy, btnPromotion, btnDelete;
//        private SwipeRevealLayout swipeRevealLayout;

        public CartProductsViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.shopcart_products_item_parent);
            //imgProduct = (CircleImageView) itemView.findViewById(R.id.shopcart_products_item_image);
            tvName = (TextView) itemView.findViewById(R.id.shopcart_products_item_name);
            tvCount = (TextView) itemView.findViewById(R.id.shopcart_products_item_count);
            tvQuantityDisplay = itemView.findViewById(R.id.shopcart_products_item_quantity_display);
            tvQuantity = (TextView) itemView.findViewById(R.id.shopcart_products_item_quantity);
            tvRibbon = itemView.findViewById(R.id.shopcart_products_item_ribbon);
            btnSub = (CTextIcon) itemView.findViewById(R.id.shopcart_products_item_sub);
            btnPlus = (CTextIcon) itemView.findViewById(R.id.shopcart_products_item_plus);
            btnCopy = itemView.findViewById(R.id.shopcart_products_item_copy);
            btnPromotion = itemView.findViewById(R.id.shopcart_products_item_promotion);
            btnDelete = itemView.findViewById(R.id.shopcart_products_item_delete);
            coParent = (RelativeLayout) itemView.findViewById(R.id.shopcart_products_item_coparent);

        }

    }

    public void addItemProduct(int pos,Product product){
        mData.add(pos, product);
        notifyDataSetChanged();
//        notifyItemInserted(mData.size());
        mChangePrice.NewPrice(totalPrice());
    }

    public void addItemProduct(Product product){
        mData.add(product);
//        notifyDataSetChanged();
        notifyItemInserted(mData.size());
        mChangePrice.NewPrice(totalPrice());
    }

    public List<BaseModel> getAllDataProduct(){
        return mData;
    }

    public void removeItem(final int pos){
        CustomCenterDialog.alertWithCancelButton(null, "Xóa " + mData.get(pos).getString("name") +" khỏi danh sách" , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    if (mData.get(pos).getString("totalMoney").equals("0")){
                        for (int i=0; i<mData.size(); i++){
                            if (mData.get(i).getInt("id") == mData.get(pos).getInt("id")){
                                mData.get(i).put("hasDouble", false);
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    }

                    mData.remove(pos);
                    mChangePrice.NewPrice(totalPrice());
                    notifyItemRemoved(pos);
                    notifyItemChanged(pos);



                }

            }
        });

    }


    @Override
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
    }

    public List<BaseModel> getAllData(){
        return mData;
    }

    public double totalPrice(){
        Double sum = 0.0;
        for (int i = 0; i < mData.size(); i++) {
            sum += mData.get(i).getDouble("totalMoney");
        }
        return sum;
    }

    private void copyNewProduct(int pos){
        BaseModel newProduct = new BaseModel();

        newProduct.put("id", mData.get(pos).getString("id"));
        newProduct.put("name", mData.get(pos).getString("name"));
        newProduct.put("productGroup", mData.get(pos).getString("productGroup"));
        newProduct.put("promotion", mData.get(pos).getBoolean("promotion"));
        newProduct.put("unitPrice", mData.get(pos).getDouble("unitPrice"));
        newProduct.put("purchasePrice", mData.get(pos).getDouble("purchasePrice"));
        newProduct.put("volume", mData.get(pos).getLong("volume"));
        newProduct.put("image", mData.get(pos).getString("image"));
        newProduct.put("imageUrl", mData.get(pos).getString("imageUrl"));
        newProduct.put("checked", mData.get(pos).getBoolean("checked"));
        newProduct.put("isPromotion", mData.get(pos).getBoolean("isPromotion"));
        newProduct.put("quantity", mData.get(pos).getInt("quantity"));
        newProduct.put("discount", mData.get(pos).getDouble("unitPrice"));
        newProduct.put("totalMoney", "0");

        mData.get(pos).put("hasDouble", true);
        mData.add(newProduct);
        mChangePrice.NewPrice(totalPrice());
        notifyItemInserted(mData.size() -1);
        notifyItemChanged(pos);
    }

    private List<Double> listPriceSuggest(int productID){
        List<Double> listResult = new ArrayList<>();

        for (BaseModel model : mBillDetail){
            if (model.getInt("productId") == productID){
                if (model.getDoubleValue("discount") != model.getDoubleValue("unitPrice")){
                    double dou = model.getDoubleValue("unitPrice") - model.getDoubleValue("discount");

                    if (!DataUtil.checkDuplicateDouble(listResult, dou)) {
                        listResult.add(dou);
                    }

                }
            }

        }

        return listResult;
    }


}
