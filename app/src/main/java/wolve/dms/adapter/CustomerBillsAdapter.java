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
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerBillsAdapter extends RecyclerView.Adapter<CustomerBillsAdapter.CustomerBillsAdapterViewHolder> {
    private List<Bill> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackListObject mListenerList;
    private CustomBottomDialog.FourMethodListener mListerner;

    public interface CallbackListObject{
        void onResponse(List<BaseModel> listResult);
    }

    public CustomerBillsAdapter(List<Bill> data, CallbackListObject  listener, CustomBottomDialog.FourMethodListener listener4) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListenerList = listener;
        this.mListerner = listener4;
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
            final List<BillDetail> listBillDetail = new ArrayList<>();
            for (int i=0; i<arrayBillDetail.length(); i++){
                BillDetail billDetail = new BillDetail(arrayBillDetail.getJSONObject(i));
                listBillDetail.add(billDetail);
            }

            CustomerBillsDetailAdapter adapter = new CustomerBillsDetailAdapter(listBillDetail);
            Util.createLinearRV(holder.rvBillDetail, adapter);

//            JSONArray arrayBillPayment = new JSONArray(mData.get(position).getString("payments"));
            final List<JSONObject> listPayment = new ArrayList<>();
//            for (int j=0; j<arrayBillPayment.length(); j++){
//                JSONObject object = arrayBillPayment.getJSONObject(j);
//                listPayment.add(object);
//            }

            PaymentAdapter paymentAdapter = new PaymentAdapter(listPayment);
            Util.createLinearRV(holder.rvPayment, paymentAdapter);


            if (mData.size()==1){
                holder.vLineUpper.setVisibility(View.GONE);
                holder.vLineUnder.setVisibility(View.GONE);
            }else if(position ==0){
                holder.vLineUpper.setVisibility(View.GONE);
            }else if (position==mData.size()-1){
                holder.vLineUnder.setVisibility(View.GONE);
            }

            holder.tvIcon.setText(String.valueOf(mData.size()-position));

            holder.tvIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomBottomDialog.choiceFourOption(mContext.getString(R.string.icon_money), "Thanh toán hóa đơn",
                            mContext.getString(R.string.icon_print), "In lại hóa đơn",
                            mContext.getString(R.string.icon_return), "Thu lại hàng",
                            mContext.getString(R.string.icon_delete), "Xóa hóa đơn", new CustomBottomDialog.FourMethodListener() {
                                @Override
                                public void Method1(Boolean one) {
                                    payBill(position);
                                }

                                @Override
                                public void Method2(Boolean two) {
                                    mListerner.Method2(true);
                                }

                                @Override
                                public void Method3(Boolean three) {
                                    mListenerList.onResponse(returnProduct(listBillDetail));
                                }

                                @Override
                                public void Method4(Boolean four) {
                                    deleteBill(position);
                                }
                            });
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
        private RecyclerView rvBillDetail, rvPayment;
        private TextView tvIcon;
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
            rvPayment = (RecyclerView) itemView.findViewById(R.id.bills_item_rvpayment);
            vLineUnder = (View) itemView.findViewById(R.id.bills_item_under);
            vLineUpper = (View) itemView.findViewById(R.id.bills_item_upper);
            lnParent = (LinearLayout) itemView.findViewById(R.id.bills_item_content_parent);
            tvIcon =  (TextView) itemView.findViewById(R.id.bills_item_icon);
            tvNote = itemView.findViewById(R.id.bills_item_note);

        }

    }

    public List<Bill> getAllBill(){
        return mData;
    }

    private void deleteBill(final int currentPosition){
        if(User.getRole().equals("MANAGER")){
            CustomCenterDialog.alertWithCancelButton(null, String.format("Bạn muốn xóa hóa đơn %s với số tiền %s đ",Util.DateString(mData.get(currentPosition).getLong("updateAt")), Util.FormatMoney(mData.get(currentPosition).getDouble("total"))),
                    "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    String param = String.valueOf(mData.get(currentPosition).getInt("id"));
                    CustomerConnect.DeleteBill(param, new CallbackJSONObject() {
                        @Override
                        public void onResponse(JSONObject result) {
                            //mDelete.onDelete(mData.get(currentPosition).BillstoString(), currentPosition);
                            Util.showToast("Xóa thành công");
                            mListerner.Method4(true);
//                            notifyDataSetChanged();

                        }

                        @Override
                        public void onError(String error) {
                            mListerner.Method4(false);
                        }
                    }, true);
                }
            });

        }
    }

    private void payBill(final int currentPosition){
        if (mData.get(currentPosition).getDouble("debt") != 0){
            CustomCenterDialog.showDialogInputPaid("Nhập số tiền khách trả", "Nợ còn lại", mData.get(currentPosition).getDouble("debt"), new CallbackPayBill() {
                @Override
                public void OnRespone(Double total, Double pay) {
                    try {
                        JSONObject params = new JSONObject();
//                                    params.put("createAt",mData.get(position).getLong("createAt"));
//                                    params.put("updateAt",mData.get(position).getLong("updateAt"));
                        params.put("debt", mData.get(currentPosition).getDouble("total") - mData.get(currentPosition).getDouble("paid") - pay);
                        params.put("total", mData.get(currentPosition).getDouble("total"));
                        params.put("paid", mData.get(currentPosition).getDouble("paid") + pay);
                        params.put("id", mData.get(currentPosition).getInt("id"));
                        params.put("customerId", new JSONObject(mData.get(currentPosition).getString("customer")).getString("id"));
                        params.put("distributorId", Distributor.getDistributorId());
                        params.put("userId", User.getUserId());
//                            params.put("note", "test .hoa");
                        if (mData.get(currentPosition).getString("note").equals("")){
                            params.put("note",String.format("%s tra %s", Util.CurrentMonthYearHour() , Util.FormatMoney(pay)));
                        }else {
                            params.put("note", mData.get(currentPosition).getString("note") + String.format("\n%s tra %s", Util.CurrentMonthYearHour() , Util.FormatMoney(pay)));
                        }

                        params.put("billDetails", null);
                        CustomerConnect.PostBill(params.toString(), new CallbackJSONObject() {
                            @Override
                            public void onResponse(JSONObject result) {
                                mListerner.Method1(true);

                                //get customer detail from serer
//                                try {
//                                    mData.get(currentPosition).put("debt", result.getDouble("debt"));
//                                    mData.get(currentPosition).put("total", result.getDouble("total"));
//                                    mData.get(currentPosition).put("paid", result.getDouble("paid"));
//                                    mData.get(currentPosition).put("note", result.getString("note"));
//                                    notifyItemChanged(currentPosition);
//
////                                    mUpdate.onUpdate(mData.get(currentPosition), currentPosition);
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }


                            }

                            @Override
                            public void onError(String error) {
                                mListerner.Method1(false);
                            }
                        }, true);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            Util.showToast("Hóa đơn này đã thanh toán");
        }
    }

    private List<BaseModel> returnProduct(List<BillDetail> list){
        List<BaseModel> listResult = new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            listResult.add(list.get(i));
        }
        return listResult;

    }

}
