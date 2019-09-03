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
            this.mData = groupCustomerPayment(data);

        }else {
            mData = new ArrayList<>();
            List<BaseModel> listTemp = groupCustomerPayment(data);
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
        try {
            holder.tvNumber.setText(String.valueOf(mData.size() -position));

            final BaseModel customer = new BaseModel(mData.get(position).getJsonObject("customer"));
            holder.tvsignBoard.setText(Constants.getShopName(customer.getString("shopType") ) + " " + customer.getString("signBoard"));
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

    public double sumPayments(){
        double totalPayment = 0.0;
        for (BaseModel row : mData){
            totalPayment += row.getDouble("paid");
        }
        return totalPayment;
    }

    private List<BaseModel> groupCustomerPayment(List<BaseModel> list){
        List<BaseModel> listResult = new ArrayList<>();
        for ( int i=0; i<list.size(); i++ ){
            boolean check = false;
            for (int a=0; a<listResult.size(); a++){
                if (Util.DateString(listResult.get(a).getLong("createAt")).equals(Util.DateString(list.get(i).getLong("createAt")))
                    && listResult.get(a).getString("customer").equals(list.get(i).getString("customer"))){

                    check = true;
                    break;
                }
            }



            if (!check){
                BaseModel row = new BaseModel();
                row.put("createAt", list.get(i).getLong("createAt"));
                row.put("user", list.get(i).getJsonObject("user"));
                row.put("customer", list.get(i).getJsonObject("customer"));

                double paid = list.get(i).getDouble("paid");
                for (int ii= i+1; ii<list.size(); ii++){
                    if (Util.DateString(row.getLong("createAt")).equals(Util.DateString(list.get(ii).getLong("createAt")))
                        && row.getString("customer").equals(list.get(ii).getString("customer"))){

                        paid += list.get(ii).getDouble("paid");

                    }
                }
                row.put("paid", paid);

                listResult.add(row);

            }
        }


        return listResult;
    }

}
