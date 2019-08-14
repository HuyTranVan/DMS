package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerBillsAdapter extends RecyclerView.Adapter<CustomerBillsAdapter.CustomerBillsAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBaseModel mListenerBill;
    private CustomBottomDialog.FourMethodListener mListerner;

    public interface CallbackBill {
//        void onResponse(List<BaseModel> bill, Double total, int i);
        void onResponse(BaseModel bill);
    }

    public CustomerBillsAdapter(List<BaseModel> data, CallbackBaseModel listener, CustomBottomDialog.FourMethodListener listener4) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListenerBill = listener;
        this.mListerner = listener4;

        DataUtil.sortbyStringKey("createAt", mData, true);

    }

    @Override
    public CustomerBillsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_customer_bills_item, parent, false);
        return new CustomerBillsAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerBillsAdapterViewHolder holder, final int position) {
        try {
            holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
            holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")) + " đ");
            holder.tvDebt.setText(Util.FormatMoney(mData.get(position).getDouble("debt")) + " đ");
            if (Util.isEmpty(mData.get(position).getString("note")) ){
                holder.tvNote.setVisibility(View.GONE);
                holder.rvReturn.setVisibility(View.GONE);

            }else {
                if (mData.get(position).hasKey(Constants.HAVEBILLRETURN)){
                    holder.rvReturn.setVisibility(View.VISIBLE);
                    holder.tvNote.setVisibility(View.GONE);

                    List<BaseModel> listReturn = DataUtil.array2ListBaseModel(mData.get(position).getJSONArray(Constants.HAVEBILLRETURN));
                    CustomerBillsReturnAdapter adapter = new CustomerBillsReturnAdapter(listReturn);
                    Util.createLinearRV(holder.rvReturn, adapter);
                }else {
                    holder.tvNote.setVisibility(View.VISIBLE);
                    holder.rvReturn.setVisibility(View.GONE);
                }

            }

            final List<BaseModel> listBillDetail = new ArrayList<>(DataUtil.array2ListBaseModel(new JSONArray(mData.get(position).getString("billDetails"))));
            CustomerBillsDetailAdapter adapter = new CustomerBillsDetailAdapter(listBillDetail);
            Util.createLinearRV(holder.rvBillDetail, adapter);

            JSONArray arrayBillPayment = new JSONArray(mData.get(position).getString("payments"));
            List<BaseModel> listPayment = new ArrayList<>(DataUtil.array2ListBaseModel(arrayBillPayment));

            if (mData.get(position).hasKey(Constants.PAYBYTRETURN)){
                listPayment.addAll(DataUtil.array2ListBaseModel(mData.get(position).getJSONArray(Constants.PAYBYTRETURN)));

            }

            if (listPayment.size() >0){
                holder.lnPayment.setVisibility(View.VISIBLE );
                PaymentAdapter paymentAdapter = new PaymentAdapter(listPayment);
                Util.createLinearRV(holder.rvPayment, paymentAdapter);

            }else {
                holder.lnPayment.setVisibility(View.GONE);
            }


            holder.tvIcon.setText(String.valueOf(mData.size()-position));

            holder.lnTitle.setOnClickListener(new View.OnClickListener() {
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
                                    mListenerBill.onResponse(mData.get(position));

                                }

                                @Override
                                public void Method4(Boolean four) {
                                    deleteBill(position);
                                }
                            });
                }
            });


        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerBillsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvHour, tvPay, tvDebt, tvTotal, tvNote, tvIcon;
        private RecyclerView rvBillDetail, rvPayment, rvReturn;
        private LinearLayout lnPayment, lnTitle;

        public CustomerBillsAdapterViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.bills_item_date);
            //tvPay = (TextView) itemView.findViewById(R.id.bills_item_pay);
            tvDebt = (TextView) itemView.findViewById(R.id.bills_item_debt);
            tvTotal = (TextView) itemView.findViewById(R.id.bills_item_total);
            tvIcon = (TextView) itemView.findViewById(R.id.bills_item_icon);
            rvBillDetail = (RecyclerView) itemView.findViewById(R.id.bills_item_rvproduct);
            rvPayment = (RecyclerView) itemView.findViewById(R.id.bills_item_rvpayment);
            rvReturn = (RecyclerView) itemView.findViewById(R.id.bills_item_rvreturn);
            lnPayment = (LinearLayout) itemView.findViewById(R.id.bills_item_payment_parent);
            //lnReturn = (LinearLayout) itemView.findViewById(R.id.bills_item_return_parent);
            lnTitle = (LinearLayout) itemView.findViewById(R.id.bills_item_title);
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
                        List<String> listParams = new ArrayList<>();
                        listParams.add(mData.get(currentPosition).getString("id"));
                        if (mData.get(currentPosition).hasKey(Constants.HAVEBILLRETURN)){
                            List<BaseModel> list = DataUtil.array2ListBaseModel(mData.get(currentPosition).getJSONArray(Constants.HAVEBILLRETURN));
                            for (BaseModel baseModel : list){
                                listParams.add(baseModel.getString("id"));

                            }
                        }

                        CustomerConnect.DeleteListBill(listParams, new CallbackListCustom() {
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
                    0.0,
                    new CallbackListCustom() {
                        @Override
                        public void onResponse(List result) {
                            try {
                                CustomerConnect.PostListPay(DataUtil.createListPaymentParam(new JSONObject(mData.get(currentPosition).getString("customer")).getInt("id"),
                                        result), new CallbackListCustom() {
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
        for (int i=0; i<list.size(); i++){
            list.get(i).put("quantity", mergeQuantity(list, list.get(i)));
            if (list.get(i).getInt("quantity") > 0){
                listResult.add(list.get(i));
            }

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
