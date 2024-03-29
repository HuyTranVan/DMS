package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class PrintOldBillAdapter extends RecyclerView.Adapter<PrintOldBillAdapter.PrintBillViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    //private int printSize;

    public PrintOldBillAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        //this.printSize = printSize;

    }

    @Override
    public PrintBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_print_oldbill_item , parent, false);
        return new PrintBillViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PrintBillViewHolder holder, final int position) {
        holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
        holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")));
        holder.tvPaid.setText(Util.FormatMoney(mData.get(position).getDouble("total") - mData.get(position).getDouble("debt")));
        holder.tvDebt.setText(Util.FormatMoney(mData.get(position).getDouble("debt")));

        List<BaseModel> list = DataUtil.array2ListObject(mData.get(position).getString("billDetails"));

        PrintBillAdapter adapterBill = new PrintBillAdapter(list);
        Util.createLinearRV(holder.rvBill, adapterBill);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Double getDebtMoney() {
        Double total = 0.0;
        for (int i = 0; i < mData.size(); i++) {
            total += (mData.get(i).getDouble("debt"));

        }
        return total;
    }


    public class PrintBillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvTotal, tvPaid, tvDebt;
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
