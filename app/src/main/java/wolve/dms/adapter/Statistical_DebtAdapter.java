package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistical_DebtAdapter extends RecyclerView.Adapter<Statistical_DebtAdapter.StatisticalBillsViewHolder> {
    //private List<BaseModel> baseData = new ArrayList<>();
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private TextView tvSum;
    private CallbackString mListener;
    //protected double totalDebt;

    public Statistical_DebtAdapter(String user, List<BaseModel> data, CallbackString listener, CallbackInt number) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        //this.tvSum = tvsum;
        //this.baseData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

        if (user.equals(Constants.ALL_FILTER)) {
            this.mData = data;
        } else {
            mData = new ArrayList<>();
            for (BaseModel row : data) {
                if (row.getBaseModel("user").getString("displayName").equals(user)) {
                    mData.add(row);
                }
            }
        }
        number.onResponse(mData.size());
        DataUtil.sortbyDoubleKey("debt", mData, true);

    }

    public void sortUp() {
        Collections.reverse(mData);
        notifyDataSetChanged();
    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_debt_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        holder.tvNumber.setText(mData.size() > 0 ? String.valueOf(mData.size() - position) : "");
        holder.tvsignBoard.setText(Constants.shopName[mData.get(position).getInt("shopType")] + " " + mData.get(position).getString("signBoard"));
        holder.tvDistrict.setText(mData.get(position).getString("street") + " - " + mData.get(position).getString("district"));
        holder.tvUser.setText(Util.getIconString(R.string.icon_username, "  ", mData.get(position).getBaseModel("user").getString("displayName")));
        holder.tvDebt.setText(Util.FormatMoney(mData.get(position).getDouble("debt")));
        holder.tvTime.setText(String.format("%d ngày", Util.countDay(mData.get(position).getLong("last_debt"))));

        holder.vLine.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);

        holder.tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.Result(mData.get(position).getString("id"));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvDistrict, tvUser, tvTime, tvDebt;
        private View vLine;
        //        private View vLineUpper, vLineUnder;
        private LinearLayout lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.statistical_debt_item_number);
            tvsignBoard = itemView.findViewById(R.id.statistical_debt_item_signboard);
            tvDistrict = itemView.findViewById(R.id.statistical_debt_item_district);
            tvUser = itemView.findViewById(R.id.statistical_debt_item_user);
            tvTime = itemView.findViewById(R.id.statistical_debt_item_last_debt);
            tvDebt = itemView.findViewById(R.id.statistical_debt_item_debt);
            vLine = itemView.findViewById(R.id.statistical_debt_item_line);
//            vLineUnder = itemView.findViewById(R.id.statistical_cash_item_under);
//            vLineUpper = itemView.findViewById(R.id.statistical_cash_item_upper);


        }

    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String charString = charSequence.toString();
//                if (charString.equals(Constants.ALL_FILTER)) {
//                    mData = baseData;
//                } else {
//                    List<BaseModel> listTemp = new ArrayList<>();
//                    for (BaseModel row : baseData) {
//                        if (row.getString("userName").equals(charString)){
//                            listTemp.add(row);
//                        }
//
//                    }
//
//                    mData = listTemp;
//                }
//                tvSum.setText(String.format("Tổng công nợ: %s", Util.FormatMoney(updateSumDebt(mData))));
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mData;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                mData = (ArrayList<BaseModel>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }

    public double sumDebts() {
        double totalDebt = 0.0;
        for (BaseModel row : mData) {
            totalDebt += row.getDouble("debt");
        }
        return totalDebt;
    }

}
