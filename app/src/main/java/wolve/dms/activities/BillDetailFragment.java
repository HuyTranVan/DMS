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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.CustomerBillsAdapter;
import wolve.dms.adapter.CustomerPaymentAdapter;
import wolve.dms.adapter.StatisticalProductGroupAdapter;
import wolve.dms.adapter.ViewpagerBillDetailAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.SheetConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackList;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.connectapi.GoogleSheetGet;
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
    private TextView tvTitle, tvDebt, tvPaid, tvTotal, tvBDF;
    private CTextIcon tvExport;
    private RadioGroup rdYears;
//    private RecyclerView rvBill ;
//    private RadioGroup rgFilter;
//    private RadioButton rdBill, rdPayment, rdProduct;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    private CustomerActivity mActivity;
    private ViewpagerBillDetailAdapter viewpagerAdapter;
    private List<RecyclerView.Adapter> listadapter;
    private List<BaseModel> mBills = new ArrayList<>();
    private int currentPosition =0;
    private double currentDebt =0.0;

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
        mBills = mActivity.listBills;
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(filterByYear(mBills));
        addYearCheck(mActivity.mYears);
        tvExport.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN)? View.VISIBLE : View.GONE);


    }

    private void addEvent() {
        tvDebt.setOnClickListener(this);
        tvExport.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        tvDebt = view.findViewById(R.id.bill_detail_debt);
        tvPaid = view.findViewById(R.id.bill_detail_paid);
        tvTotal = view.findViewById(R.id.bill_detail_total);
        viewPager = view.findViewById(R.id.bill_detail_viewpager);
        tabLayout = view.findViewById(R.id.bill_detail_tabs);
        tvBDF = view.findViewById(R.id.bill_detail_bdf);
        tvExport = view.findViewById(R.id.bill_detail_export);
        rdYears = view.findViewById(R.id.bill_detail_yeargroup);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bill_detail_debt:
                if (currentDebt >0){
                    showDialogPayment();
                }
                break;

            case R.id.bill_detail_export:
                exportBillToSheet();
                break;



        }
    }

    private void exportBillToSheet(){
        String range = String.format(Api_link.STATISTICAL_SHEET_TAB2, 2);

        SheetConnect.postValue(Api_link.STATISTICAL_SHEET_KEY, range, getListValueExportToSheet(mBills), new GoogleSheetGet.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {

            }
        },true);
    }

    private void showDialogPayment(){
        CustomCenterDialog.showDialogPayment("THANH TOÁN CÁC HÓA ĐƠN NỢ",
                getAllBillHaveDebt(),
                new CallbackList() {
                    @Override
                    public void onResponse(List result) {
                        postPayToServer(DataUtil.createListPaymentParam(mActivity.currentCustomer.getInt("id"),  result) );
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }

    private void postPayToServer(List<String> listParam){
        CustomerConnect.PostListPay(listParam, new CallbackList() {
            @Override
            public void onResponse(List result) {
                Util.showToast("Thanh toán thành công");
                reloadCustomer();
                //Transaction.returnShopCartActivity(Constants.PRINT_BILL_ACTIVITY, Constants.RELOAD_DATA, Constants.RESULT_PRINTBILL_ACTIVITY);


            }

            @Override
            public void onError(String error) {

            }//
        }, true);
    }

    private RecyclerView.Adapter createRVBill(List<BaseModel> listbill){
        CustomerBillsAdapter adapter = new CustomerBillsAdapter(listbill, new CustomerBillsAdapter.CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> listResult, Double total, int id) {
                if (listResult.size() >0){
                    showDialogReturnProduct(total, listResult, id);

                }else {
                    Util.showToast("Đã thu lại hết sản phẩm");
                }

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
                List<BaseModel> mList = new ArrayList<>();
                try{
                    if (result.getString("bills") != null) {
                        JSONArray array = new JSONArray(result.getString("bills"));

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject objectBill = array.getJSONObject(i);
                            int userID = objectBill.getJSONObject("user").getInt("id");

                            if (User.getRole().equals(Constants.ROLE_ADMIN)){
                                mList.add(new Bill(objectBill));
                            }else {
                                if (User.getId() == userID){
                                    mList.add(new Bill(objectBill));
                                }
                            }

                        }

                        mActivity.currentCustomer.put("bills", array);
                        mActivity.showMoneyOverview(mList);

                        intitialData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

            }
        }, true);

    }

    private void showOverViewDetail(BaseModel object){
        tvTotal.setText(String.format("Tổng: %s" ,Util.FormatMoney(object.getDouble("total"))));
        currentDebt = object.getDouble("debt");
        tvDebt.setText(String.format("Nợ: %s" ,Util.FormatMoney(currentDebt)));
        tvPaid.setText(String.format("Trả: %s" ,Util.FormatMoney(object.getDouble("paid"))));

    }

    private void printDebtBills(Boolean printOnlyDebt){
        List<JSONObject> list = new ArrayList<>();
        for (int i=0; i<mBills.size(); i++){
            if (printOnlyDebt){
                if (mBills.get(i).getDouble("debt") >0 ){
                    list.add(mBills.get(i).convertJsonObject());
                }

            }else {
                list.add(mBills.get(i).convertJsonObject());

            }
        }

        if (list.size() >0){
            Transaction.gotoPrintBillActivity(mActivity.currentCustomer.CustomertoString(),
                    DataUtil.convertListObject2Array(list).toString(), true);
        }else {
            Util.showToast("Không có hóa đơn nợ phù hợp");
        }

    }

    private List<BaseModel> getAllBillHaveDebt(){
        List<BaseModel> list = new ArrayList<>();
        for (int i=0; i<mBills.size(); i++) {
            if (mBills.get(i).getDouble("debt") > 0) {
                list.add(mBills.get(i));

            }
        }
        DataUtil.sortbyKey("createAt", list, true);
        return list;
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
                            if (listResult.get(ii).getString("type").equals(Constants.PAYMENT) &&
                                    Util.DateString(listResult.get(ii).getLong("createAt")).equals(Util.DateString(object.getLong("createAt")))  ){
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

    private List<BaseModel> filterByYear(List<BaseModel> listbill){
        String year  =  Constants.ALL_FILTER;
        int selectedRadioButtonID = rdYears.getCheckedRadioButtonId();
        if(selectedRadioButtonID!=-1){
            RadioButton selectedRadioButton = (RadioButton) rdYears.findViewById(selectedRadioButtonID);
            year = selectedRadioButton.getText().toString();

        }

        List<BaseModel> Lists = new ArrayList<>();

        for (int i =0; i< listbill.size(); i++){
            if (!year.equals(Constants.ALL_FILTER)){
                if (Util.YearString(listbill.get(i).getLong("createAt")).equals(year)){
                    Lists.add(listbill.get(i));
                }

            }else {
                Lists.add(listbill.get(i));
            }
        }
        return Lists;
    }

    private void setupViewPager( final List<BaseModel> listbill){

        showOverViewDetail(Util.getTotal(listbill));
        tvBDF.setText(String.format("BDF:%s ",DataUtil.defineBDFPercent(getAllBillDetail(listbill))) +"%");

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

    private List<List<Object>> getListValueExportToSheet(List<BaseModel> listbill){
        DataUtil.sortbyKey("createAt", listbill, false);
        List<List<Object>> values = new ArrayList<>();
        try {
            for (int i=0; i<listbill.size(); i++){
                BaseModel bill = listbill.get(i);
                List<Object> data = new ArrayList<>();

                final Customer customer = new Customer(bill.getJsonObject("customer"));
//                data.add(i+1);
                data.add(Util.DateString(bill.getLong("createAt")));
                data.add(Constants.getShopInfo(customer.getString("shopType") , null) + " " + customer.getString("signBoard"));
                data.add(customer.getString("phone"));

                JSONArray arrayDetail = bill.getJSONArray("billDetails");
                String detail = "";
                for (int a =0; a<arrayDetail.length(); a++){
                    JSONObject object = arrayDetail.getJSONObject(a);
                    if (a ==0){
                        detail += String.format("%s (%s x %s)", object.getString("productName") ,
                                object.getString("quantity"),
                                Util.FormatMoney(object.getDouble("unitPrice") - object.getDouble("discount")));
                    }else {
                        detail += "\n" + String.format("%s (%s x %s)", object.getString("productName") ,
                                object.getString("quantity"),
                                Util.FormatMoney(object.getDouble("unitPrice") - object.getDouble("discount"))) ;
                    }


                }
                data.add(detail);
                data.add(bill.getDouble("total"));
                data.add(bill.getDouble("paid"));
                data.add(bill.getDouble("debt"));
                data.add(bill.getString("note"));

                values.add(data);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values;
    }

    private void addYearCheck(List years){
        for (int i = 0; i < years.size(); i++) {
            RadioButton button = new RadioButton(mActivity);
            button.setId(i);
            button.setText(years.get(i).toString());
            button.setPadding(10,0,10,0);
            if (i ==0){
                button.setChecked(true);
            }
//            button.setChecked(i == currentHours); // Only select button with same index as currently selected number of hours
//            button.setBackgroundResource(R.drawable.item_selector); // This is a custom button drawable, defined in XML
            rdYears.addView(button);
        }

        rdYears.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setupViewPager(filterByYear(mBills));
            }
        });



    }


}
