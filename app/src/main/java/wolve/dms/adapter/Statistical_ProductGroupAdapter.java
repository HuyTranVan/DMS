package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistical_ProductGroupAdapter extends RecyclerView.Adapter<Statistical_ProductGroupAdapter.StatisticalProductGroupViewHolder> {
    //List<BillDetail> mListDetail = new ArrayList<>();
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public Statistical_ProductGroupAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();

        List<BaseModel> listGroup = ProductGroup.getProductGroupList();

        for (int i=0; i<listGroup.size(); i++){
            BaseModel group = createGroupFromBill(listGroup.get(i), data);
            if (group.getInt("count") >0){
                mData.add(group);
            }
        }


    }

    public void updateData(List<BaseModel> data){
        List<BaseModel> listGroup = ProductGroup.getProductGroupList();
        mData = new ArrayList<>();

        for (int i=0; i<listGroup.size(); i++){
            BaseModel group = createGroupFromBill(listGroup.get(i), data);
            if (group.getInt("count") >0){
                mData.add(group);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public StatisticalProductGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_productgroup_item, parent, false);
        return new StatisticalProductGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalProductGroupViewHolder holder, final int position) {
        holder.tvGroupName.setText(mData.get(position).getString("name"));
        holder.tvGroupSum.setText(mData.get(position).getString("count"));

        Statistical_ProductAdapter adapter = new Statistical_ProductAdapter(mData.get(position).getList("products"), new CallbackString() {
            @Override
            public void Result(String s) {
                if (!s.equals("0")){
                    holder.tvSumCheck.setVisibility(View.VISIBLE);
                    holder.tvSumCheck.setText(String.format("(%s)",s));

                }else {
                    holder.tvSumCheck.setVisibility(View.GONE);
//                    holder.tvSumCheck.setText(s);
                }
            }
        });
        Util.createLinearRV(holder.rvGroup, adapter);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalProductGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvGroupSum, tvSumCheck;
        private RecyclerView rvGroup;
        private CardView lnParent;

        public StatisticalProductGroupViewHolder(View itemView) {
            super(itemView);
            tvGroupSum = itemView.findViewById(R.id.statistical_productgroup_item_sum);
            tvGroupName = (TextView) itemView.findViewById(R.id.statistical_productgroup_item_group);
            rvGroup = (RecyclerView) itemView.findViewById(R.id.statistical_productgroup_item_rvproduct);
            lnParent = (CardView) itemView.findViewById(R.id.statistical_productgroup_item_content_parent);
            tvSumCheck = itemView.findViewById(R.id.statistical_productgroup_item_sumchecked);
        }

    }

    private BaseModel createGroupFromBill(BaseModel group, List<BaseModel> detailList){
        List<BaseModel> mResults = new ArrayList<>();
        for (BaseModel detail : detailList){
            if (detail.getInt("productGroup_id") == group.getInt("id")){
                boolean check = false;
                for (BaseModel result : mResults){
                    if (result.getInt("product_id") == detail.getInt("product_id")){
                        int quantity = result.getInt("quantity") + detail.getInt("quantity");
                        double total = result.getDouble("total")
                                + (detail.getDouble("unitPrice") - detail.getDouble("discount")) * detail.getInt("quantity");
                        result.put("quantity", quantity );
                        result.put("total", total );
                        check = true;

                        break;

                    }

                }

                if (!check){
                    BaseModel newDetail = new BaseModel();
                    newDetail.put("product_id", detail.getInt("product_id"));
                    newDetail.put("productName", detail.getString("productName"));
                    newDetail.put("quantity", detail.getInt("quantity"));
                    newDetail.put("total", (detail.getDouble("unitPrice") - detail.getDouble("discount")) * detail.getInt("quantity"));

                    mResults.add(newDetail);

                }

            }

        }
        BaseModel groupResult = new BaseModel();

        groupResult.put("count", Math.round(DataUtil.sumValueFromList(mResults, "quantity") ));
        groupResult.put("name", group.getString("name"));
        groupResult.putList("products", mResults);

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
