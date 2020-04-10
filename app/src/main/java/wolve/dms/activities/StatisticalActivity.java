package wolve.dms.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.api.services.sheets.v4.model.Sheet;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.Statistical_ViewpagerAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SheetConnect;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;

import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CustomTabLayout;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialog;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialogFragment;
import wolve.dms.libraries.calendarpicker.YearPicker;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetGetAllTab;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetGetData;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.libraries.connectapi.sheetapi.GoogleSheetPostData.SHEET_COLUM;
import static wolve.dms.utils.Constants.YEAR_DEFAULT;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalActivity extends BaseActivity implements  View.OnClickListener, View.OnLongClickListener {
    private ImageView btnBack;
    protected TextView tvTitle, tvEmployeeName, btnExport;
    protected ViewPager viewPager;
    private CustomTabLayout tabLayout;
    private RadioGroup rdGroup;
    private RadioButton rdYear, rdMonth, rdDate;
    private CardView btnReload;
    private LinearLayout btnEmployeeFilter;
    private RelativeLayout rlBottom;

    private Statistical_ViewpagerAdapter pageAdapter;

    protected BaseModel temptWarehouse;
    protected List<BaseModel> listInitialBill = new ArrayList<>();
    protected List<BaseModel> listInitialBillDetail = new ArrayList<>();
    protected List<BaseModel> listUser;
    protected List<BaseModel> listInitialPayment;
    protected List<BaseModel> listInitialDebt;
    protected List<BaseModel> listInitialCustomerByUser ;
    //private List<Object> listSheetID = new ArrayList<>();
    private int[] icons = new int[]{
            R.string.icon_chartline,
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
        return R.id.statistical_parent;
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
        listUser = new ArrayList<>();

        listUser.add(0, getAllFilterUser());

        rdMonth.setText(Util.CurrentMonthYear());
        rdDate.setText(Constants.DATE_DEFAULT);
        rdYear.setText(YEAR_DEFAULT);

        rlBottom.setVisibility(User.getCurrentRoleId()==Constants.ROLE_ADMIN|| User.getCurrentRoleId()==Constants.ROLE_WAREHOUSE? View.VISIBLE :View.GONE);
        tvEmployeeName.setText(User.getCurrentRoleId()==Constants.ROLE_ADMIN? Constants.ALL_FILTER : User.getFullName());

        loadInitialData(getStartDay(), getEndDay());

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
        pageAdapter = new Statistical_ViewpagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDashboardFragment.class.getName()),  getResources().getString(icons[0]), "Dashboard");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalBillsFragment.class.getName()),  getResources().getString(icons[1]),"Hóa đơn");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalProductFragment.class.getName()),  getResources().getString(icons[2]), "Sản Phẩm");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalPaymentFragment.class.getName()),  getResources().getString(icons[3]), "Tiền thu");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDebtFragment.class.getName()),  getResources().getString(icons[5]), "Đã mua");

        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(5);

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

                loadInitialData(startday, startday+86400000);
                btnExport.setVisibility(View.GONE);

                break;

            case R.id.statistical_filter_month:
                monthPicker();

                break;

            case R.id.statistical_filter_year:
                yearPicker();

                break;

            case R.id.statistical_filter_by_employee:
                CustomBottomDialog.choiceListObject("Chọn nhân viên", listUser, "displayName", new CallbackBaseModel() {
                    @Override
                    public void onResponse(BaseModel object){
                        tvEmployeeName.setText(object.getString("displayName"));
                        updateAllFragmentData(object.getInt("id"));

                    }

                    @Override
                    public void onError() {

                    }
                }, null);

                break;

            case R.id.statistical_export:
                updateSheetTab();
                break;

            case R.id.statistical_reload:
                loadInitialData(getStartDay(), getEndDay());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);

        }else {
            Transaction.gotoHomeActivityRight(true);
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
                loadInitialData(start, end);
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

                loadInitialData(getStartDay(), getEndDay());

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

                loadInitialData(getStartDay(), getEndDay());

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

    private void loadInitialData(long starDay, long lastDay){
        SystemConnect.getStatisticals(String.format(Api_link.STATISTICAL_PARAM, starDay, lastDay), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                updatePaymentList(DataUtil.array2ListObject(result.getString(Constants.PAYMENTS)));
                updateDebtList(result.getBaseModel(Constants.DEBTS));
                updateBillsList(DataUtil.array2ListObject(result.getString(Constants.BILLS)));
                updateBillDetailList(listInitialBill);
                temptWarehouse = result.getBaseModel("inventory");

                updateAllFragmentData(0);

            }

            @Override
            public void onError(String error) {

            }
        });



    }

    private void updateBillsList(List<BaseModel> list){
        listInitialBill= new ArrayList<>(list);
        updateListUser(listInitialBill);

    }

    private void updateBillDetailList(List<BaseModel> listbill){
        listInitialBillDetail = new ArrayList<>();
        for (BaseModel row : listbill){
            List<BaseModel> listTemp = DataUtil.array2ListBaseModel(row.getJSONArray("billDetails"));
            for (int i=0; i<listTemp.size(); i++){
                listTemp.get(i).put("user", row.getJsonObject("user"));
                listInitialBillDetail.add(listTemp.get(i) );
            }

        }
    }

    private void updatePaymentList(List<BaseModel> list){
        listInitialPayment = new ArrayList<>(list);
        updateListUser(listInitialPayment);

    }

//    private void updateDebtList(List<BaseModel> list){
//        listInitialDebt= new ArrayList<>(list);
//        updateListUser(listInitialDebt);
//
//    }

    private void updateDebtList(BaseModel debt){
        listInitialDebt= DataUtil.array2ListObject(debt.getString("debtList"));
        listInitialCustomerByUser = DataUtil.array2ListObject(debt.getString("ordered"));
        updateListUser(listInitialDebt);

    }

    private void updateAllFragmentData(int id){
        String username = tvEmployeeName.getText().toString().trim();
        Util.paymentFragment.reloadData(username, listInitialPayment);
        Util.debtFragment.reloadData(username, listInitialDebt);
        Util.debtFragment.updateCustomerNumber(id, listInitialCustomerByUser);
        Util.billsFragment.reloadData(username,listInitialBill);
        Util.productFragment.reloadData(username,listInitialBillDetail);
        Util.dashboardFragment.reloadData(username,listInitialBill,
                listInitialBillDetail,
                Util.billsFragment.getSumBillTotal(),
                Util.paymentFragment.getSumPayment(),
                Util.paymentFragment.getCoutList(),
                Util.debtFragment.getSumDebt(),
                Util.debtFragment.CoutList(),
                Util.paymentFragment.getSumProfit(),
                Util.paymentFragment.getSumBaseProfit(),
                temptWarehouse);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        if (data.hasExtra(Constants.RELOAD_DATA) && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY){
            if (data.getBooleanExtra(Constants.RELOAD_DATA, false)){
                loadInitialData(getStartDay(), getEndDay());
            }

        }

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
        String startday = Util.DateString(getStartDay());
        String endday = Util.DateString(Util.CurrentTimeStamp()< getEndDay() ? Util.CurrentTimeStamp() : getEndDay());
        SheetConnect.getALlTab(Api_link.STATISTICAL_SHEET_KEY, new GoogleSheetGetAllTab.CallbackListSheet() {
            @Override
            public void onRespone(List<Sheet> results) {
                boolean check = false;
                for (Sheet sheet : results) {
                    if (sheet.getProperties().getTitle().equals(rdMonth.getText().toString())){
                        check = true;
                        break;
                    }

                }

                if (!check){

                    SheetConnect.createNewTab(Api_link.STATISTICAL_SHEET_KEY, rdMonth.getText().toString(), new GoogleSheetGetData.CallbackListList() {
                        @Override
                        public void onRespone(List<List<Object>> results) {
                            updateSheetIncomeData(rdMonth.getText().toString() ,
                                            SHEET_COLUM,
                                            DataUtil.updateIncomeByUserToSheet(startday, endday, listUser, listInitialBill, listInitialBillDetail, listInitialPayment, listInitialDebt));

//                                            DataUtil.getCashByUser(listInitialBill, listInitialPayment, listUser)));

                        }
                    },false);

                }else {
                    updateSheetIncomeData(rdMonth.getText().toString(),
                                    SHEET_COLUM,
                                    DataUtil.updateIncomeByUserToSheet(startday, endday, listUser, listInitialBill, listInitialBillDetail, listInitialPayment, listInitialDebt));

//                                    DataUtil.getCashByUser(listInitialBill, listInitialPayment, listUser)));

                }

            }
        }, false);
    }

    private void updateSheetIncomeData(final String tabtitle, String sheet_direction, final List<List<Object>> params){
        SheetConnect.postValue(Api_link.STATISTICAL_SHEET_KEY,
                String.format(Api_link.STATISTICAL_SHEET_TAB, tabtitle, 1),
                params,
                sheet_direction,
                new GoogleSheetGetData.CallbackListList() {
                    @Override
                    public void onRespone(List<List<Object>> results) {


                    }
                },true);


    }

    private void updateListUser(List<BaseModel> list){
        for (int i = 0; i< list.size(); i++){
            if (!DataUtil.checkDuplicate(listUser, "displayName", list.get(i).getBaseModel("user"))){
                listUser.add(list.get(i).getBaseModel("user"));
            }

        }
    }

    private BaseModel getAllFilterUser(){
        BaseModel model = new BaseModel();
        model.put("displayName", Constants.ALL_FILTER);

        return model;
    }

}