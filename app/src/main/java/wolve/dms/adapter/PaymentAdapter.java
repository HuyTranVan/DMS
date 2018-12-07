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
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PrintBillViewHolder> {
    private List<JSONObject> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public PaymentAdapter(List<JSONObject> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;

    }

    @Override
    public PrintBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_payment_item, parent, false);
        return new PrintBillViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PrintBillViewHolder holder, final int position) {
        try {
            holder.tvDate.setText(String.format("%s",Util.DateHourString(mData.get(position).getLong("createAt"))));
            holder.tvTotal.setText(String.format("Trả: %s",Util.FormatMoney(mData.get(position).getDouble("paid"))));

            holder.vLine.setVisibility(position == mData.size() -1?View.GONE:View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Double getTotalMoney(){
        Double total =0.0;
        for (int i=0; i<mData.size(); i++){
            try {
                total += (mData.get(i).getDouble("debt") );
            } catch (JSONException e) {
                total =0.0;
            }
        }
        return total;
    }

    public class PrintBillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvTotal ;
        private View vLine;

        public PrintBillViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.payment_item_date);
            tvTotal = (TextView) itemView.findViewById(R.id.payment_item_total);
            vLine = itemView.findViewById(R.id.item_seperateline);
        }

    }

}
