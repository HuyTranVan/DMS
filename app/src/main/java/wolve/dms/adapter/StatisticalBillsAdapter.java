package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Customer;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class StatisticalBillsAdapter extends RecyclerView.Adapter<StatisticalBillsAdapter.StatisticalBillsViewHolder> {
    private List<Bill> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public StatisticalBillsAdapter(List<Bill> data , CallbackString callbackString) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = callbackString;

        Collections.sort(mData, new Comparator<Bill>(){
            @Override
            public int compare(Bill lhs, Bill rhs) {
                return lhs.getDouble("createAt").compareTo(rhs.getDouble("createAt"));
            }
        });
        Collections.reverse(mData);
    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_bills_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        try {
//            holder.lnParent.setBackground(( position % 2 ) == 0 ?  mContext.getResources().getDrawable(R.drawable.colorgrey_corner):
//                    mContext.getResources().getDrawable(R.drawable.colorwhite_bordergrey_corner));

            final Customer customer = new Customer(mData.get(position).getJsonObject("customer"));

            holder.tvDate.setText(Util.DateString(mData.get(position).getLong("createAt")));
            holder.tvHour.setText(Util.HourString(mData.get(position).getLong("createAt")));
            holder.tvTotal.setText("Tổng: "+ Util.FormatMoney(mData.get(position).getDouble("total")));
            holder.tvPay.setText("Trả: "+ Util.FormatMoney(mData.get(position).getDouble("paid")));
            holder.tvDebt.setText("Nợ: "+ Util.FormatMoney(mData.get(position).getDouble("debt")));
            holder.tvNumber.setText(String.valueOf(position +1));
            holder.tvsignBoard.setText(Constants.getShopInfo(customer.getString("shopType") , null) + " " + customer.getString("signBoard"));
            holder.tvDistrict.setText(customer.getString("street") + " - " + customer.getString("district"));
            holder.tvUser.setText(String.format("Nhân viên: %s",mData.get(position).getJsonObject("user").getString("displayName")));

            JSONArray arrayBillDetail = new JSONArray(mData.get(position).getString("billDetails"));
            List<BillDetail> listBillDetail = new ArrayList<>();
            for (int i=0; i<arrayBillDetail.length(); i++){
                BillDetail billDetail = new BillDetail(arrayBillDetail.getJSONObject(i));
                listBillDetail.add(billDetail);
            }
            CustomerBillsDetailAdapter adapter = new CustomerBillsDetailAdapter(listBillDetail);
            adapter.notifyDataSetChanged();
            holder.rvBillDetail.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            holder.rvBillDetail.setLayoutManager(linearLayoutManager);

            if (mData.size()==1){
                holder.vLineUpper.setVisibility(View.GONE);
                holder.vLineUnder.setVisibility(View.GONE);
            }else if(position ==0){
                holder.vLineUpper.setVisibility(View.GONE);
            }else if (position==mData.size()-1){
                holder.vLineUnder.setVisibility(View.GONE);
            }

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
        private TextView tvDate, tvHour, tvPay, tvDebt, tvTotal, tvNumber, tvsignBoard, tvDistrict, tvUser;
        private RecyclerView rvBillDetail;
        private View vLineUpper, vLineUnder;
        private LinearLayout lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.statistical_bills_item_date);
            tvHour = (TextView) itemView.findViewById(R.id.statistical_bills_item_hour);
            tvPay = (TextView) itemView.findViewById(R.id.statistical_bills_item_pay);
            tvDebt = (TextView) itemView.findViewById(R.id.statistical_bills_item_debt);
            tvTotal = (TextView) itemView.findViewById(R.id.statistical_bills_item_total);
            rvBillDetail = (RecyclerView) itemView.findViewById(R.id.statistical_bills_item_rvproduct);
            vLineUnder = (View) itemView.findViewById(R.id.statistical_bills_item_under);
            vLineUpper = (View) itemView.findViewById(R.id.statistical_bills_item_upper);
            lnParent = (LinearLayout) itemView.findViewById(R.id.statistical_bills_item_content_parent);
            tvNumber = (TextView) itemView.findViewById(R.id.statistical_bills_item_number);
            tvsignBoard = (TextView) itemView.findViewById(R.id.statistical_bills_item_signboard);
            tvDistrict = (TextView) itemView.findViewById(R.id.statistical_bills_item_district);
            tvUser = itemView.findViewById(R.id.statistical_bills_item_user);
        }

    }

    public List<Bill> getAllBill(){
        return mData;
    }

//    public Double getTotalMoney(){
//        Double money =0.0;
//        for (int i=0; i<mData.size(); i++){
//            money += mData.get(i).getDouble("total");
//        }
//        return money;
//    }

}
