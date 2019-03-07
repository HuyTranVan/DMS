package wolve.dms.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.StatisticalViewpagerAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.SheetConnect;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.customviews.CustomTabLayout;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialog;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialogFragment;
import wolve.dms.libraries.connectapi.GoogleSheetGet;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalCustomerActivity extends BaseActivity implements  View.OnClickListener {
    private ImageView btnBack;
    private TextView tvTitle, tvEmployeeName;
    private ViewPager viewPager;
    private CustomTabLayout tabLayout;
    private RadioGroup rdGroup;
    private RadioButton rdMonth, rdDate;
    private CTextIcon  btnExport;
    private LinearLayout btnEmployeeFilter;
    private RelativeLayout rlBottom;

    private StatisticalViewpagerAdapter pageAdapter;
    protected List<Bill> listInitialBill = new ArrayList<>();
    protected JSONArray InitialBillHavePayment = new JSONArray();
    protected JSONArray InitialCheckin = new JSONArray();
    protected List<Bill> listBill = new ArrayList<>();
    protected List<BaseModel> listUser = new ArrayList<>();
    private List<Object> listSheetID = new ArrayList<>();
    private int[] icons = new int[]{R.string.icon_chart,
            R.string.icon_bill,
            R.string.icon_product_group,
            R.string.icon_money,
            R.string.icon_district};
    private final String MONTH_DEFAULT ="Chọn tháng";
    private final String DATE_DEFAULT ="Chọn ngày";
    private boolean loadBillNotYetPaid = true;
//    private String currentDate;
    private int mDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private final String ALL_FILTER ="TẤT CẢ";

    @Override
    public int getResourceLayout() {
        return R.layout.activity_statistical_customer;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvTitle = (TextView) findViewById(R.id.statistical_customer_title);
        viewPager = (ViewPager) findViewById(R.id.statistical_customer_viewpager);
        tabLayout = (CustomTabLayout) findViewById(R.id.statistical_customer_tabs);
        rdGroup = findViewById(R.id.statistical_customer_filter);
        rdMonth = findViewById(R.id.statistical_customer_filter_month);
        rdDate = findViewById(R.id.statistical_customer_filter_date);
        btnEmployeeFilter = findViewById(R.id.statistical_customer_filter_by_employee);
        btnExport = findViewById(R.id.statistical_customer_export);
        rlBottom = findViewById(R.id.statistical_customer_bottom_group);
        tvEmployeeName = findViewById(R.id.statistical_customer_filter_by_employee_name);

    }

    @Override
    public void initialData() {
        rdMonth.setText(Util.CurrentMonthYear());
        rdDate.setText(DATE_DEFAULT);
        rlBottom.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN) ? View.VISIBLE :View.GONE);
        tvEmployeeName.setText(User.getRole().equals(Constants.ROLE_ADMIN)? ALL_FILTER : User.getFullName());

        loadListCheckin(getStartDay(), getEndDay());

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        rdMonth.setOnClickListener(this);
        rdDate.setOnClickListener(this);
        btnEmployeeFilter.setOnClickListener(this);
        btnExport.setOnClickListener(this);

    }

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new StatisticalViewpagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalCheckinFragment.class.getName()),  getResources().getString(icons[4]), "Checkin");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDebtFragment.class.getName()),  getResources().getString(icons[4]), "Khách còn nợ");

        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(1);

    }

    public void setupTabLayout(TabLayout tabLayout) {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pageAdapter.getTabView(i));
        }
        tabLayout.requestFocus();
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                onBackPressed();

                break;

            case R.id.statistical_customer_filter_month:
                rdMonth.setText(rdMonth.getText().toString().equals(MONTH_DEFAULT) ? Util.CurrentMonthYear(): rdMonth.getText().toString()) ;
                rdDate.setText(DATE_DEFAULT);
//                currentDate = rdMonth.getText().toString();
                monthPicker();


                break;

            case R.id.statistical_customer_filter_date:
                rdMonth.setText(MONTH_DEFAULT);
                rdDate.setText(rdDate.getText().toString().equals(DATE_DEFAULT) ? Util.CurrentDayMonthYear(): rdDate.getText().toString()) ;
//                currentDate = rdDate.getText().toString();
                datePicker();

                break;

            case R.id.statistical_customer_filter_by_employee:
                if (listUser.size()>1){
                    List<String> users = new ArrayList<>();
                    users.add(0,ALL_FILTER);
                    for (int i=0; i< listUser.size(); i++){
                        users.add(listUser.get(i).getString("displayName"));
                    }

//                    CustomBottomDialog.choiceList("Chọn nhân viên", users, new CustomBottomDialog.StringListener() {
//                        @Override
//                        public void onResponse(String content) {
//                            tvEmployeeName.setText(content);
//                            loadBill(listInitialBill, content);
//
//                            Util.cashFragment.reloadData(convertToListPayment(tvEmployeeName.getText().toString().trim(),InitialBillHavePayment , getStartDay() , getEndDay()));
//                            Util.checkinFragment.reloadData(convertToListCheckin(tvEmployeeName.getText().toString().trim(),
//                                    InitialCheckin,
//                                    getStartDay(),
//                                    getEndDay()) );
//
//                        }
//                    });
                }

                break;

            case R.id.statistical_export:
//                postListbill();


                break;

        }
    }

    private void datePicker() {
//        CustomCenterDialog.showDialogDatePicker(rdDate,new CustomCenterDialog.CallbackRangeTime() {
//            @Override
//            public void onSelected(long start, long end) {
////                currentDate =rdDate.getText().toString();
//                loadListCheckin(start, end);
//            }
//
//        });

    }

    private void monthPicker() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(mYear, mMonth);
        datePickerDialogFragment.setOnDateSetListener(new SimpleDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                mMonth = monthOfYear;
                mYear =year;

                rdMonth.setText(monthOfYear+1 +"-" + year);
//                currentDate = rdMonth.getText().toString();

                loadListCheckin(getStartDay(), getEndDay());

            }
        });
        datePickerDialogFragment.show(getSupportFragmentManager(), null);
    }

    private void postListbill(){
        SheetConnect.getALlValue(Api_link.STATISTICAL_SHEET_KEY, String.format(Api_link.STATISTICAL_SHEET_TAB1,3),  new GoogleSheetGet.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                listSheetID = new ArrayList<>();
                if (results != null){
                    for (int i=0; i<results.size(); i++){
                        listSheetID.add(results.get(i).get(0));
                    }
                }

                String range = String.format(Api_link.STATISTICAL_SHEET_TAB1, listSheetID.size()+3);

                SheetConnect.postValue(Api_link.STATISTICAL_SHEET_KEY, range, getListValueExportToSheet(listBill), new GoogleSheetGet.CallbackListList() {
                    @Override
                    public void onRespone(List<List<Object>> results) {

                    }
                },true);

            }
        }, false);
    }

    private void loadListCheckin(final long starDay, final long lastDay){
        String param = String.format(Api_link.CUSTOMER_CHECKIN_RANGE_PARAM, starDay, lastDay);

        CustomerConnect.ListCustomer(param, new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                if (loadBillNotYetPaid){
                    loadBillNotYetPaid(starDay, lastDay);
                }
                loadBillNotYetPaid = false;

                InitialCheckin =result;
                Util.checkinFragment.reloadData(convertToListCheckin(tvEmployeeName.getText().toString().trim(),
                        InitialCheckin,
                        starDay,
                        lastDay) );

            }

            @Override
            public void onError(String error) {
            }
        }, false);
    }

    private void loadBillNotYetPaid(final long starDay, final long lastDay){
//        String param = String.format(Api_link.BILL_NOT_YET_PAID_RANGE_PARAM, starDay, lastDay);
        String param = "";

        CustomerConnect.ListBillNotYetPaid(param, new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                Util.debtFragment.reloadData(convertToListDebt(tvEmployeeName.getText().toString(),result));
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private long getStartDay(){
        long date = 0;
        if (rdMonth.isChecked()){
            String currentdate = rdMonth.getText().toString();
            if (currentdate.split("-").length ==2){
                String yearStart = currentdate.split("-")[1];
                String yearNext = currentdate.split("-")[1];
                String monthStart = currentdate.split("-")[0].length() ==1 ? "0"+currentdate.split("-")[0] : currentdate.split("-")[0];
                String monthNext = null;
                int next = Integer.parseInt(monthStart) + 1;

                if (next == 13){
                    monthNext = "01";
                    yearNext = String.valueOf(Integer.parseInt(yearNext)+1);

                }else if (String.valueOf(next).length() ==1){
                    monthNext = "0" + String.valueOf(next);
                }else {
                    monthNext = String.valueOf(next);
                }

                date =Util.TimeStamp1(String.format("01-%s-%s",monthStart, yearStart));


            }else {
                date = Util.TimeStamp1(currentdate);

            }

        }else {
            String currentdate1 = rdDate.getText().toString();
            if (currentdate1.contains("\n")){
                date = Util.TimeStamp1(currentdate1.split("\n")[0]);
            }else {
                date = Util.TimeStamp1(currentdate1);
            }
        }


        return date;
    }

    private long getEndDay(){
        long date = 0;

        if (rdMonth.isChecked()){
            String currentdate = rdMonth.getText().toString();
            if (currentdate.split("-").length ==2){
                String yearStart = currentdate.split("-")[1];
                String yearNext = currentdate.split("-")[1];
                String monthStart = currentdate.split("-")[0].length() ==1 ? "0"+currentdate.split("-")[0] : currentdate.split("-")[0];
                String monthNext = null;
                int next = Integer.parseInt(monthStart) + 1;

                if (next == 13){
                    monthNext = "01";
                    yearNext = String.valueOf(Integer.parseInt(yearNext)+1);

                }else if (String.valueOf(next).length() ==1){
                    monthNext = "0" + String.valueOf(next);
                }else {
                    monthNext = String.valueOf(next);
                }

                date =Util.TimeStamp1(String.format("01-%s-%s", monthNext , yearNext));


            }else {
                date = Util.TimeStamp1(currentdate) + 86400000;

            }

        }else {
            String currentdate1 = rdDate.getText().toString();
            if (currentdate1.contains("\n")){
                date = Util.TimeStamp1(currentdate1.split("\n")[1]) + 86400000 ;
            }else {
                date = Util.TimeStamp1(currentdate1)+ 86400000 ;
            }
        }



        return date;
    }

    private List<List<Object>> getListValueExportToSheet(List<Bill> listbill){
        List<List<Object>> values = new ArrayList<>();
        try {
            for (int i=0; i<listbill.size(); i++){
                Bill bill = listbill.get(i);
                if (!listSheetID.toString().contains(bill.getString("id"))){
                    final Customer customer = new Customer(bill.getJsonObject("customer"));
                    List<Object> data = new ArrayList<>();
                    data.add(bill.getString("id"));
                    data.add(Util.DateString(bill.getLong("createAt")));
                    data.add(bill.getJsonObject("user").getString("displayName"));
                    data.add(Constants.getShopInfo(customer.getString("shopType") , null) + " " + customer.getString("signBoard"));
                    data.add(customer.getString("phone"));
                    data.add(bill.getDouble("total"));
                    data.add(bill.getDouble("paid"));
                    data.add(bill.getDouble("debt"));
                    data.add(bill.getString("note"));

                    values.add(data);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values;
    }

    private boolean checkDuplicate(List<BaseModel> list, String key, BaseModel object){
        boolean check = false;
        for (int i=0; i<list.size(); i++){
            if (list.get(i).getString(key).equals( object.getString(key))){
                check = true;
                break;
            }
        }

        return check;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY){
            //reloadData();
        }

    }

    private List<BaseModel> convertToListCheckin(String userName, JSONArray arrayCheckin, long start, long end){
        List<BaseModel> listCheckins = new ArrayList<>();
        try {
            for (int i=0; i< arrayCheckin.length(); i++){
                BaseModel objectCustomer = new BaseModel(arrayCheckin.getJSONObject(i));


                JSONArray arrayDetail = objectCustomer.getJSONArray("checkIns");

                if (arrayDetail.length() >0){
//                    int cunt = arrayDetail.length();
                    for (int a=0; a<arrayDetail.length(); a++){
                        BaseModel objectDetail = new BaseModel(arrayDetail.getJSONObject(a));

                        if (objectDetail.getLong("createAt") - start >= 0 &&
                                objectDetail.getLong("createAt") - end <= 0){

                            if (!checkDuplicate(listCheckins,"id", objectDetail)){
                                BaseModel checkin = new BaseModel();

                                checkin.put("id", objectDetail.getInt("id"));
                                checkin.put("createAt", objectDetail.getLong("createAt"));
                                checkin.put("updateAt", objectDetail.getLong("updateAt"));
                                checkin.put("note", objectDetail.getString("note"));
                                checkin.put("user", objectDetail.getJsonObject("user"));

                                checkin.put("customerId", objectCustomer.getInt("id"));
                                checkin.put("signBoard", objectCustomer.getString("signBoard"));
                                checkin.put("street", objectCustomer.getString("street"));
                                checkin.put("district", objectCustomer.getString("district"));
                                checkin.put("province", objectCustomer.getString("province"));
                                checkin.put("shopType", objectCustomer.getString("shopType"));

                                BaseModel user = new BaseModel(objectDetail.getJsonObject("user"));
                                if (!checkDuplicate(listUser, "id" ,user)){
                                    listUser.add(user);
                                }

                                if (userName.equals(ALL_FILTER)){
                                    listCheckins.add(checkin);

                                }else {
                                    if (user.getString("displayName").equals(userName)){
                                        listCheckins.add(checkin);
                                    }

                                }

                            }

                        }
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listCheckins;
    }

    private List<BaseModel> convertToListDebt(String userName, JSONArray arrayResult){
        List<BaseModel> listDebts = new ArrayList<>();
        try {
            for (int i=0; i<arrayResult.length(); i++){
                JSONObject objectBill = arrayResult.getJSONObject(i);
                JSONArray arrayPayment = objectBill.getJSONArray("payments");

//                if (arrayPayment.length() >0){
//                    for (int a=0; a<arrayPayment.length(); a++){
//                        BaseModel objectPayment = new BaseModel(arrayPayment.getJSONObject(a));
//
//                        if (objectPayment.getLong("createAt") - start >= 0 &&
//                                objectPayment.getLong("createAt") - end <= 0){
//
//                            if (!checkDuplicate(listPayments,"id", objectPayment)){
//                                BaseModel newCash = new BaseModel();
//                                newCash.put("id", objectPayment.getInt("id"));
//                                newCash.put("createAt", objectPayment.getLong("createAt"));
//                                newCash.put("updateAt", objectPayment.getLong("updateAt"));
//                                newCash.put("note", objectPayment.getString("note"));
//                                newCash.put("paid", objectPayment.getDouble("paid"));
//                                newCash.put("user", objectBill.getJSONObject("user"));
//                                newCash.put("customer", objectBill.getJSONObject("customer"));
//
//                                BaseModel user = new BaseModel(objectBill.getJSONObject("user"));
//                                if (!checkDuplicate(listUser, "id" ,user)){
//                                    listUser.add(user);
//                                }
//
//                                if (userName.equals(ALL_FILTER)){
//                                    listPayments.add(newCash);
//
//                                }else {
//                                    if (user.getString("displayName").equals(userName)){
//                                        listPayments.add(newCash);
//                                    }
//
//                                }
//
//
//                            }
//
//                        }
//                    }
//
//                }



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return listDebts;
    }

}
