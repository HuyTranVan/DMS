package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackList;
import wolve.dms.models.BaseModel;
import wolve.dms.models.BillDetail;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerPaymentAdapter extends RecyclerView.Adapter<CustomerPaymentAdapter.CustomerPaymentViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackListObject mListenerList;

    public interface CallbackListObject{
        void onResponse(List<BaseModel> listResult, Double total, int id);
    }

    public CustomerPaymentAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
//        this.mListenerList = listener;

        Collections.sort(mData, new Comparator<BaseModel>(){
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("createAt").compareToIgnoreCase(obj2.getString("createAt"));
            }
        });
        Collections.reverse(mData);

    }

    @Override
    public CustomerPaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_customer_payment_item, parent, false);
        return new CustomerPaymentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerPaymentViewHolder holder, final int position) {
        holder.tvTime.setText(Util.DateString(mData.get(position).getLong("createAt")));
        switch (mData.get(position).getString("type")){
            case Constants.BILL:
                holder.tvIcon.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dark_rounded_button));
                holder.tvPaid.setVisibility(View.INVISIBLE);
                holder.tvTotal.setVisibility(View.VISIBLE);
                holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")));

                break;

            case Constants.PAYMENT:
                holder.tvIcon.setBackground(mContext.getResources().getDrawable(R.drawable.bg_blue_rounded_button));
                holder.tvTotal.setVisibility(View.INVISIBLE);
                holder.tvPaid.setVisibility(View.VISIBLE);
                holder.tvPaid.setText(Util.FormatMoney(mData.get(position).getDouble("total")));

                break;

        }

        if (mData.size()==1){
            holder.vLineUpper.setVisibility(View.GONE);
            holder.vLineUnder.setVisibility(View.GONE);
        }else if(position ==0){
            holder.vLineUpper.setVisibility(View.GONE);
        }else if (position==mData.size()-1){
            holder.vLineUnder.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerPaymentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime, tvPaid, tvTotal;
        private TextView tvIcon;
        private View vLineUpper, vLineUnder;
        private LinearLayout lnParent;

        public CustomerPaymentViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.customer_payment_item_time);
            tvTotal = (TextView) itemView.findViewById(R.id.customer_payment_item_total);
            tvPaid = (TextView) itemView.findViewById(R.id.customer_payment_item_paid);
            vLineUnder = (View) itemView.findViewById(R.id.line_under);
            vLineUpper = (View) itemView.findViewById(R.id.line_upper);
//            lnParent = (LinearLayout) itemView.findViewById(R.id.customer_payment_item_group);
            tvIcon =  (TextView) itemView.findViewById(R.id.customer_payment_item_count);

        }

    }

    public List<BaseModel> getAllBill(){
        return mData;
    }



}
