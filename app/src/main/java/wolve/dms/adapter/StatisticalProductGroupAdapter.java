package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class StatisticalProductGroupAdapter extends RecyclerView.Adapter<StatisticalProductGroupAdapter.StatisticalProductGroupViewHolder> {
    //List<BillDetail> mListDetail = new ArrayList<>();
    private List<ProductGroup> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public StatisticalProductGroupAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        //this.mListDetail = data;
        this.mContext = Util.getInstance().getCurrentActivity();

        List<ProductGroup> listGroup = ProductGroup.getProductGroupList();

        for (int i=0; i<listGroup.size(); i++){
            ProductGroup group = createGroupFromBill(listGroup.get(i), data);
            if (group.getInt("count") >0){
                mData.add(group);
            }
        }


    }

    @Override
    public StatisticalProductGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_productgroup_item, parent, false);
        return new StatisticalProductGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalProductGroupViewHolder holder, final int position) {

        holder.tvGroupName.setText(mData.get(position).getString("name"));
            //mProduct = filterProductByGroup(mData.get(position).getInt("id"));

        holder.tvGroupSum.setText(mData.get(position).getString("count"));

        List<Product> mProduct = new ArrayList<>();
        StatisticalProductAdapter adapter = new StatisticalProductAdapter(mData.get(position).getJSONArray("products"));
        Util.createLinearRV(holder.rvGroup, adapter);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalProductGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvGroupSum;
        private RecyclerView rvGroup;
        private CardView lnParent;

        public StatisticalProductGroupViewHolder(View itemView) {
            super(itemView);
            tvGroupSum = itemView.findViewById(R.id.statistical_productgroup_item_sum);
            tvGroupName = (TextView) itemView.findViewById(R.id.statistical_productgroup_item_group);
            rvGroup = (RecyclerView) itemView.findViewById(R.id.statistical_productgroup_item_rvproduct);
            lnParent = (CardView) itemView.findViewById(R.id.statistical_productgroup_item_content_parent);

        }

    }

    private ProductGroup createGroupFromBill(ProductGroup group, List<BaseModel> detailList){
        List<Product> products = Product.getProductList();
        ProductGroup groupResult = group;

        JSONArray productLists = new JSONArray();

        try {
            for(int i=0; i< products.size(); i++){
                int groupid = new JSONObject(products.get(i).getString("productGroup")).getInt("id");

                int sumQuantity =0;
                if (group.getInt("id") == groupid){
                    for (int j=0; j<detailList.size(); j++){
                        if (detailList.get(j).getString("productId").equals(products.get(i).getString("id"))){
                            sumQuantity += detailList.get(j).getInt("quantity");
                        }
                    }

                }

                if (sumQuantity >0){
                    Product product = new Product();
                    product.put("name", products.get(i).getString("name"));
                    product.put("sumquantity", sumQuantity);

                    productLists.put(product.ProductJSONObject());
                }
            }

            groupResult.put("count",sumProductQuantity(productLists) );
            groupResult.put("products", productLists);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return groupResult;
    }

    private int sumProductQuantity(JSONArray array){
        int sum = 0;
        try{
            for (int i=0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                sum+= object.getInt("sumquantity");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sum;
    }


}
