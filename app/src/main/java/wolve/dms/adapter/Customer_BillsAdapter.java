package wolve.dms.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_BillsAdapter extends RecyclerView.Adapter<Customer_BillsAdapter.CustomerBillsAdapterViewHolder> implements Filterable {
    private List<BaseModel> mData = new ArrayList<>();
    private List<BaseModel> baseData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBaseModel mListener;
    private CallbackInt countListener;

    public Customer_BillsAdapter(List<BaseModel> data, CallbackBaseModel listener, CallbackInt numberlistener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.baseData = data;
        this.mData = baseData;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.countListener = numberlistener;

        DataUtil.sortbyStringKey("createAt", mData, true);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mData = baseData;

                } else {
                    BaseModel contentObj = new BaseModel(charString);
                    List<BaseModel> listTemp = new ArrayList<>();
                    for (BaseModel row : baseData) {
                        if (row.getLong("createAt") >= contentObj.getLong("from") &&  row.getLong("createAt") <= contentObj.getLong("to")){
                            listTemp.add(row);
                        }
                    }

                    mData = listTemp;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (ArrayList<BaseModel>) filterResults.values;
                countListener.onResponse(mData.size());
                notifyDataSetChanged();
            }
        };
    }

    public void updateData(List<BaseModel> data){
        this.baseData = data;
        this.mData = baseData;
        DataUtil.sortbyStringKey("createAt", mData, true);
        notifyDataSetChanged();
    }

    public void updateTempBill(){
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
        holder.lnCover.setVisibility(mData.get(position).isNull(Constants.DELIVER_BY)? View.VISIBLE: View.GONE);
        holder.btnDelete.setVisibility(User.getCurrentRoleId() == Constants.ROLE_ADMIN||
                User.getId() == mData.get(position).getInt("user_id")
                ? View.VISIBLE:View.GONE);

        holder.btnConfirm.setVisibility(CustomSQL.getLong(Constants.CURRENT_DISTANCE) < 500 ? View.VISIBLE : View.GONE);

        holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
        holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("total")) + " đ");
        holder.tvDebt.setText(Util.FormatMoney(mData.get(position).getDouble("debt")) + " đ");
        if ( mData.get(position).getDouble("debt") >0.0 ){
            holder.tvDebt.setBackground(mContext.getDrawable(R.drawable.btn_round_transparent_border_red));
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


        List<BaseModel> listReturn = new ArrayList<>(DataUtil.array2ListObject(mData.get(position).getString("billReturn")));
        Customer_BillsReturnAdapter adapterReturn = new Customer_BillsReturnAdapter(listReturn);
        Util.createLinearRV(holder.rvReturn, adapterReturn);

        if (!mData.get(position).isNull(Constants.DELIVER_BY)){
            holder.tvDeliver.setVisibility(View.VISIBLE);
            holder.tvDeliver.setText(String.format("%s giao hàng %s",
                    mData.get(position).getBaseModel("deliverByObject").getString("displayName"),
                    mData.get(position).getLong("createAt").equals(mData.get(position).getLong("deliverTime"))? "" : Util.DateHourString(mData.get(position).getLong("deliverTime"))));

        }else {
            holder.tvDeliver.setVisibility(View.GONE);
        }


        final List<BaseModel> listBillDetail = new ArrayList<>(DataUtil.array2ListObject(mData.get(position).getString("billDetails")));
        Customer_BillsDetailAdapter adapterDetail = new Customer_BillsDetailAdapter(listBillDetail);
        Util.createLinearRV(holder.rvBillDetail, adapterDetail);

        List<BaseModel> listPayment = new ArrayList<>(DataUtil.array2ListObject(mData.get(position).getString("payments")));
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

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>();
                list.add("Trả hàng");

                if (User.getCurrentRoleId()==Constants.ROLE_ADMIN){
                    list.add("Xóa hóa đơn");
                }

                CustomDropdow.createDropdown(holder.btnMenu, list, new CallbackString() {
                    @Override
                    public void Result(String s) {
                        if (s.equals(list.get(0))){
                            BaseModel respone = new BaseModel();
                            respone.put(Constants.TYPE, Constants.BILL_RETURN);
                            respone.putBaseModel(Constants.RESULT, mData.get(position));
                            mListener.onResponse(respone);

                        }else if (s.equals(list.get(1))){
                            if (listPayment.size() >0 ){
                                Util.showToast("Không thể xóa hóa đơn có phát sinh thanh toán ");
                            }else if (listReturn.size() >0){
                                Util.showToast("Không thể xóa hóa đơn có phát sinh trả hàng ");
                            }else {
                                deleteBill(position);
                            }


                        }
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerBillsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvHour, tvPay, tvDebt, tvTotal, tvIcon, tvDeliver;
        private CTextIcon btnMenu, btnDelete;
        private RecyclerView rvBillDetail, rvPayment, rvReturn;
        private LinearLayout lnPayment;
        private RelativeLayout lnTitle,lnCover ;
        private Button btnConfirm;

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
                            if (result.size() >0){
                                Util.showToast("Xóa thành công");
                                BaseModel respone = new BaseModel();
                                respone.put(Constants.TYPE, Constants.BILL_DELETE);
                                mListener.onResponse(respone);

                            }else {
                                Util.showSnackbarError("Lỗi");
                            }

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

            CustomCenterDialog.showDialogPayment(String.format("THANH TOÁN HÓA ĐƠN %s", Util.DateString(mData.get(currentPosition).getLong("createAt"))),
                    currentDebt,
                    0.0,
                    true,
                    new CallbackListCustom() {
                        @Override
                        public void onResponse(List result) {

                            try {
                                CustomerConnect.PostListPay(DataUtil.createListPaymentParam(new JSONObject(mData.get(currentPosition).getString("customer")).getInt("id"),
                                        result,false), new CallbackListCustom() {
                                    @Override
                                    public void onResponse(List result) {

                                        //Util.showToast("Thanh toán thành công");
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
