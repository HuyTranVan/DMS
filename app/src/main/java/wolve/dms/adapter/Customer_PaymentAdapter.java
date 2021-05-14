package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_PaymentAdapter extends RecyclerView.Adapter<Customer_PaymentAdapter.CustomerPaymentViewHolder> implements Filterable {
    private List<BaseModel> mData = new ArrayList<>();
    private List<BaseModel> baseData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackListObject mListenerList;

    public interface CallbackListObject {
        void onResponse(List<BaseModel> listResult, Double total, int id);
    }

    public Customer_PaymentAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.baseData = data;
        this.mData = baseData;
        this.mContext = Util.getInstance().getCurrentActivity();

        Collections.sort(mData, new Comparator<BaseModel>() {
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("createAt").compareToIgnoreCase(obj2.getString("createAt"));
            }
        });
        Collections.reverse(mData);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mData = baseData;

                } else {
                    BaseModel contentObj = new BaseModel(charString);
                    List<BaseModel> listTemp = new ArrayList<>();
                    for (BaseModel row : baseData) {
                        if (row.getLong("createAt") >= contentObj.getLong("from") && row.getLong("createAt") <= contentObj.getLong("to")) {
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

    public void updateData(List<BaseModel> data) {
        this.baseData = data;
        this.mData = baseData;
        Collections.sort(mData, new Comparator<BaseModel>() {
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("createAt").compareToIgnoreCase(obj2.getString("createAt"));
            }
        });
        Collections.reverse(mData);
        notifyDataSetChanged();
    }

    @Override
    public CustomerPaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_customer_payment_item, parent, false);
        return new CustomerPaymentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerPaymentViewHolder holder, final int position) {
        holder.tvTime.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);

        if (mData.get(position).hasKey("type")) {
            switch (mData.get(position).getString("type")) {
                case Constants.BILL:
                    holder.tvText.setText("Mua hàng ");
                    holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
                    holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")));

                    break;

                case Constants.PAYMENT:

                    if (mData.get(position).getDoubleValue("total") >= 0.0) {
                        if (mData.get(position).hasKey("payByReturn") && mData.get(position).getInt("payByReturn") != 1) {
                            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
                            holder.tvText.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
                            holder.tvText.setText("Khách hàng thanh toán ");
                            holder.tvTotal.setText("+ " + Util.FormatMoney(mData.get(position).getDouble("total")));

                        } else {
                            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.black_text_color_hint));
                            holder.tvText.setTextColor(mContext.getResources().getColor(R.color.black_text_color_hint));
                            holder.tvText.setText("Trừ tiền trả hàng ");
                            holder.tvTotal.setText("+ " + Util.FormatMoney(mData.get(position).getDouble("total")));
                        }


                    } else {
                        holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.orange_dark));
                        holder.tvText.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
                        holder.tvText.setText(String.format("Trả lại khách hàng %s", mData.get(position).getString("note").equals("") ?
                                "" : String.format("(%s)", mData.get(position).getString("note"))));
                        holder.tvTotal.setText("- " + Util.FormatMoney(mData.get(position).getDouble("total") * -1));
                    }

                    break;

            }
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerPaymentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime, tvText, tvTotal;
        private View line;


        public CustomerPaymentViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.customer_payment_item_time);
            tvTotal = (TextView) itemView.findViewById(R.id.customer_payment_item_total);
            tvText = (TextView) itemView.findViewById(R.id.customer_payment_item_content);
            line = itemView.findViewById(R.id.line_bottom);


        }

    }

    public List<BaseModel> getAllBill() {
        return mData;
    }


}
