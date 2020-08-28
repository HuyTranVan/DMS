package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ProductGroupAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public TestAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;

        //DataUtil.sortProductGroup(mData, false);
    }

    @Override
    public ProductGroupAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_test_item, parent, false);
        return new ProductGroupAdapterViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ProductGroupAdapterViewHolder holder, final int position) {
        holder.text.setText(String.format("%d %s (%s %s) _   %d",
                mData.get(position).getInt("id"),
                mData.get(position).getString("signBoard"),
                mData.get(position).getString("street"),
                mData.get(position).getString("district"),
                (mData.size() - position)));

        //BaseModel object = Util.getTotal(mData.get(position).getList(Constants.BILLS));

        List<BaseModel> listBill = mData.get(position).getList(Constants.BILLS);
        double debt = 0.0;
        String user = "";
        for (BaseModel baseModel : listBill) {
            if (baseModel.getDoubleValue("debt") > 0) {
                debt += baseModel.getDouble("debt");

                user = user + " " + baseModel.getInt("user_id");


            }


        }

//        mData.get(position).put("debt", debt);
//        mData.get(position).put("user_id", listBill.get(listBill.size()-1).getInt("user_id"));

        String note = String.format("Nợ %s\n%s", Util.FormatMoney(debt), user);
        holder.note.setText(note);


//        String note = Security.decrypt(mData.get(position).getString("note"));

//        if (!note.equals("")){
//            if (Util.isJSONObject(note)){
//                //mData.get(position).put("isReturn", );
//                holder.note.setText(DataUtil.formatString(note));
//            }
//
////            else {
////                holder.note.setText(mData.get(position).getString("note"));
////            }
//
//        }


        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CustomerConnect.GetCustomerDetail(mData.get(position).getString("id"), new CallbackCustom() {
//                    @Override
//                    public void onResponse(BaseModel result) {
//                        BaseModel customer = DataUtil.rebuiltCustomer(result, false);
//                        CustomSQL.setBaseModel(Constants.CUSTOMER, customer);
//
//                        Transaction.gotoCustomerActivity();
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, true, true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductGroupAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView text, note;
        //private LinearLayout lnParent;

        public ProductGroupAdapterViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.name);
            note = (TextView) itemView.findViewById(R.id.note);
            //lnParent = (LinearLayout) itemView.findViewById(R.id.productgroup_item_parent);
        }

    }

    public List<BaseModel> getmData() {
        return mData;
    }

    private void payBill(BaseModel bill) {
        BaseModel note = new BaseModel(Security.decrypt(bill.getString("note")));
        BaseModel objectPay = DataUtil.array2ListObject(note.getString("havePaymentReturn")).get(0);
        //BaseModel payXXX = new BaseModel(arrayPay.getJSONObject(0))

        String param = String.format("createAt=%d&customerId=%d&paid=%s&billId=%d&userId=%d&note=%s&payByReturn=%d",
                objectPay.getLong("createAt"),
                bill.getInt("customer_id"),
                objectPay.getDouble("paid"),
                bill.getInt("id"),
                objectPay.getBaseModel("user").getInt("id"),
                "",
                1);


        CustomCenterDialog.alertWithCancelButton("Trả tiền", param, "yes", "cancel", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {


//                    CustomerConnect.PostPay(param, new CallbackCustom() {
//                        @Override
//                        public void onResponse(BaseModel result) {
//
//                            Util.showToast("true");
//
//                        }
//
//                        @Override
//                        public void onError(String error) {
//
//                        }
//
//                    }, true);


                }


            }
        });


    }

    public List<BaseModel> getDebtData() {
        List<BaseModel> list = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {
            BaseModel newCus = new BaseModel();


            List<BaseModel> listBill = new ArrayList<>(mData.get(i).getList(Constants.BILLS));
            double debt = 0.0;
            String user = "";
            for (BaseModel baseModel : listBill) {
                if (baseModel.getDouble("debt") > 0.0) {
                    debt += baseModel.getDouble("debt");

                    user = user + " " + baseModel.getInt("user_id");


                }


            }

            newCus.put("id", mData.get(i).getInt("id"));
            newCus.put("distributor_id", mData.get(i).getInt("distributor_id"));
            newCus.put("debt", debt);
            newCus.put("user_id", listBill.get(listBill.size() - 1).getInt("user_id"));

            list.add(newCus);

        }


        return list;

    }


}
