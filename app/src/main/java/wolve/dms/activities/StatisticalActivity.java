package wolve.dms.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.sheets.v4.model.Sheet;

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
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustomList;

import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.customviews.CustomTabLayout;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialog;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialogFragment;
import wolve.dms.libraries.calendarpicker.YearPicker;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetGetAllTab;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetGetData;
import wolve.dms.models.BaseModel;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.YEAR_DEFAULT;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalActivity extends BaseActivity implements  View.OnClickListener, View.OnLongClickListener {
    private ImageView btnBack;
    protected TextView tvTitle, tvEmployeeName;
    private ViewPager viewPager;
    private CustomTabLayout tabLayout;
    private RadioGroup rdGroup;
    private RadioButton rdYear, rdMonth, rdDate;
    private CTextIcon  btnExport;
    private CardView btnReload;
    private LinearLayout btnEmployeeFilter;
    private RelativeLayout rlBottom;

    private StatisticalViewpagerAdapter pageAdapter;

    protected List<BaseModel> InitialBillHavePayment = new ArrayList();
    protected JSONArray InitialCheckin = new JSONArray();
    protected List<BaseModel> listBill = new ArrayList<>();
    protected List<BaseModel> listInitialBill = new ArrayList<>();
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    protected List<BaseModel> listUser = new ArrayList<>();
    protected List<BaseModel> listPayment = new ArrayList<>();
    protected List<BaseModel> listDebt = new ArrayList<>();
    //private List<Object> listSheetID = new ArrayList<>();
    private int[] icons = new int[]{R.string.icon_chart,
            R.string.icon_bill,
            R.string.icon_product_group,
            R.string.icon_money,
            R.string.icon_district,
            R.string.icon_bomb};
    private int mDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int currentChecked;


    @Override
    public int getResourceLayout() {
        return R.layout.activity_statistical;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvTitle = (TextView) findViewById(R.id.statistical_title);
        viewPager = (ViewPager) findViewById(R.id.statistical_viewpager);
        tabLayout = (CustomTabLayout) findViewById(R.id.statistical_tabs);
        rdGroup = findViewById(R.id.statistical_filter);
        rdMonth = findViewById(R.id.statistical_filter_month);
        rdYear = findViewById(R.id.statistical_filter_year);
        rdDate = findViewById(R.id.statistical_filter_date);
        btnEmployeeFilter = findViewById(R.id.statistical_filter_by_employee);
        btnExport = findViewById(R.id.statistical_export);
        btnReload = findViewById(R.id.statistical_reload);
        rlBottom = findViewById(R.id.statistical_bottom_group);
        tvEmployeeName = findViewById(R.id.statistical_filter_by_employee_name);

    }

    @Override
    public void initialData() {
        rdMonth.setText(Util.CurrentMonthYear());
        rdDate.setText(Constants.DATE_DEFAULT);
        rdYear.setText(YEAR_DEFAULT);
        rlBottom.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN)|| User.getRole().equals(Constants.ROLE_WAREHOUSE)? View.VISIBLE :View.GONE);
        tvEmployeeName.setText(User.getRole().equals(Constants.ROLE_ADMIN)? Constants.ALL_FILTER : User.getFullName());

        //loadInitialBills(getStartDay(), getEndDay());

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

        currentChecked = rdGroup.getCheckedRadioButtonId();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        rdMonth.setOnClickListener(this);
        rdDate.setOnClickListener(this);
        rdDate.setOnLongClickListener(this);
        rdYear.setOnClickListener(this);
        btnEmployeeFilter.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        btnReload.setOnClickListener(this);

    }

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new StatisticalViewpagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDashboardFragment.class.getName()),  getResources().getString(icons[0]), "Dashboard");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalBillsFragment.class.getName()),  getResources().getString(icons[1]),"Hóa đơn");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalProductFragment.class.getName()),  getResources().getString(icons[2]), "Sản Phẩm");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalCashFragment.class.getName()),  getResources().getString(icons[3]), "Tiền thu");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDebtFragment.class.getName()),  getResources().getString(icons[5]), "Nợ");

        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(5);

        loadListDebt();

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

            case R.id.statistical_filter_date:
                long startday = Util.TimeStamp2(String.format("%s 00:00:00",Util.CurrentDayMonthYear() ));
                rdDate.setText(Util.CurrentDayMonthYear());
                rdMonth.setText(Constants.MONTH_DEFAULT);
                rdYear.setText(Constants.YEAR_DEFAULT);
                currentChecked = rdGroup.getCheckedRadioButtonId();

                loadInitialBills(startday, startday+86400000);
                btnExport.setVisibility(View.GONE);

                break;

            case R.id.statistical_filter_month:
                monthPicker();

                break;

            case R.id.statistical_filter_year:
                yearPicker();

                break;

            case R.id.statistical_filter_by_employee:
                if (listUser.size()>1){
                    List<String> users = new ArrayList<>();
                    if (User.getRole().equals(Constants.ROLE_ADMIN)){
                        users.add(0, Constants.ALL_FILTER);
                    }
                    for (int i=0; i< listUser.size(); i++){
                        users.add(listUser.get(i).getString("displayName"));
                    }

                    CustomBottomDialog.choiceList("Chọn nhân viên", users, new CustomBottomDialog.StringListener() {
                        @Override
                        public void onResponse(String content) {
                            tvEmployeeName.setText(content);
                            loadBill(listInitialBill, content);

                            //Util.cashFragment.reloadData(convertToListPayment(tvEmployeeName.getText().toString().trim(),InitialBillHavePayment , getStartDay() , getEndDay()));
                            //Util.debtFragment.adapter.getFilter().filter(content);
                        }
                    });
                }

                break;

            case R.id.statistical_export:
                //postListbill();
                updateSheetTab();
                break;

            case R.id.statistical_reload:
                loadInitialBills(getStartDay(), getEndDay());
                break;
        }
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

            btnExport.setVisibility(View.VISIBLE);

        }else if (rdDate.isChecked()){
            String currentdate1 = rdDate.getText().toString();
            if (currentdate1.contains("\n")){
                date = Util.TimeStamp1(currentdate1.split("\n")[0]);
            }else {
                date = Util.TimeStamp1(currentdate1);
            }

            btnExport.setVisibility(View.GONE);

        }else if (rdYear.isChecked()){
            date = Util.TimeStamp1(String.format("01-01-%s", rdYear.getText().toString()));

            btnExport.setVisibility(View.GONE);
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

        }else if (rdDate.isChecked()){
            String currentdate1 = rdDate.getText().toString();
            if (currentdate1.contains("\n")){
                date = Util.TimeStamp1(currentdate1.split("\n")[1]) + 86400000 ;
            }else {
                date = Util.TimeStamp1(currentdate1)+ 86400000 ;
            }

        }else if (rdYear.isChecked()){

            date = Util.TimeStamp1(String.format("01-01-%d", Integer.parseInt(rdYear.getText().toString()) +1));

        }



        return date;
    }

    private void datePicker() {
        YearPicker.showDialogDatePicker(rdDate, new CustomCenterDialog.CallbackRangeTime() {
            @Override
            public void onSelected(long start, long end) {
                rdDate.setChecked(true);
                rdMonth.setText(Constants.MONTH_DEFAULT);
                rdYear.setText(Constants.YEAR_DEFAULT);
                currentChecked = rdGroup.getCheckedRadioButtonId();
                loadInitialBills(start, end);
                btnExport.setVisibility(View.GONE);
            }

        }, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (!result){
                    rdGroup.check(currentChecked);
                    if (currentChecked != rdDate.getId()){
                        rdDate.setText(Constants.DATE_DEFAULT);
                    }
                }
            }
        });

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

                rdDate.setText(Constants.DATE_DEFAULT);
                rdYear.setText(Constants.YEAR_DEFAULT);
                currentChecked = rdGroup.getCheckedRadioButtonId();

                loadInitialBills(getStartDay(), getEndDay());

            }
        });
        datePickerDialogFragment.setOnDismissListener(new SimpleDatePickerDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean result) {
                if (!result){
                    rdGroup.check(currentChecked);
                    if (currentChecked != rdMonth.getId()){
                        rdMonth.setText(Constants.MONTH_DEFAULT);
                    }

                }
            }
        });

        datePickerDialogFragment.show(getSupportFragmentManager(), null);
    }

    private void yearPicker() {
        YearPicker.showDialogYearPicker(null, new CallbackString() {
            @Override
            public void Result(String s) {
                rdYear.setText(s);
                rdMonth.setText(Constants.MONTH_DEFAULT);
                rdDate.setText(Constants.DATE_DEFAULT);
                currentChecked = rdGroup.getCheckedRadioButtonId();

                loadInitialBills(getStartDay(), getEndDay());

            }
        }, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (!result){
                    rdGroup.check(currentChecked);
                    if (currentChecked != rdYear.getId()){
                        rdYear.setText(Constants.YEAR_DEFAULT);
                    }


                }
            }
        });
    }

    private void loadInitialData(long starDay, long lastDay, boolean loadDebt){
        SystemConnect.loadListObject(createLoadParam(starDay, lastDay, loadDebt), new CallbackListCustom() {
            @Override
            public void onResponse(List result) {



                //if (loadDebt)

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private List<BaseModel> createLoadParam(long starDay, long lastDay, boolean loadDebt){
        //1. LoadBill
        //2. LoadPayment
        //3. LoadDebt
        List<BaseModel> listResult = new ArrayList<>();
        listResult.add(0,DataUtil.createBillParam(starDay, lastDay));
        listResult.add(1,DataUtil.createPaymentParam(starDay, lastDay));
        if (loadDebt){
            listResult.add(2, DataUtil.createDebtParam());
        }

        return listResult;
    }


    private void loadInitialBills(final long starDay, final long lastDay){
        String param = String.format(Api_link.BILL_RANGE_PARAM, starDay, lastDay);

        CustomerConnect.ListBill(param, new CallbackCustomList() {
//            @Override
//            public void onResponse(JSONArray result) {
//                loadListPayment(starDay, lastDay);
//
//                try {
//                    listInitialBill = new ArrayList<>();
//
//                    for (int i=0; i<result.length(); i++){
//                        Bill bill = new Bill(result.getJSONObject(i));
//                        BaseModel user = new BaseModel(bill.getString("user"));
//
//                        if (bill.getJsonObject("distributor").getInt("id") == Distributor.getId()){
//
//                            if (!DataUtil.checkDuplicate(listUser, "id" ,user)){
//                                if (User.getRole().equals(Constants.ROLE_WAREHOUSE)){
//                                    if (!user.getString("role").equals(Constants.ROLE_ADMIN)){
//                                        listUser.add(user);
//                                    }
//                                }else {
//                                    listUser.add(user);
//                                }
//                            }
//                            listInitialBill.add(bill);
//
//                        }
//                    }
//
//                    loadBill(listInitialBill, tvEmployeeName.getText().toString().trim());
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

            @Override
            public void onResponse(List<BaseModel> results) {
                loadListPayment(starDay, lastDay);

                listInitialBill = new ArrayList<>();

                for (int i=0; i<results.size(); i++){
                    BaseModel distributor = new BaseModel(results.get(i).getJsonObject("distributor"));
                    BaseModel user = new BaseModel(results.get(i).getJsonObject("user"));

                    if (distributor.getInt("id") == Distributor.getId()){

                        if (!DataUtil.checkDuplicate(listUser, "id" ,user)){
                            if (User.getRole().equals(Constants.ROLE_WAREHOUSE)){
                                if (!user.getString("role").equals(Constants.ROLE_ADMIN)){
                                    listUser.add(user);
                                }
                            }else {
                                listUser.add(user);
                            }
                        }
                        listInitialBill.add(results.get(i));

                    }
                }

                loadBill(listInitialBill, tvEmployeeName.getText().toString().trim());

            }

            @Override
            public void onError(String error) {
            }
        }, false);
    }

    private void loadListPayment(final long starDay, final long lastDay){
        String param = String.format(Api_link.BILL_HAVE_PAYMENT_RANGE_PARAM, starDay, lastDay);

        CustomerConnect.ListBillHavePayment(param, new CallbackCustomList() {
//            @Override
//            public void onResponse(JSONArray result) {
//                InitialBillHavePayment =result;
//                listPayment = convertToListPayment(Constants.ALL_FILTER,InitialBillHavePayment , starDay, lastDay);
//                Util.cashFragment.reloadData(convertToListPayment(tvEmployeeName.getText().toString().trim(),InitialBillHavePayment , starDay, lastDay));
//
//            }

            @Override
            public void onResponse(List<BaseModel> results) {
//                InitialBillHavePayment =result;
//                listPayment = convertToListPayment(Constants.ALL_FILTER,InitialBillHavePayment , starDay, lastDay);
//                Util.cashFragment.reloadData(convertToListPayment(tvEmployeeName.getText().toString().trim(),InitialBillHavePayment , starDay, lastDay));

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void loadListDebt(){
        String param = String.format(Api_link.CUSTOMER_DEBT_PARAM, 0);
        CustomerConnect.ListCustomer(param,500, new CallbackCustomList() {
//            @Override
//            public void onResponse(JSONArray result) {
//                loadInitialBills(getStartDay(), getEndDay());
//
//                try {
//                    for (int i=0; i<result.length(); i++){
//                        BaseModel object = new BaseModel(result.getJSONObject(i));
//                        object.put("debt", object.getDouble("currentDebt"));
//
//                        if (!object.getString("note").isEmpty()  && Util.isJSONValid(object.getString("note"))){
//                            JSONObject note = new JSONObject(object.getString("note"));
//
//                            object.put("userName", note.getString("userName"));
//
//                            listDebt.add(object);
//
//
//                        }
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                Util.debtFragment.reloadData(listDebt);
//            }

            @Override
            public void onResponse(List<BaseModel> results) {


                loadInitialBills(getStartDay(), getEndDay());
                 for (int i=0; i<results.size(); i++){
                    results.get(i).put("debt", results.get(i).getDouble("currentDebt"));

                    if (!results.get(i).getString("note").isEmpty()  && Util.isJSONValid(results.get(i).getString("note"))){
                        BaseModel note = new BaseModel(results.get(i).getJsonObject("note"));

                        results.get(i).put("userName", note.getString("userName"));

                        listDebt.add(results.get(i));


                    }

                }

                Util.debtFragment.reloadData(listDebt);
            }

            @Override
            public void onError(String error) {

            }
        }, false);
    }


    private void loadBill(List<BaseModel> initialBills, String userName){
        listBill = new ArrayList<>();
        listBillDetail = new ArrayList<>();
        if (userName.equals(Constants.ALL_FILTER)){
            listBill = initialBills;

        }else {
            for (int i=0; i<initialBills.size(); i++){
                User user = new User(initialBills.get(i).getJsonObject("user"));
                if (userName.equals(user.getString("displayName"))){
                    listBill.add(initialBills.get(i));
                }
            }
        }
        try {
            for (int a=0; a<listBill.size(); a++){
                JSONArray array = listBill.get(a).getJSONArray("billDetails");
                for (int j=0; j<array.length(); j++){
                    JSONObject object= array.getJSONObject(j);
                    listBillDetail.add(new BillDetail(object));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Util.dashboardFragment.reloadData(listBill, listBillDetail, Util.cashFragment.getTotalPaid(), Util.debtFragment.getTotalDebt());
        Util.billsFragment.reloadData(listBill);
        Util.productFragment.reloadData(listBillDetail);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY){
            //reloadData();
        }

    }



    private List<BaseModel> convertToListPayment(String userName, JSONArray arrayResult, long start, long end){
        List<BaseModel> listPayments = new ArrayList<>();
        try {
            for (int i=0; i<arrayResult.length(); i++){
                JSONObject objectBill = arrayResult.getJSONObject(i);
                JSONArray arrayPayment = objectBill.getJSONArray("payments");

                if (arrayPayment.length() >0){
                    for (int a=0; a<arrayPayment.length(); a++){
                        BaseModel objectPayment = new BaseModel(arrayPayment.getJSONObject(a));

                        if (objectPayment.getLong("createAt") - start >= 0 &&
                                objectPayment.getLong("createAt") - end <= 0){

                            if (!DataUtil.checkDuplicate(listPayments,"id", objectPayment)){
                                BaseModel newCash = new BaseModel();
                                newCash.put("id", objectPayment.getInt("id"));
                                newCash.put("createAt", objectPayment.getLong("createAt"));
                                newCash.put("updateAt", objectPayment.getLong("updateAt"));
                                newCash.put("note", objectPayment.getString("note"));
                                newCash.put("paid", objectPayment.getDouble("paid"));
                                newCash.put("user", objectBill.getJSONObject("user"));
                                newCash.put("customer", objectBill.getJSONObject("customer"));

                                BaseModel user = new BaseModel(objectBill.getJSONObject("user"));
                                if (!DataUtil.checkDuplicate(listUser, "id" ,user)){
                                    if (User.getRole().equals(Constants.ROLE_WAREHOUSE)){
                                        if (!user.getString("role").equals(Constants.ROLE_ADMIN)){
                                            listUser.add(user);
                                        }
                                    }else {
                                        listUser.add(user);
                                    }

                                }

                                if (userName.equals(Constants.ALL_FILTER)){
                                    listPayments.add(newCash);

                                }else {
                                    if (user.getString("displayName").equals(userName)){
                                        listPayments.add(newCash);
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



        return listPayments;
    }

    @Override
    public boolean onLongClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.statistical_filter_date:

                datePicker();

                break;

        }
        return true;
    }

    private void updateSheetTab(){

        SheetConnect.getALlTab(Api_link.STATISTICAL_SHEET_KEY, new GoogleSheetGetAllTab.CallbackListSheet() {
            @Override
            public void onRespone(List<Sheet> results) {

//                String currentTab = "";
                boolean check = false;
                for (Sheet sheet : results) {
                    if (sheet.getProperties().getTitle().equals(rdMonth.getText().toString())){
                        check = true;
//                        currentTab = sheet.getProperties().getTitle();
                        break;
                    }

                }

                if (!check){
//                    currentTab = rdMonth.getText().toString();

                    SheetConnect.createNewTab(Api_link.STATISTICAL_SHEET_KEY, rdMonth.getText().toString(), new GoogleSheetGetData.CallbackListList() {
                        @Override
                        public void onRespone(List<List<Object>> results) {
                            updateSheetIncomeData(rdMonth.getText().toString() ,
                                            DataUtil.updateIncomeByUserToSheet(Util.DateString(getStartDay()),
                                            Util.DateString(Util.CurrentTimeStamp()< getEndDay() ? Util.CurrentTimeStamp() : getEndDay()),
                                            DataUtil.getCashByUser(listInitialBill, listPayment, listUser)));

                        }
                    },false);

                }else {
                    updateSheetIncomeData(rdMonth.getText().toString() ,
                                    DataUtil.updateIncomeByUserToSheet(Util.DateString(getStartDay()),
                                    Util.DateString(Util.CurrentTimeStamp()< getEndDay() ? Util.CurrentTimeStamp() : getEndDay()),
                                    DataUtil.getCashByUser(listInitialBill, listPayment, listUser)));



                }

            }
        }, false);
    }

    private void updateSheetIncomeData(final String tabtitle, final List<List<Object>> params){
        SheetConnect.postValue(Api_link.STATISTICAL_SHEET_KEY,
                String.format(Api_link.STATISTICAL_SHEET_TAB, tabtitle, 1),
                params,
                new GoogleSheetGetData.CallbackListList() {
                    @Override
                    public void onRespone(List<List<Object>> results) {


                    }
                },true);


    }

}

//        SheetConnect.updateCurrentTab(Api_link.STATISTICAL_SHEET_KEY, new GoogleSheetGetData.CallbackListList() {
//            @Override
//            public void onRespone(List<List<Object>> results) {
//
//
//
//
//
//            }
//        }, true);

//    private List<BaseModel> convertToListCheckin(String userName, JSONArray arrayCheckin, long start, long end){
//        List<BaseModel> listCheckins = new ArrayList<>();
//        try {
//            for (int i=0; i< arrayCheckin.length(); i++){
//                BaseModel objectCustomer = new BaseModel(arrayCheckin.getJSONObject(i));
//
//
//                JSONArray arrayDetail = objectCustomer.getJSONArray("checkIns");
//
//                if (arrayDetail.length() >0){
////                    int cunt = arrayDetail.length();
//                    for (int a=0; a<arrayDetail.length(); a++){
//                        BaseModel objectDetail = new BaseModel(arrayDetail.getJSONObject(a));
//
//                        if (objectDetail.getLong("createAt") - start >= 0 &&
//                                objectDetail.getLong("createAt") - end <= 0){
//
//                            if (!checkDuplicate(listCheckins,"id", objectDetail)){
//                                BaseModel checkin = new BaseModel();
//
//                                checkin.put("id", objectDetail.getInt("id"));
//                                checkin.put("createAt", objectDetail.getLong("createAt"));
//                                checkin.put("updateAt", objectDetail.getLong("updateAt"));
//                                checkin.put("note", objectDetail.getString("note"));
//                                checkin.put("user", objectDetail.getJsonObject("user"));
//
//                                checkin.put("customerId", objectCustomer.getInt("id"));
//                                checkin.put("signBoard", objectCustomer.getString("signBoard"));
//                                checkin.put("street", objectCustomer.getString("street"));
//                                checkin.put("district", objectCustomer.getString("district"));
//                                checkin.put("province", objectCustomer.getString("province"));
//                                checkin.put("shopType", objectCustomer.getString("shopType"));
//
//                                BaseModel user = new BaseModel(objectDetail.getJsonObject("user"));
//                                if (!checkDuplicate(listUser, "id" ,user)){
//                                    if (User.getRole().equals(Constants.ROLE_WAREHOUSE)){
//                                        if (!user.getString("role").equals(Constants.ROLE_ADMIN)){
//                                            listUser.add(user);
//                                        }
//                                    }else {
//                                        listUser.add(user);
//                                    }
//                                }
//
//                                if (userName.equals(Constants.ALL_FILTER)){
//                                    listCheckins.add(checkin);
//
//                                }else {
//                                    if (user.getString("displayName").equals(userName)){
//                                        listCheckins.add(checkin);
//                                    }
//
//                                }
//
//                            }
//
//                        }
//                    }
//
//                }
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return listCheckins;
//    }