package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackClickProduct;
import wolve.dms.models.Product;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartProductDialogAdapter extends RecyclerView.Adapter<CartProductDialogAdapter.ProductDialogShopCartAdapterViewHolder> {
    private List<Product> mData = new ArrayList<>();
    private List<Product> allData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Boolean isPromotion;

    public CartProductDialogAdapter(List<Product> list, int productGroupId, Boolean ispromotion) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.allData = list;
        this.isPromotion = ispromotion;

        for (int i=0 ; i<allData.size(); i++){
            try {
                int groupid = new JSONObject(allData.get(i).getString("productGroup")).getInt("id");
                if (!isPromotion){
                    if (productGroupId == groupid){
                        this.mData.add(allData.get(i));
                    }
                }else {
                    if (productGroupId == groupid && allData.get(i).getBoolean("promotion")){
                        this.mData.add(allData.get(i));
                    }
                }

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void reloadList(int groupId){
        mData = new ArrayList<>();
        for (int i=0 ; i<allData.size(); i++){
            try {
                int groupid = new JSONObject(allData.get(i).getString("productGroup")).getInt("id");
                if (groupId == groupid){
                    mData.add(allData.get(i));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ProductDialogShopCartAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cart_product_dialog_item, parent, false);
        return new ProductDialogShopCartAdapterViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ProductDialogShopCartAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvUnitPrice.setText(Util.FormatMoney(mData.get(position).getDouble("unitPrice")));
        holder.cbCheck.setOnCheckedChangeListener(null);
        holder.cbCheck.setChecked(mData.get(position).getBoolean("checked")? true: false);

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (mData.get(position).getBoolean("checked")){
                     holder.cbCheck.setChecked(false);
//                     putValueToParentData(mData.get(position), false);
                 }else {
                     holder.cbCheck.setChecked(true);
//                     putValueToParentData(mData.get(position), true);
                 }
            }
        });

        holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked){
                        mData.get(position).put("checked", true);
//                        putValueToParentData(mData.get(position), true);

                    }else {
                        mData.get(position).put("checked", false);
//                        putValueToParentData(mData.get(position), false);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ProductDialogShopCartAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvUnitPrice;
        private RelativeLayout lnParent;
        private CircleImageView imgProduct;
        private CheckBox cbCheck;

        public ProductDialogShopCartAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.shopcart_dialog_product_item_name);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.shopcart_dialog_product_item_unitprice);
            imgProduct = (CircleImageView) itemView.findViewById(R.id.shopcart_dialog_product_item_image);
            cbCheck = (CheckBox) itemView.findViewById(R.id.shopcart_dialog_product_item_checkbox);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.shopcart_dialog_product_item_parent);

        }

    }

    public List<Product> getAllData(){
        return mData;
    }

//    private void putValueToParentData(Product product, Boolean checked){
//        try {
//            for (int i=0; i<allData.size(); i++){
//                if (product.getInt("id") == allData.get(i).getInt("id")){
//                    allData.get(i).put("checked", checked);
//                    break;
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

}
