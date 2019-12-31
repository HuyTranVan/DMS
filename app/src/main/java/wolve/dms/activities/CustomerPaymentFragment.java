package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Customer_PaymentAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerPaymentFragment extends Fragment implements View.OnClickListener{
    private View view;
    private RecyclerView rvPayment;
    private Customer_PaymentAdapter adapter;

    private CustomerActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_payment,container,false);
        CustomerActivity.paymentFragment = this;

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        createRVPayment(new ArrayList<>());
    }

    private void addEvent() {
        //tvCount.setOnClickListener(this);
    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        rvPayment = (RecyclerView) view.findViewById(R.id.customer_rvpayment);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){



        }
    }

    public void updateList(){
        //adapter.updateData(convert2ListPayment(mActivity.listBills));
        adapter.updateData(createListPayment(mActivity.listBills, DataUtil.array2ListObject(mActivity.currentCustomer.getString("payments"))));
    }

    public void createRVPayment(List<BaseModel> list){
        adapter = new Customer_PaymentAdapter(list);
        Util.createLinearRV(rvPayment, adapter);

    }

    private List<BaseModel> createListPayment(List<BaseModel> listBill, List<BaseModel> listPayment){
        List<BaseModel> listResult = new ArrayList<>();
        for (int i=0; i<listBill.size(); i++){
            BaseModel objectbill = new BaseModel();
            objectbill.put("createAt", listBill.get(i).getLong("createAt"));
            objectbill.put("type", Constants.BILL);
            objectbill.put("total", listBill.get(i).getDouble("total"));

            if (listBill.get(i).getDouble("total") !=0.0){
                listResult.add(objectbill);
            }
        }

        for (int ii=0; ii<listPayment.size(); ii++){
            //if (listPayment.get(ii).getInt("payByReturn") != 1){
                BaseModel objectPay = new BaseModel();
                objectPay.put("createAt", listPayment.get(ii).getLong("createAt"));
                objectPay.put("type", Constants.PAYMENT);
                objectPay.put("total", listPayment.get(ii).getDouble("paid"));
                objectPay.put("payByReturn", listPayment.get(ii).getInt("payByReturn"));

                listResult.add(objectPay);
            //}
        }

        return listResult;

    }

    private List<BaseModel> convert2ListPayment(List<BaseModel> listBill){
        List<BaseModel> listResult = new ArrayList<>();

        try {
            for (int i=0; i<listBill.size(); i++){
                BaseModel objectbill = new BaseModel();
                objectbill.put("createAt", listBill.get(i).getLong("createAt"));
                objectbill.put("type", Constants.BILL);
                objectbill.put("total", listBill.get(i).getDouble("total"));

                if (listBill.get(i).getDouble("total") !=0.0){
                    listResult.add(objectbill);
                }


                JSONArray arrayPayment = listBill.get(i).getJSONArray("payments");
                if (arrayPayment.length() ==0){
                    if (listBill.get(i).getDouble("paid") >0 && listBill.get(i).getDouble("paid") <=listBill.get(i).getDouble("total")){
                        BaseModel objectpayment1 = new BaseModel();
                        objectpayment1.put("createAt", listBill.get(i).getLong("createAt"));
                        objectpayment1.put("type", Constants.PAYMENT);
                        objectpayment1.put("total", listBill.get(i).getDouble("paid"));

                        listResult.add(objectpayment1);
                    }

                }else {
                    for (int j=0; j<arrayPayment.length(); j++){
                        JSONObject object = arrayPayment.getJSONObject(j);
                        boolean check = false;

                        for (int ii =0; ii<listResult.size(); ii++){
//                            if (listResult.get(ii).getString("type").equals(Constants.PAYMENT) &&
//                                    listResult.get(ii).getLong("createAt") == object.getLong("createAt") ){
                            if (listResult.get(ii).getString("type").equals(Constants.PAYMENT)
                                    && Util.DateString(listResult.get(ii).getLong("createAt")).equals(Util.DateString(object.getLong("createAt")))
                                    && object.getInt("payByReturn") == 1){
                                check = true;
                                listResult.get(ii).put("total", listResult.get(ii).getDouble("total") + object.getLong("paid"));

                                break;

                            }else {
                                check = false;

                            }

                        }

                        if (!check) {
                            BaseModel objectpayment2 = new BaseModel();
                            objectpayment2.put("createAt", object.getLong("createAt"));
                            objectpayment2.put("type", Constants.PAYMENT);
                            objectpayment2.put("total", object.getLong("paid"));

                            listResult.add(objectpayment2);
                        }
                    }
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listResult;
    }

}
