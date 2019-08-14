package wolve.dms.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class StatisticalProductAdapter extends RecyclerView.Adapter<StatisticalProductAdapter.StatisticalProductGroupViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public StatisticalProductAdapter(JSONArray data, CallbackString callback) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mListener = callback;
        this.mContext = Util.getInstance().getCurrentActivity();

        try {
            for (int i=0; i<data.length(); i++){
                BaseModel product = new BaseModel(data.getJSONObject(i));
                product.put("isChecked", false);
                mData.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public StatisticalProductGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_product_item, parent, false);
        return new StatisticalProductGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalProductGroupViewHolder holder, final int pos) {
        holder.tvName.setText(mData.get(pos).getString("name"));
        holder.tvQuantity.setText(String.valueOf(mData.get(pos).getInt("sumquantity")));

        if (mData.get(pos).getBoolean("isChecked")){
            holder.cbCheck.setVisibility(View.VISIBLE);
//            holder.cbCheck.setChecked(true);
            holder.tvName.setTypeface(null, Typeface.BOLD);
            holder.tvQuantity.setTypeface(null, Typeface.BOLD);
//            holder.tvQuantity.setTextColor(mContext.getResources().getColor(R.color.colorBlueDark));


        }else {
            holder.cbCheck.setVisibility(View.GONE);
//            holder.cbCheck.setChecked(false);
            holder.tvName.setTypeface( null, Typeface.NORMAL);
            holder.tvQuantity.setTypeface(null, Typeface.NORMAL);
//            holder.tvQuantity.setTextColor(mContext.getResources().getColor(R.color.black_text_color));

        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = mData.get(pos).getBoolean("isChecked");
//                try {

                    mData.get(pos).put("isChecked", check? false : true);
                    mListener.Result(String.valueOf(getSumProduct()));
                    notifyDataSetChanged();

//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });

        //holder.line.setVisibility(pos == mData.size() -1 ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalProductGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView  tvQuantity;
        private TextView tvName;
        private CheckBox cbCheck;
        private View line;
        private RecyclerView rvGroup;
        private RelativeLayout lnParent;

        public StatisticalProductGroupViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.statistical_product_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.statistical_product_item_quantity);
            rvGroup = (RecyclerView) itemView.findViewById(R.id.statistical_productgroup_item_rvproduct);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.statistical_product_item_parent);
            line = (View) itemView.findViewById(R.id.statistical_product_item_line);
            cbCheck = itemView.findViewById(R.id.statistical_product_item_check);


        }

    }

    private int getSumProduct(){
        int sum =0;
        for (int i=0; i<mData.size(); i++){
            if (mData.get(i).getBoolean("isChecked")){
                sum += mData.get(i).getInt("sumquantity");
            }
        }
        return sum;
    }



}
