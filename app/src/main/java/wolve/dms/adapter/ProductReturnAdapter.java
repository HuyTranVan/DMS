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
import wolve.dms.callback.CallbackDouble;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductReturnAdapter extends RecyclerView.Adapter<ProductReturnAdapter.CartProductsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackDouble mListener;

    public interface CallbackReturn{
        void returnEqualLessDebt(List<BaseModel> listReturn, Double sumReturn, BaseModel bill);
        void returnMoreThanDebt(List<BaseModel> listReturn, Double sumReturn, BaseModel bill);

    }

    public ProductReturnAdapter(BaseModel bill, CallbackDouble listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mData = updateQuantityBills(bill);

        boolean check = false;
        for (BaseModel baseModel: mData){
            if (!baseModel.getDouble("mergeQuantity").equals(0.0)){
                check = true;
                break;
            }

        }
        if (!check){
            mListener.Result(-1.0);
        }

    }


    @Override
    public CartProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_return_item, parent, false);
        return new CartProductsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartProductsViewHolder holder, final int position) {
        double price = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
        holder.tvName.setText(String.format("%s (%s)",mData.get(position).getString("productName"), Util.FormatMoney(price)));
        holder.tvQuantity.setText(mData.get(position).getString("quantityReturn"));

        int quantityremain = mData.get(position).getInt("mergeQuantity") - mData.get(position).getInt("quantityReturn");
        holder.tvTotalQuantity.setText(String.format("%s",String.valueOf(quantityremain)));

        holder.btnSub.setVisibility(mData.get(position).getInt("quantityReturn") >0 ? View.VISIBLE :View.INVISIBLE);
        holder.btnPlus.setVisibility(mData.get(position).getInt("quantityReturn") >= mData.get(position).getInt("mergeQuantity") ?View.INVISIBLE :View.VISIBLE);

        holder.tvNote.setVisibility(quantityremain >0 ? View.GONE :View.VISIBLE);

        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = mData.get(position).getInt("quantityReturn");
                if ( currentQuantity > 0){
                    mData.get(position).put("quantityReturn", currentQuantity -1);
                    notifyItemChanged(position);

                    mListener.Result(sumReturnBill());
                }

            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnPlus.setVisibility(View.INVISIBLE);
                int currentQuantity = mData.get(position).getInt("quantityReturn");
                mData.get(position).put("quantityReturn", currentQuantity +1);
                notifyItemChanged(position);

                mListener.Result(sumReturnBill());


            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CartProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity , tvTotalQuantity, tvNote, btnSub, btnPlus;
        private RelativeLayout lnParent;

        public CartProductsViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.product_return_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.product_return_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.product_return_item_quantity);
            btnSub = (TextView) itemView.findViewById(R.id.product_return_item_sub);
            btnPlus = (TextView) itemView.findViewById(R.id.product_return_item_plus);
            tvTotalQuantity = itemView.findViewById(R.id.product_return_item_totalquantity);
            tvNote = itemView.findViewById(R.id.product_return_item_note);

        }

    }

    public List<BaseModel> getListSelected(){
        List<BaseModel> listResult = new ArrayList<>();
        for (int i=0; i<mData.size(); i++){
            if (mData.get(i).getInt("quantityReturn") >0){
                BaseModel product = new BaseModel();
                product.put("quantity", mData.get(i).getInt("quantityReturn")*-1);
                Double total = (mData.get(i).getDouble("unitPrice") - mData.get(i).getDouble("discount")) *mData.get(i).getDouble("mergeQuantity") ;
                product.put("total", total);
                Double discount = getDiscountFromOldBill(mData.get(i));
                product.put("discount", discount);
                product.put("id",mData.get(i).getInt("productId"));

                listResult.add(product);
            }

        }

        return listResult;
    }

    private Double getDiscountFromOldBill(BaseModel objectCurrent){
        List<BaseModel> listProduct = Product.getProductList();
        Double discount =0.0;
        for (int i=0; i<listProduct.size(); i++){
            if (listProduct.get(i).getInt("id") == objectCurrent.getInt("productId")){
                Double netPrice = objectCurrent.getDouble("unitPrice") - objectCurrent.getDouble("discount");
                discount =listProduct.get(i).getDouble("unitPrice") - netPrice;
                break;
            }
        }

        return discount;


    }

    public double sumReturnBill(){
        double sum = 0.0;
        for (BaseModel item: mData){
            Double newPrice = item.getDouble("unitPrice") - item.getDouble("discount");
            sum += item.getInt("quantityReturn") * newPrice;
        }

        return sum;
    }

    private List<BaseModel> updateQuantityBills(BaseModel bill){
        List<BaseModel> listDetail = DataUtil.array2ListBaseModel(bill.getJSONArray("billDetails"));

        for (BaseModel detail : listDetail){
            detail.put("quantityReturn", 0);

            if (bill.hasKey(Constants.HAVEBILLRETURN)){
                List<BaseModel> listTemp = DataUtil.array2ListBaseModel(bill.getJSONArray(Constants.HAVEBILLRETURN));
                List<BaseModel> listTempDetail = new ArrayList<>();
                for (BaseModel baseModel : listTemp){
                    listTempDetail.addAll(DataUtil.array2ListBaseModel(baseModel.getJSONArray("billDetails")));

                }

                int quantity = detail.getInt("quantity");
                for (BaseModel tempDetail : listTempDetail){
                    if (tempDetail.getInt("productId") == detail.getInt("productId") &&
                            (tempDetail.getDouble("unitPrice") - tempDetail.getDouble("discount")) == (detail.getDouble("unitPrice") - detail.getDouble("discount"))){

                        quantity += tempDetail.getInt("quantity");

                    }

                }

                detail.put("mergeQuantity", quantity);

            }else {
                detail.put("mergeQuantity", detail.getInt("quantity"));

            }

        }

        return listDetail;
    }

}
