package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerBillsDetailAdapter extends RecyclerView.Adapter<CustomerBillsDetailAdapter.CustomerBillsDetailAdapterViewHolder> {
    private List<BillDetail> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CustomerBillsDetailAdapter(List<BillDetail> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();

    }

    @Override
    public CustomerBillsDetailAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_customer_billsdetail_item, parent, false);
        return new CustomerBillsDetailAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerBillsDetailAdapterViewHolder holder, final int position) {
//            holder.lnParent.setBackgroundResource(( position % 2 ) == 0 ? R.color.colorLightGrey: R.color.colorWhite);
            holder.tvName.setText(mData.get(position).getString("productName"));
            holder.tvQuantity.setText(mData.get(position).getInt("quantity") + "x" + Util.FormatMoney(mData.get(position).getDouble("unitPrice")));
            holder.tvDiscount.setText(mData.get(position).getDouble("discount") ==0 ? "--" : "- "+ Util.FormatMoney(mData.get(position).getDouble("discount")));
            Double sumMoney = mData.get(position).getInt("quantity") * (mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount"));
            holder.tvTotal.setText(Util.FormatMoney(sumMoney));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerBillsDetailAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity, tvDiscount, tvTotal;
        private LinearLayout lnParent;

        public CustomerBillsDetailAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.customer_billdetail_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.customer_billdetail_item_quantity);
            tvDiscount = (TextView) itemView.findViewById(R.id.customer_billdetail_item_discount);
            tvTotal = (TextView) itemView.findViewById(R.id.customer_billdetail_item_total);
            lnParent = (LinearLayout) itemView.findViewById(R.id.customer_billdetail_item_parent);

        }

    }



}
