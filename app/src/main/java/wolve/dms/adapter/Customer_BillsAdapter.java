package wolve.dms.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_BillsAdapter extends RecyclerView.Adapter<Customer_BillsAdapter.CustomerBillsAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBaseModel mListener;

    public Customer_BillsAdapter(List<BaseModel> data, CallbackBaseModel listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

        DataUtil.sortbyStringKey("createAt", mData, true);

    }

    public void updateData(List<BaseModel> data){
        this.mData = data;
        DataUtil.sortbyStringKey("createAt", mData, true);
        notifyDataSetChanged();
    }

    @Override
    public CustomerBillsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_customer_bills_item, parent, false);
        return new CustomerBillsAdapterViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final CustomerBillsAdapterViewHolder holder, final int position) {
        holder.lnCover.setVisibility(mData.get(position).hasKey(Constants.TEMPBILL) && mData.get(position).getBoolean(Constants.TEMPBILL)? View.VISIBLE: View.GONE);
        holder.btnDelete.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN)||
                User.getId() == mData.get(position).getBaseModel("user").getInt("id")
                ? View.VISIBLE:View.GONE);

        holder.btnConfirm.setVisibility(CustomSQL.getLong(Constants.CHECKIN_TIME) != 0 ? View.VISIBLE : View.GONE);
//        holder.btnConfirm.setVisibility(View.GONE);
//        holder.btnDelete.setVisibility(View.GONE);

        holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
        holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")) + " đ");
        holder.tvDebt.setText(Util.FormatMoney(mData.get(position).getDouble("debt")) + " đ");
        if ( mData.get(position).getDouble("debt") >0.0 ){
            holder.tvDebt.setBackground(mContext.getDrawable(R.drawable.btn_round_border_red));
            holder.tvDebt.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            holder.tvDebt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payBill(position);

                }
            });
        }else {
            holder.tvDebt.setBackground(null);
            holder.tvDebt.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            holder.tvDebt.setOnClickListener(null);

        }
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>();
                list.add("Trả hàng");

                if (User.getRole().equals(Constants.ROLE_ADMIN)){
                    list.add("Xóa hóa đơn");
                }

                CustomDropdow.createDropdown(holder.btnMenu,
                        list,
                        new CallbackString() {
                            @Override
                            public void Result(String s) {
                                if (s.equals(list.get(0))){
                                    BaseModel respone = new BaseModel();
                                    respone.put(Constants.TYPE, Constants.BILL_RETURN);
                                    respone.putBaseModel(Constants.RESULT, mData.get(position));
                                    mListener.onResponse(respone);

                                }else if (s.equals(list.get(1))){
                                    deleteBill(position);

                                }
                            }
                        });


            }
        });

        if (Util.isEmpty(mData.get(position).getString("note")) ){
            holder.rvReturn.setVisibility(View.GONE);
            holder.tvDeliver.setVisibility(View.GONE);

        }else {
            if (mData.get(position).hasKey(Constants.HAVEBILLRETURN)){
                holder.rvReturn.setVisibility(View.VISIBLE);
                //holder.tvNote.setVisibility(View.GONE);

                List<BaseModel> listReturn = new ArrayList<>(DataUtil.array2ListObject(mData.get(position).getString(Constants.HAVEBILLRETURN)));
                Customer_BillsReturnAdapter adapter = new Customer_BillsReturnAdapter(listReturn);
                Util.createLinearRV(holder.rvReturn, adapter);

            }else {
                holder.rvReturn.setVisibility(View.GONE);

            }

            if (mData.get(position).hasKey(Constants.DELIVER_BY)){
                String s = mData.get(position).getString(Constants.DELIVER_BY);
                holder.tvDeliver.setVisibility(View.VISIBLE);
                holder.tvDeliver.setText(String.format("Giao bởi: %s vào %s", mData.get(position).getBaseModel(Constants.DELIVER_BY).getString("displayName"),
                        mData.get(position).getBaseModel(Constants.DELIVER_BY).getString("currentTime")));

            }else {
                holder.tvDeliver.setVisibility(View.GONE);

            }

        }

        final List<BaseModel> listBillDetail = new ArrayList<>(DataUtil.array2ListObject(mData.get(position).getString("billDetails")));
        Customer_BillsDetailAdapter adapter = new Customer_BillsDetailAdapter(listBillDetail);
        Util.createLinearRV(holder.rvBillDetail, adapter);

        List<BaseModel> listPayment = new ArrayList<>(DataUtil.array2ListObject(mData.get(position).getString("payments")));

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

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBill(position);
            }
        });

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseModel respone = new BaseModel();
                respone.put(Constants.TYPE, Constants.BILL_DELIVER);
                respone.putBaseModel(Constants.RESULT, mData.get(position));
                mListener.onResponse(respone);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerBillsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvHour, tvPay, tvDebt, tvTotal, tvIcon, tvDeliver;
        private CTextIcon btnMenu;
        private RecyclerView rvBillDetail, rvPayment, rvReturn;
        private LinearLayout lnPayment, lnCover;
        private RelativeLayout lnTitle;
        private Button btnDelete, btnConfirm;

        public CustomerBillsAdapterViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.bills_item_date);
            btnMenu = itemView.findViewById(R.id.bills_item_delete);
            tvDebt = (TextView) itemView.findViewById(R.id.bills_item_debt);
            tvTotal = (TextView) itemView.findViewById(R.id.bills_item_total);
            tvIcon = (TextView) itemView.findViewById(R.id.bills_item_icon);
            rvBillDetail = (RecyclerView) itemView.findViewById(R.id.bills_item_rvproduct);
            rvPayment = (RecyclerView) itemView.findViewById(R.id.bills_item_rvpayment);
            rvReturn = (RecyclerView) itemView.findViewById(R.id.bills_item_rvreturn);
            lnPayment = (LinearLayout) itemView.findViewById(R.id.bills_item_payment_parent);
            //lnReturn = (LinearLayout) itemView.findViewById(R.id.bills_item_return_parent);
            lnTitle =  (RelativeLayout) itemView.findViewById(R.id.bills_item_title);
            lnCover = itemView.findViewById(R.id.bills_item_cover);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            tvDeliver = itemView.findViewById(R.id.bills_item_deliver_name);


        }

    }

    public List<BaseModel> getAllBill(){
        return mData;
    }

    private void deleteBill(final int currentPosition){
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
                            BaseModel respone = new BaseModel();
                            respone.put(Constants.TYPE, Constants.BILL_DELETE);
                            mListener.onResponse(respone);

                        }

                        @Override
                        public void onError(String error) {
                            Util.showSnackbarError(error);
                            mListener.onError();
                        }
                    }, true);
                }

            }
        });

    }

    private void payBill(final int currentPosition){
        if (mData.get(currentPosition).getDouble("debt") != 0){
            List currentDebt = new ArrayList();
            currentDebt.add(mData.get(currentPosition));
            Log.e("reasdfasdf", mData.get(currentPosition).BaseModelstoString());

            CustomCenterDialog.showDialogPayment(String.format("THANH TOÁN HÓA ĐƠN %s", Util.DateString(mData.get(currentPosition).getLong("createAt"))),
                    currentDebt,
                    0.0,
                    true,
                    new CallbackListCustom() {
                        @Override
                        public void onResponse(List result) {

                            try {
                                CustomerConnect.PostListPay(DataUtil.createListPaymentParam(new JSONObject(mData.get(currentPosition).getString("customer")).getInt("id"),
                                        result), new CallbackListCustom() {
                                    @Override
                                    public void onResponse(List result) {

                                        Util.showToast("Thanh toán thành công");
                                        BaseModel respone = new BaseModel();
                                        respone.put(Constants.TYPE, Constants.BILL_PAY);
                                        mListener.onResponse(respone);

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
                            Util.showSnackbarError(error);
                            mListener.onError();
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
