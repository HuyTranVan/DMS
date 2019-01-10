package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.adapter.CustomerBillsAdapter;
import wolve.dms.adapter.CustomerPaymentAdapter;
import wolve.dms.adapter.StatisticalProductGroupAdapter;
import wolve.dms.adapter.ViewpagerBillDetailAdapter;
import wolve.dms.adapter.ViewpagerShopcartAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Customer;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class BillDetailFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tvTitle, tvDebt, tvPaid, tvTotal;
//    private RecyclerView rvBill ;
//    private RadioGroup rgFilter;
//    private RadioButton rdBill, rdPayment, rdProduct;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    private CustomerActivity mActivity;
    private ViewpagerBillDetailAdapter viewpagerAdapter;
    private List<RecyclerView.Adapter> listadapter;
    private int currentPosition =0;

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
//        tvTitle.setText(String.format("HÓA ĐƠN: %s",mActivity.tvTitle.getText().toString()));
//        createRVBill(mActivity.listBills);
//        rdBill.setChecked(true);
        showOverViewDetail(Util.getTotal(mActivity.listBills));
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(mActivity.listBills);

    }

    private void addEvent() {
//        rgFilter.setOnCheckedChangeListener(this);
//        btnBack.setOnClickListener(this);
//        tvPrint.setOnClickListener(this);
//        btnNew.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
//        tvTitle = view.findViewById(R.id.header_title);
//        btnBack = (ImageView) view.findViewById(R.id.icon_back);
//        rvBill = view.findViewById(R.id.bill_detail_rvbill);
        tvDebt = view.findViewById(R.id.bill_detail_debt);
        tvPaid = view.findViewById(R.id.bill_detail_paid);
        tvTotal = view.findViewById(R.id.bill_detail_total);
//        rgFilter = view.findViewById(R.id.bill_detail_rgfilter);
//        rdBill = view.findViewById(R.id.bill_detail_rdbill);
//        rdPayment = view.findViewById(R.id.bill_detail_rdpayment);
//        rdProduct = view.findViewById(R.id.bill_detail_rdproduct);
        viewPager = view.findViewById(R.id.bill_detail_viewpager);
        tabLayout = view.findViewById(R.id.bill_detail_tabs);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.bill_detail_print:
//                CustomBottomDialog.choiceTwoOption(null, "In những hóa đơn còn nợ", null, "In tất cả hóa đơn", new CustomBottomDialog.TwoMethodListener() {
//                    @Override
//                    public void Method1(Boolean one) {
//                        printDebtBills(true);
//                    }
//
//                    @Override
//                    public void Method2(Boolean two) {
//                        printDebtBills(false);
//                    }
//                });
//                break;

//            case R.id.bill_detail_new:
//                //getActivity().getSupportFragmentManager().popBackStack();
//                Util.hideKeyboard(v);
//                mActivity.openShopCartScreen(mActivity.getCurrentCustomer());
//                break;


        }
    }


    private RecyclerView.Adapter createRVBill(List<BaseModel> listbill){
        CustomerBillsAdapter adapter = new CustomerBillsAdapter(listbill, new CustomerBillsAdapter.CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> listResult, Double total, int id) {
                showDialogReturnProduct(total, listResult, id);
            }

        }, new CustomBottomDialog.FourMethodListener() {
            @Override
            public void Method1(Boolean one) {
                reloadCustomer();
            }

            @Override
            public void Method2(Boolean two) {
                printDebtBills(true);

                }

            @Override
            public void Method3(Boolean three) {
                reloadCustomer();
            }

            @Override
            public void Method4(Boolean four) {
                reloadCustomer();
            }
        });

        return adapter;

    }

    private RecyclerView.Adapter createRVPayment(List<BaseModel> listbill){
//        rvBill.setAdapter(null);
        CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(convert2ListPayment(listbill));

        return adapter;

    }

    private RecyclerView.Adapter createRVProduct(List<BaseModel> listbill){
        StatisticalProductGroupAdapter adapter = new StatisticalProductGroupAdapter(listbill);
        return adapter;
    }

    private void showDialogReturnProduct(Double total, List<BaseModel> listProductReturn, int billId){
        CustomCenterDialog.showDialogReturnProduct(billId, total, mActivity.currentCustomer.getInt("id"), listProductReturn, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    Util.showToast("Trả hàng thành công");
                    reloadCustomer();
                }else {
                    Util.showToast("Không trả hàng thành công");
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
                mActivity.listBills = new ArrayList<>();
                try{
                    if (customer.getString("bills") != null) {
                        JSONArray array = new JSONArray(customer.getString("bills"));

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject objectBill = array.getJSONObject(i);
                            JSONObject objectUser = objectBill.getJSONObject("user");
                            if (User.getRole().equals(Constants.ROLE_ADMIN)){
                                mActivity.listBills.add(new Bill(objectBill));
                            }else {
                                if (User.getId() == objectUser.getInt("id")){
                                    mActivity.listBills.add(new Bill(objectBill));
                                }
                            }

                        }

                        mActivity.currentCustomer.put("bills", array);
                        mActivity.showMoneyOverview(Util.getTotal(mActivity.listBills));
                        setupViewPager(mActivity.listBills);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                createRVBill(mActivity.listBills);
                showOverViewDetail(Util.getTotal(mActivity.listBills));

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

    private void printDebtBills(Boolean printOnlyDebt){
        List<JSONObject> list = new ArrayList<>();
        String billReturnId = checkBillReturn(mActivity.listBills).toString();

        for (int i=0; i<mActivity.listBills.size(); i++){
            if (printOnlyDebt){
                if (mActivity.listBills.get(i).getDouble("debt") >0 && !billReturnId.contains(mActivity.listBills.get(i).getString("id"))){
                    list.add(mActivity.listBills.get(i).convertJsonObject());
                }

            }else {
                if (mActivity.listBills.get(i).getDouble("debt") >=0 && !billReturnId.contains(mActivity.listBills.get(i).getString("id"))){
                    list.add(mActivity.listBills.get(i).convertJsonObject());
                }
            }
        }

        if (list.size() >0){
            Transaction.gotoPrintBillActivity(mActivity.currentCustomer.CustomertoString(),
                    DataUtil.convertListObject2Array(list).toString(), true);
        }else {
            Util.showToast("Không có hóa đơn nợ phù hợp");
        }

    }

    private List<String> checkBillReturn(List<BaseModel> list){
        List<String> listResult = new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            if (!list.get(i).getString("note").equals("") && list.get(i).getString("note").matches(Util.DETECT_NUMBER)){
                listResult.add(list.get(i).getString("id"));
                listResult.add(list.get(i).getString("note"));

//                break;
            }
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

                listResult.add(objectbill);

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
                            if (listResult.get(ii).getString("type").equals(Constants.PAYMENT) &&
                                    listResult.get(ii).getLong("createAt") == object.getLong("createAt") ){
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

    private void setupViewPager( final List<BaseModel> listbill){
        List<String> titles = new ArrayList<>();
        titles.add(0,"Hóa đơn");
        titles.add(1,"Sản phẩm");
        titles.add(2,"Thanh toán");


        listadapter = new ArrayList<>();
        listadapter.add(0,createRVBill(listbill));
        listadapter.add(1,createRVProduct(getAllBillDetail(listbill)));
        listadapter.add(2, createRVPayment(listbill));


        viewpagerAdapter = new ViewpagerBillDetailAdapter(listadapter, titles);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i=0; i<titles.size(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(mActivity).inflate(R.layout.view_tab_product, null);
            TextView tabTextTitle = (TextView) customView.findViewById(R.id.tabNotify);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

            textTitle.setText(titles.get(i).toUpperCase());
            tabTextTitle.setVisibility(View.GONE);

            tab.setCustomView(customView);
        }
    }

    private List<BaseModel> getAllBillDetail(List<BaseModel> listbill){
        final List<BaseModel> listBillDetail = new ArrayList<>();

        try {
            for (int i=0; i<listbill.size(); i++){
                JSONArray arrayBillDetail  = new JSONArray(listbill.get(i).getString("billDetails"));

                for (int ii=0; ii<arrayBillDetail.length(); ii++){
                    BillDetail billDetail = new BillDetail(arrayBillDetail.getJSONObject(ii));
                    listBillDetail.add(billDetail);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listBillDetail;

    }

}
