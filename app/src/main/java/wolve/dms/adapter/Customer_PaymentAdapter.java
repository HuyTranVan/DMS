package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_PaymentAdapter extends RecyclerView.Adapter<Customer_PaymentAdapter.CustomerPaymentViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackListObject mListenerList;

    public interface CallbackListObject{
        void onResponse(List<BaseModel> listResult, Double total, int id);
    }

    public Customer_PaymentAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();

        Collections.sort(mData, new Comparator<BaseModel>(){
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("createAt").compareToIgnoreCase(obj2.getString("createAt"));
            }
        });
        Collections.reverse(mData);

    }

    public void updateData(List<BaseModel> data){
        this.mData = data;
        Collections.sort(mData, new Comparator<BaseModel>(){
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

        if (mData.get(position).hasKey("type")){
            switch (mData.get(position).getString("type")){
                case Constants.BILL:
                    holder.tvText.setText("Mua hàng ");
                    holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
                    holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")));

                    break;

                case Constants.PAYMENT:
                    holder.tvText.setText("Khách hàng thanh toán ");
                    holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
                    holder.tvTotal.setText("+ " + Util.FormatMoney(mData.get(position).getDouble("total")));

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


        public CustomerPaymentViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.customer_payment_item_time);
            tvTotal = (TextView) itemView.findViewById(R.id.customer_payment_item_total);
            tvText = (TextView) itemView.findViewById(R.id.customer_payment_item_content);


        }

    }

    public List<BaseModel> getAllBill(){
        return mData;
    }



}
