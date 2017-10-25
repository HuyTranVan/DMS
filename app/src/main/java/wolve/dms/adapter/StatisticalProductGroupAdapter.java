package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class StatisticalProductGroupAdapter extends RecyclerView.Adapter<StatisticalProductGroupAdapter.StatisticalProductGroupViewHolder> {
    List<BillDetail> mListDetail = new ArrayList<>();
    private List<ProductGroup> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public StatisticalProductGroupAdapter(List<BillDetail> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = Util.mListProductGroup;
        this.mListDetail = data;
        this.mContext = Util.getInstance().getCurrentActivity();

    }

    @Override
    public StatisticalProductGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_productgroup_item, parent, false);
        return new StatisticalProductGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalProductGroupViewHolder holder, final int position) {
            List<Product> mProduct = new ArrayList<>();
            holder.tvGroupName.setText(mData.get(position).getString("name"));
            mProduct = sumProductByGroup(mData.get(position).getInt("id"));

            StatisticalProductAdapter adapter = new StatisticalProductAdapter(mProduct);
            adapter.notifyDataSetChanged();
            holder.rvGroup.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            holder.rvGroup.setLayoutManager(linearLayoutManager);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalProductGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName;
        private RecyclerView rvGroup;
        private CardView lnParent;

        public StatisticalProductGroupViewHolder(View itemView) {
            super(itemView);

            tvGroupName = (TextView) itemView.findViewById(R.id.statistical_productgroup_item_group);
            rvGroup = (RecyclerView) itemView.findViewById(R.id.statistical_productgroup_item_rvproduct);
            lnParent = (CardView) itemView.findViewById(R.id.statistical_productgroup_item_content_parent);

        }

    }

    private List<Product> sumProductByGroup(int groupId){
        List<Product> productList = new ArrayList<>();

        for(int i=0; i<Util.mListProduct.size(); i++){
            try {
                int groupid = new JSONObject(Util.mListProduct.get(i).getString("productGroup")).getInt("id");
                int sumQuantity =0;
                if (groupId == groupid){
                    for (int j=0; j<mListDetail.size(); j++){
                        if (mListDetail.get(j).getInt("productId") == Util.mListProduct.get(i).getInt("id")){
                            sumQuantity += mListDetail.get(j).getInt("quantity");
                        }
                    }

                }

                if (sumQuantity !=0){
                    Product product = new Product(new JSONObject());
                    product.put("name", Util.mListProduct.get(i).getString("name"));
                    product.put("sumquantity", sumQuantity);

                    productList.add(product);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }



}
