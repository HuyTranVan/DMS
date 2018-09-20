package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.adapter.CustomerBillsAdapter;
import wolve.dms.adapter.ShopcartViewpagerAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackUpdateBill;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class BillDetailFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private TextView tvTitle, tvDebt, tvPaid, tvTotal;
    private RecyclerView rvBill ;

    private CustomerActivity mActivity;
    private List<Product> listProducts = new ArrayList<>();
    private CartProductDialogAdapter adapter;
    private ProductGroup productGroup;
    private ShopcartViewpagerAdapter viewpagerAdapter;
    private int currentPosition =0;
    private List<CartProductDialogAdapter> listadapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_bill_detail,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        tvTitle.setText(String.format("HÓA ĐƠN: %s",mActivity.tvTitle.getText().toString()));
        createRVProduct(mActivity.listBills);
        showOverViewDetail(Util.getTotal(mActivity.listBills));

    }

    private void addEvent() {
        btnBack.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        tvTitle = view.findViewById(R.id.header_title);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        rvBill = view.findViewById(R.id.bill_detail_rvbill);
        tvDebt = view.findViewById(R.id.bill_detail_debt);
        tvPaid = view.findViewById(R.id.bill_detail_paid);
        tvTotal = view.findViewById(R.id.bill_detail_total);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;



        }
    }


    private void createRVProduct(List<Bill> listbill){
        CustomerBillsAdapter adapter = new CustomerBillsAdapter(listbill, new CustomerBillsAdapter.CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> listResult) {
                showDialogReturnProduct(listResult);
            }
        }, new CustomBottomDialog.FourMethodListener() {
            @Override
            public void Method1(Boolean one) {
                reloadCustomer();
            }

            @Override
            public void Method2(Boolean two) {
                reloadCustomer();
            }

            @Override
            public void Method3(Boolean three) {

            }

            @Override
            public void Method4(Boolean four) {
                reloadCustomer();
            }
        });

        rvBill.setAdapter(adapter);
        rvBill.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvBill.setLayoutManager(layoutManager);
    }

    private void showDialogReturnProduct(List<BaseModel> listProductReturn){
        CustomCenterDialog.showDialogReturnProduct(mActivity.currentCustomer.getInt("id"), listProductReturn, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    Util.showToast("Trả hàng thành công");
                    reloadCustomer();
                }
            }
        });

    }

    private void reloadCustomer(){
        String param = mActivity.currentCustomer.getString("id");
        CustomerConnect.GetCustomerDetail(param, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Customer customer = new Customer(result);
                List<Bill> listBills = new ArrayList<>();
                try{
                    if (customer.getString("bills") != null) {
                        JSONArray array = new JSONArray(customer.getString("bills"));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject objectBill = array.getJSONObject(i);
                            JSONObject objectUser = objectBill.getJSONObject("user");
                            if (User.getRole().equals(Constants.ROLE_ADMIN)){
                                listBills.add(new Bill(objectBill));
                            }else {
                                if (User.getId() == objectUser.getInt("id")){
                                    listBills.add(new Bill(objectBill));
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                createRVProduct(listBills);
                mActivity.showMoneyOverview((Util.getTotal(listBills)));
                showOverViewDetail(Util.getTotal(listBills));


            }

            @Override
            public void onError(String error) {

            }
        }, true);

    }

    private void showOverViewDetail(JSONObject object){
        try {
            tvTotal.setText(String.format("Tổng: %s" ,Util.FormatMoney(object.getDouble("total"))));
            tvDebt.setText(String.format("Nợ: %s" ,Util.FormatMoney(object.getDouble("debt"))));
            tvPaid.setText(String.format("Trả: %s" ,Util.FormatMoney(object.getDouble("paid"))));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
