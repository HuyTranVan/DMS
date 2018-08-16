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
import java.util.Comparator;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.callback.CallbackUpdateBill;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerBillsAdapter extends RecyclerView.Adapter<CustomerBillsAdapter.CustomerBillsAdapterViewHolder> {
    private List<Bill> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackDeleteAdapter mDelete;
    private CallbackUpdateBill mUpdate;

    public CustomerBillsAdapter(List<Bill> data, CallbackDeleteAdapter mDelete, CallbackUpdateBill mUpdate) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mDelete = mDelete;
        this.mUpdate = mUpdate;
        Collections.sort(mData, new Comparator<Bill>(){
            public int compare(Bill obj1, Bill obj2) {
                return obj1.getString("createAt").compareToIgnoreCase(obj2.getString("createAt"));
            }
        });
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

            holder.tvDate.setText(Util.DateString(mData.get(position).getLong("createAt")));
            holder.tvHour.setText(Util.HourString(mData.get(position).getLong("createAt")));
            holder.tvTotal.setText("Tổng: "+ Util.FormatMoney(mData.get(position).getDouble("total")));
            holder.tvPay.setText("Trả: "+ Util.FormatMoney(mData.get(position).getDouble("paid")));
            holder.tvDebt.setText("Nợ: "+ Util.FormatMoney(mData.get(position).getDouble("debt")));
            if (mData.get(position).getString("note").equals("")){
                holder.tvNote.setVisibility(View.GONE);
            }else {
                holder.tvNote.setVisibility(View.VISIBLE);
                holder.tvNote.setText(mData.get(position).getString("note"));
            }

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
                    if(User.getRole().equals("MANAGER")){
                        CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa hóa đơn " + Util.DateString(mData.get(position).getLong("updateAt")), "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                String param = String.valueOf(mData.get(position).getInt("id"));
                                CustomerConnect.DeleteBill(param, new CallbackJSONObject() {
                                    @Override
                                    public void onResponse(JSONObject result) {
                                        mDelete.onDelete(mData.get(position).BillstoString(), position);
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

            holder.lnParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mData.get(position).getDouble("debt") != 0){
                        CustomCenterDialog.showDialogInputPaid("Nhập số tiền khách trả", "Nợ còn lại", mData.get(position).getDouble("debt"), new CallbackPayBill() {
                            @Override
                            public void OnRespone(Double total, Double pay) {
                                try {
                                    JSONObject params = new JSONObject();
//                                    params.put("createAt",mData.get(position).getLong("createAt"));
//                                    params.put("updateAt",mData.get(position).getLong("updateAt"));
                                    params.put("debt", mData.get(position).getDouble("total") - mData.get(position).getDouble("paid") - pay);
                                    params.put("total", mData.get(position).getDouble("total"));
                                    params.put("paid", mData.get(position).getDouble("paid") + pay);
                                    params.put("id", mData.get(position).getInt("id"));
                                    params.put("customerId", new JSONObject(mData.get(position).getString("customer")).getString("id"));
                                    params.put("distributorId", Distributor.getDistributorId());
                                    params.put("userId", User.getUserId());
//                            params.put("note", "test .hoa");
                                    if (mData.get(position).getString("note").equals("")){
                                        params.put("note",String.format("%s tra %s", Util.CurrentMonthYearHour() , Util.FormatMoney(pay)));
                                    }else {
                                        params.put("note", mData.get(position).getString("note") + String.format("\n%s tra %s", Util.CurrentMonthYearHour() , Util.FormatMoney(pay)));
                                    }


                                    params.put("billDetails", null);
                                    CustomerConnect.PostBill(params.toString(), new CallbackJSONObject() {
                                        @Override
                                        public void onResponse(JSONObject result) {
                                            //get customer detail from serer
                                            try {
                                                mData.get(position).put("debt", result.getDouble("debt"));
                                                mData.get(position).put("total", result.getDouble("total"));
                                                mData.get(position).put("paid", result.getDouble("paid"));
                                                mData.get(position).put("note", result.getString("note"));
                                                notifyItemChanged(position);

                                                mUpdate.onUpdate(mData.get(position), position);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            String s = result.toString();

                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    }, true);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });




                    }
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
        private TextView tvDate, tvHour, tvPay, tvDebt, tvTotal, tvNote;
        private RecyclerView rvBillDetail;
        private CTextIcon tvicon;
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
            tvicon = (CTextIcon) itemView.findViewById(R.id.bills_item_icon);
            tvNote = itemView.findViewById(R.id.bills_item_note);

        }

    }

    public List<Bill> getAllBill(){
        return mData;
    }


}
