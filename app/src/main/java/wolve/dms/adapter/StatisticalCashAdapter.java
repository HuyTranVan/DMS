package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class StatisticalCashAdapter extends RecyclerView.Adapter<StatisticalCashAdapter.StatisticalBillsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public StatisticalCashAdapter(List<BaseModel> data, CallbackString listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        Collections.reverse(mData);

    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_cash_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        try {
            holder.tvNumber.setText(String.valueOf(mData.size() -position));

            final BaseModel customer = new BaseModel(mData.get(position).getJsonObject("customer"));
            holder.tvsignBoard.setText(Constants.getShopTitle(customer.getString("shopType") , null) + " " + customer.getString("signBoard"));
            holder.tvDistrict.setText(customer.getString("street") + " - " + customer.getString("district"));

            String user = String.format("Nhân viên: %s",mData.get(position).getJsonObject("user").getString("displayName"));
            holder.tvUser.setText(user);

            String time = Util.DateHourString(mData.get(position).getLong("createAt"));
            holder.tvTime.setText(time);

            holder.tvPaid.setText(Util.FormatMoney(mData.get(position).getDouble("paid")));

            holder.vLine.setVisibility(position == mData.size()-1? View.GONE:View.VISIBLE);

            holder.tvNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.Result(customer.getString("id"));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvDistrict, tvUser, tvTime, tvPaid;
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
            vLine =itemView.findViewById(R.id.statistical_cash_item_line);
//            vLineUnder = itemView.findViewById(R.id.statistical_cash_item_under);
//            vLineUpper = itemView.findViewById(R.id.statistical_cash_item_upper);


        }

    }


}
