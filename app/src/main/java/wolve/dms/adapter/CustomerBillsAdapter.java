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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CTextView;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.models.User;
import wolve.dms.utils.CustomDialog;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerBillsAdapter extends RecyclerView.Adapter<CustomerBillsAdapter.CustomerBillsAdapterViewHolder> {
    private List<Bill> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackDeleteAdapter mDelete;

    public CustomerBillsAdapter(List<Bill> data, CallbackDeleteAdapter mDelete) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mDelete = mDelete;
        Collections.reverse(mData);
    }

    @Override
    public CustomerBillsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_customer_bills_item, parent, false);
        return new CustomerBillsAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerBillsAdapterViewHolder holder, final int position) {
        try {
//            holder.lnParent.setBackground(( position % 2 ) == 0 ?  mContext.getResources().getDrawable(R.drawable.colorgrey_corner):
//                    mContext.getResources().getDrawable(R.drawable.colorwhite_bordergrey_corner));

            holder.tvDate.setText(Util.DateString(mData.get(position).getLong("updateAt")));
            holder.tvHour.setText(Util.HourString(mData.get(position).getLong("updateAt")));
            holder.tvTotal.setText("Tổng: "+ Util.FormatMoney(mData.get(position).getDouble("total")));
            holder.tvPay.setText("Trả: "+ Util.FormatMoney(mData.get(position).getDouble("paid")));
            holder.tvDebt.setText("Nợ: "+ Util.FormatMoney(mData.get(position).getDouble("debt")));

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

            holder.tvicon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(User.getCurrentUser().getString("role").equals("MANAGER")){
                        CustomDialog.alertWithCancelButton(null, "Bạn muốn xóa hóa đơn " + Util.DateString(mData.get(position).getLong("updateAt")), "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                String param = String.valueOf(mData.get(position).getInt("id"));
                                CustomerConnect.DeleteBill(param, new CallbackJSONObject() {
                                    @Override
                                    public void onResponse(JSONObject result) {
                                        mDelete.onDelete(mData.get(position).BillstoString(), mData.size() -1);
                                        mData.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                }, true);
                            }
                        });

                    }


                    return true;
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

    public class CustomerBillsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvHour, tvPay, tvDebt, tvTotal;
        private RecyclerView rvBillDetail;
        private CTextView tvicon;
        private View vLineUpper, vLineUnder;
        private LinearLayout lnParent;

        public CustomerBillsAdapterViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.bills_item_date);
            tvHour = (TextView) itemView.findViewById(R.id.bills_item_hour);
            tvPay = (TextView) itemView.findViewById(R.id.bills_item_pay);
            tvDebt = (TextView) itemView.findViewById(R.id.bills_item_debt);
            tvTotal = (TextView) itemView.findViewById(R.id.bills_item_total);
            rvBillDetail = (RecyclerView) itemView.findViewById(R.id.bills_item_rvproduct);
            vLineUnder = (View) itemView.findViewById(R.id.bills_item_under);
            vLineUpper = (View) itemView.findViewById(R.id.bills_item_upper);
            lnParent = (LinearLayout) itemView.findViewById(R.id.bills_item_content_parent);
            tvicon = (CTextView) itemView.findViewById(R.id.bills_item_icon);

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
