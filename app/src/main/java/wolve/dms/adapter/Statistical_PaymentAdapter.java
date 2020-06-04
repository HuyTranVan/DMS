package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

public class Statistical_PaymentAdapter extends RecyclerView.Adapter<Statistical_PaymentAdapter.StatisticalBillsViewHolder> implements Filterable {
    private List<BaseModel> baseData;
    private List<BaseModel> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public Statistical_PaymentAdapter(String username, List<BaseModel> data, CallbackString listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

        if (username.equals(Constants.ALL_FILTER)) {
            this.baseData = data;

        } else {
            baseData = new ArrayList<>();
            for (BaseModel row : data) {
                if (row.getBaseModel("user").getString("displayName").equals(username)) {
                    baseData.add(row);
                }
            }
        }

        this.mData = baseData;
        DataUtil.sortbyStringKey("createAt", mData, true);


    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_cash_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        holder.tvNumber.setText(String.valueOf(mData.size() - position));

        final BaseModel customer = new BaseModel(mData.get(position).getJsonObject("customer"));
        holder.tvsignBoard.setText(Constants.shopName[customer.getInt("shopType")] + " " + customer.getString("signBoard"));
        holder.tvDistrict.setText(customer.getString("street") + " - " + customer.getString("district"));

        String user = Util.getIconString(R.string.icon_username, "   ", mData.get(position).getBaseModel("user").getString("displayName"));
        String collect = mData.get(position).getInt("user_id") != mData.get(position).getInt("user_collect") ?
                String.format(" (%s thu hộ)", mData.get(position).getBaseModel("collect_by").getString("displayName")) : "";
        holder.tvUser.setText(user + collect);

        String time = Util.DateHourString(mData.get(position).getLong("createAt"));
        holder.tvTime.setText(time);

        holder.tvProfit.setText(String.format("(%s)", Util.FormatMoney(mData.get(position).getDouble("bill_profit"))));

        holder.tvPaid.setText(Util.FormatMoney(mData.get(position).getDouble("paid")));

        holder.vLine.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);

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
        private TextView tvNumber, tvsignBoard, tvDistrict, tvTime, tvPaid, tvProfit, tvUser;
        private View vLine;
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
            vLine = itemView.findViewById(R.id.statistical_cash_item_line);

        }
    }

    public double sumPayments() {
        double totalPayment = 0.0;
        for (BaseModel row : mData) {
            totalPayment += row.getDouble("paid");
        }
        return totalPayment;
    }

    public double sumCollect() {
        double total = 0.0;
        for (BaseModel row : mData) {
            if (row.getInt("user_id") != row.getInt("user_collect")) {
                total += row.getDouble("paid");
            }

        }
        return total;
    }

    public double sumProfit() {
        double totalProfit = 0.0;
        for (BaseModel row : mData) {
            totalProfit += row.getDouble("bill_profit");
        }
        return totalProfit;
    }

    public double sumBaseProfit() {
        double totalProfit = 0.0;
        for (BaseModel row : mData) {
            totalProfit += row.getDouble("base_profit");
        }
        return totalProfit;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.equals(Constants.ALL_TOTAL)) {
                    mData = baseData;

                } else {
                    List<BaseModel> listTemp = new ArrayList<>();
                    for (BaseModel row : baseData) {
                        if (row.getInt("user_id") != row.getInt("user_collect")) {
                            listTemp.add(row);
                        }

                    }

                    mData = listTemp;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (ArrayList<BaseModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
