package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
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
        holder.text.setText(mData.get(position).getString("id") + "   " + (mData.size() - position));
        String note = Security.decrypt(mData.get(position).getString("note"));

        if (!note.equals("")){
            if (Util.isJSONObject(note)){
                //mData.get(position).put("isReturn", );
                holder.note.setText(DataUtil.formatString(note));
            }

//            else {
//                holder.note.setText(mData.get(position).getString("note"));
//            }

        }



        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerConnect.GetCustomerDetail(mData.get(position).getString("customer_id"), new CallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result) {
                        BaseModel customer = DataUtil.rebuiltCustomer(result);
                        CustomSQL.setBaseModel(Constants.CUSTOMER, customer);

                        Transaction.gotoCustomerActivity();
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true,true);
            }
        });

        holder.note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //payBill(mData.get(position));
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

    public List<BaseModel> getmData(){
        return mData;
    }

    private void payBill(BaseModel bill){
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
                if (result){


                    CustomerConnect.PostPay(param, new CallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result) {

                            Util.showToast("true");

                        }

                        @Override
                        public void onError(String error) {

                        }

                    }, true);



                }


            }
        });



    }

}
