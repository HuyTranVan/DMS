package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_BillsDetailAdapter extends RecyclerView.Adapter<Customer_BillsDetailAdapter.CustomerBillsDetailAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private Context mContext;

    public Customer_BillsDetailAdapter(List<BaseModel> data) {
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
        Double  netMoney = mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount");
        Double sumMoney = mData.get(position).getInt("quantity") * (mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount"));
        holder.tvName.setText(mData.get(position).getString("productName"));
        holder.tvQuantity.setText(netMoney ==0 ? String.format("SL: %s",mData.get(position).getInt("quantity")) :String.format("%sx%s", mData.get(position).getInt("quantity") ,  Util.FormatMoney(netMoney) ));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerBillsDetailAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity, tvTotal;
        private LinearLayout lnParent;

        public CustomerBillsDetailAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.customer_billdetail_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.customer_billdetail_item_quantity);
            //tvTotal = (TextView) itemView.findViewById(R.id.customer_billdetail_item_total);
            lnParent = (LinearLayout) itemView.findViewById(R.id.customer_billdetail_item_parent);

        }

    }



}
