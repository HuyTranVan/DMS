package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistica_PaymentAdapter extends RecyclerView.Adapter<Statistica_PaymentAdapter.StatisticalBillsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public Statistica_PaymentAdapter(String user, List<BaseModel> data, CallbackString listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        //this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

        if (user.equals(Constants.ALL_FILTER)){
            this.mData = DataUtil.groupCustomerPayment(data);

        }else {
            mData = new ArrayList<>();
            List<BaseModel> listTemp = DataUtil.groupCustomerPayment(data);
            for (BaseModel row : listTemp){
                if (row.getBaseModel("user").getString("displayName").equals(user)){
                    mData.add(row);
                }
            }
        }
        DataUtil.sortbyStringKey("createAt", mData, true);


    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_cash_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        holder.tvNumber.setText(String.valueOf(mData.size() -position));

        final BaseModel customer = new BaseModel(mData.get(position).getJsonObject("customer"));
        holder.tvsignBoard.setText(Constants.getShopName(customer.getString("shopType") ) + " " + customer.getString("signBoard"));
        holder.tvDistrict.setText(customer.getString("street") + " - " + customer.getString("district"));

        String user = String.format("Nhân viên: %s",mData.get(position).getBaseModel("user").getString("displayName"));
        holder.tvUser.setText(user);

        String time = Util.DateHourString(mData.get(position).getLong("createAt"));
        holder.tvTime.setText(time  );

        //double va = mData.get(position).getDouble("paid") * mData.get(position).getDouble("billProfit") /  mData.get(position).getDouble("billTotal");
        //holder.tvProfit.setText(String.format("(%s)",Util.FormatMoney(mData.get(position).getDouble("billProfit"))));

        holder.tvPaid.setText(Util.FormatMoney(mData.get(position).getDouble("paid")));

        holder.vLine.setVisibility(position == mData.size()-1? View.GONE:View.VISIBLE);

        holder.tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.Result(customer.getString("id"));
            }

        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvDistrict, tvUser, tvTime, tvPaid, tvProfit;
        private View vLine;
//        private View vLineUpper, vLineUnder;
        private LinearLayout lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.statistical_cash_item_number);
            tvsignBoard = itemView.findViewById(R.id.statistical_cash_item_signboard);
            tvDistrict = itemView.findViewById(R.id.statistical_cash_item_district);
            tvUser = itemView.findViewById(R.id.statistical_cash_item_user);
            tvTime = itemView.findViewById(R.id.statistical_cash_item_time);
            tvPaid = itemView.findViewById(R.id.statistical_cash_item_paid);
            tvProfit = itemView.findViewById(R.id.statistical_cash_item_profit);
            vLine =itemView.findViewById(R.id.statistical_cash_item_line);

        }
    }

    public double sumPayments(){
        double totalPayment = 0.0;
        for (BaseModel row : mData){
            totalPayment += row.getDouble("paid");
        }
        return totalPayment;
    }

    public double sumProfit(){
        double totalProfit = 0.0;
//        for (BaseModel row : mData){
//            totalProfit += row.getDouble("billProfit");
//        }
        return totalProfit;
    }



}
