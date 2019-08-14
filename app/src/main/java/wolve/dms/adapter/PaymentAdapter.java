package wolve.dms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
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
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PrintBillViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public PaymentAdapter(List<BaseModel> list) {
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
        String date = Util.DateHourString(mData.get(position).getLong("createAt"));
        String user = mData.get(position).getBaseModel("user").getString("displayName");
        String note = "";
        if (mData.get(position).hasKey("idbillreturn")){
            note = "thu hàng";
            //holder.tvTotal.setTypeface(null, Typeface.BOLD);

        }else if (mData.get(position).getDouble("paid") <0.0){
            note ="trả khách";
            //holder.tvTotal.setTypeface(null, Typeface.BOLD);
        }else {
            note ="thu tiền";
            //holder.tvTotal.setTypeface(null, Typeface.NORMAL);
        }

        holder.tvDate.setText(String.format("%s %s %s:",date, user, note));
        holder.tvTotal.setText(String.format("%s %s đ",mData.get(position).getDouble("paid") <0.0? "+" : "-",
                mData.get(position).getDouble("paid") <0.0? Util.FormatMoney(mData.get(position).getDouble("paid") *-1) :Util.FormatMoney(mData.get(position).getDouble("paid"))));



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Double getTotalMoney(){
        Double total =0.0;
        for (int i=0; i<mData.size(); i++){
//            try {
            total += (mData.get(i).getDouble("debt") );
//            } catch (JSONException e) {
//                total =0.0;
//            }
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
