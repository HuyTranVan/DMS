package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.utils.DataFilter;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class PrintOldBillAdapter extends RecyclerView.Adapter<PrintOldBillAdapter.PrintBillViewHolder> {
    private List<JSONObject> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int printSize;

    public PrintOldBillAdapter(int printSize, List<JSONObject> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.printSize = printSize;

    }

    @Override
    public PrintBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(printSize == 57? R.layout.adapter_print_oldbill_item : R.layout.adapter_print_oldbill_item, parent, false);
        return new PrintBillViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PrintBillViewHolder holder, final int position) {
        try {
            holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
            holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")));
            holder.tvPaid.setText(Util.FormatMoney(mData.get(position).getDouble("paid")));
            holder.tvDebt.setText(Util.FormatMoney(mData.get(position).getDouble("debt")));

            List<JSONObject> list = DataFilter.array2ListObject(mData.get(position).getString("billDetails"));

            PrintBillAdapter adapterBill = new PrintBillAdapter(printSize , list) ;
            Util.createLinearRV(holder.rvBill, adapterBill);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Double getDebtMoney(){
        Double total =0.0;
        for (int i=0; i<mData.size(); i++){
            try {
                total += (mData.get(i).getDouble("debt"));
            } catch (JSONException e) {
                total =0.0;
            }
        }
        return total;
    }


    public class PrintBillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvTotal, tvPaid, tvDebt ;
        private RecyclerView rvBill;
        private View vLine;

        public PrintBillViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.oldbill_item_date);
            tvTotal = (TextView) itemView.findViewById(R.id.oldbill_item_total);
            tvPaid = (TextView) itemView.findViewById(R.id.oldbill_item_paid);
            tvDebt = (TextView) itemView.findViewById(R.id.oldbill_item_debt);
            rvBill = (RecyclerView) itemView.findViewById(R.id.oldbill_item_rvbill);
//            vLine= itemView.findViewById(R.id.item_seperateline);
        }

    }

}
