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
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackList;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataFilter;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerBillsAdapter extends RecyclerView.Adapter<CustomerBillsAdapter.CustomerBillsAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackListObject mListenerList;
    private CustomBottomDialog.FourMethodListener mListerner;

    public interface CallbackListObject{
        void onResponse(List<BaseModel> listResult, Double total, int id);
    }

    public CustomerBillsAdapter(List<BaseModel> data, CallbackListObject  listener, CustomBottomDialog.FourMethodListener listener4) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListenerList = listener;
        this.mListerner = listener4;

        DataFilter.sortbyKey("createAt", mData, true);

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
            if (mData.get(position).getString("note").equals("") || mData.get(position).getString("note").matches(Util.DETECT_NUMBER) ){
                holder.tvNote.setVisibility(View.GONE);
            }else {
                holder.tvNote.setVisibility(View.VISIBLE);
                holder.tvNote.setText(mData.get(position).getString("note"));
            }

            JSONArray arrayBillDetail = new JSONArray(mData.get(position).getString("billDetails"));
            final List<BaseModel> listBillDetail = new ArrayList<>();
            for (int i=0; i<arrayBillDetail.length(); i++){
                BaseModel billDetail = new BaseModel(arrayBillDetail.getJSONObject(i));
                listBillDetail.add(billDetail);
            }

            CustomerBillsDetailAdapter adapter = new CustomerBillsDetailAdapter(listBillDetail);
            Util.createLinearRV(holder.rvBillDetail, adapter);

            if (mData.get(position).getString("payments")!= null){
                JSONArray arrayBillPayment = new JSONArray(mData.get(position).getString("payments"));
                final List<JSONObject> listPayment = new ArrayList<>();
                for (int j=0; j<arrayBillPayment.length(); j++){
                    JSONObject object = arrayBillPayment.getJSONObject(j);
                    listPayment.add(object);
                }

                PaymentAdapter paymentAdapter = new PaymentAdapter(listPayment);
                Util.createLinearRV(holder.rvPayment, paymentAdapter);
            }


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
                            mContext.getString(R.string.icon_print), "In lại những hóa đơn nợ",
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
                                    mListenerList.onResponse(returnProduct(listBillDetail),mData.get(position).getDouble("total"), mData.get(position).getInt("id"));

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

    public List<BaseModel> getAllBill(){
        return mData;
    }

    private void deleteBill(final int currentPosition){
        if(User.getRole().equals("MANAGER")){
            CustomCenterDialog.alertWithCancelButton(null, String.format("Bạn muốn xóa hóa đơn %s với số tiền %s đ",Util.DateString(mData.get(currentPosition).getLong("updateAt")), Util.FormatMoney(mData.get(currentPosition).getDouble("total"))),
                    "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    if (result){
//                        String currentId = String.valueOf(mData.get(currentPosition).getInt("id"));

                        List<String> listParams = new ArrayList<>();
                        listParams = Util.arrayToList(mData.get(currentPosition).getString("idbill").split("-"));
//                    listParams.add(currentId);
//                    for (int i=0; i<mData.size(); i++){
//                        if (Util.isBillReturn(mData.get(i))){
//                            listParams.add(mData.get(i).getString("note"));
//                        }
//                    }

                        CustomerConnect.DeleteListBill(listParams, new CallbackList() {
                            @Override
                            public void onResponse(List result) {
                                Util.showToast("Xóa thành công");
                                mListerner.Method4(true);
                            }

                            @Override
                            public void onError(String error) {
                                mListerner.Method4(false);
                            }
                        }, true);
                    }

                }
            });

        }else {
            Util.showToast("Liên hệ Admin để xóa hóa đơn này");
        }
    }

    private void payBill(final int currentPosition){
        if (mData.get(currentPosition).getDouble("debt") != 0){
            List currentDebt = new ArrayList();
            currentDebt.add(mData.get(currentPosition));

            CustomCenterDialog.showDialogPayment(String.format("THANH TOÁN HÓA ĐƠN %s", Util.DateString(mData.get(currentPosition).getLong("createAt"))),
                    currentDebt,
                    new CallbackList() {
                        @Override
                        public void onResponse(List result) {
                            try {
                                CustomerConnect.PostListPay(DataFilter.createListPaymentParam(new JSONObject(mData.get(currentPosition).getString("customer")).getInt("id"),
                                        result), new CallbackList() {
                                    @Override
                                    public void onResponse(List result) {
                                        mListerner.Method1(true);

                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                }, true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(String error) {
                            mListerner.Method1(false);
                        }
                    });


        }else {
            Util.showToast("Hóa đơn này đã thanh toán");
        }
    }

    private List<BaseModel> returnProduct(List<BaseModel> list){
        List<BaseModel> listResult = new ArrayList<>();
        try {
            for (int i=0; i<list.size(); i++){
                list.get(i).put("quantity", mergeQuantity(list, list.get(i)));
                if (list.get(i).getInt("quantity") > 0){
                    listResult.add(list.get(i));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listResult;

    }


    private int mergeQuantity(List<BaseModel> list, BaseModel billdetail){
        int count = billdetail.getInt("quantity");

        for (int i=0; i<list.size(); i++){
            if (!list.get(i).getString("id").equals(billdetail.getString("id"))){
                Double netPrice1 = billdetail.getDouble("unitPrice") - billdetail.getDouble("discount");
                Double netPrice2 = list.get(i).getDouble("unitPrice") - list.get(i).getDouble("discount");

                if (billdetail.getInt("productId") == list.get(i).getInt("productId")
                        && list.get(i).getInt("quantity")<0
                        && netPrice1.equals( netPrice2)){
                    count = count + list.get(i).getInt("quantity");

                }

            }
        }

        return count;
    }

}
