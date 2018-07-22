package wolve.dms.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.StatisticalViewpagerAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackJSONArray;

import wolve.dms.callback.CallbackString;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialog;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialogFragment;
import wolve.dms.models.Bill;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalActivity extends BaseActivity implements  View.OnClickListener {
    private ImageView btnBack;
    private TextView tvTitle;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RadioGroup rdGroup;
    private RadioButton rdMonth, rdDate;

    private StatisticalViewpagerAdapter pageAdapter;
    List<Bill> listBill = new ArrayList<>();
    private int[] icons = new int[]{R.string.icon_chart,
            R.string.icon_bill,
            R.string.icon_product_group};
    private final String MONTH_DEFAULT ="Chọn tháng";
    private final String DATE_DEFAULT ="Chọn ngày";
    private String currentDate;
    private int mDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mYear = Calendar.getInstance().get(Calendar.YEAR);


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
        tabLayout = (TabLayout) findViewById(R.id.statistical_tabs);
        rdGroup = findViewById(R.id.statistical_filter);
        rdMonth = findViewById(R.id.statistical_filter_month);
        rdDate = findViewById(R.id.statistical_filter_date);

    }

    @Override
    public void initialData() {
        rdMonth.setText(Util.CurrentMonthYear());
        rdDate.setText(DATE_DEFAULT);
        currentDate = rdMonth.getText().toString();
        loadAllBill(paramDate(currentDate), true);

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        rdMonth.setOnClickListener(this);
        rdDate.setOnClickListener(this);

    }

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new StatisticalViewpagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDashboardFragment.class.getName()),  getResources().getString(icons[0]), "Dashboard");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalBillsFragment.class.getName()),  getResources().getString(icons[1]),"Hóa đơn");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalProductFragment.class.getName()),  getResources().getString(icons[2]), "Sản phẩm");
        viewPager.setAdapter(pageAdapter);
        //viewPager.setCurrentItem(Util.currentHomeFragment);
        viewPager.setOffscreenPageLimit(4);

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

            case R.id.statistical_filter_month:
                rdMonth.setText(rdMonth.getText().toString().equals(MONTH_DEFAULT) ? Util.CurrentMonthYear(): rdMonth.getText().toString()) ;
                rdDate.setText(DATE_DEFAULT);
                currentDate = rdMonth.getText().toString();
                monthPicker();


                break;

            case R.id.statistical_filter_date:
                rdMonth.setText(MONTH_DEFAULT);
                rdDate.setText(rdDate.getText().toString().equals(DATE_DEFAULT) ? Util.CurrentDayMonthYear(): rdDate.getText().toString()) ;
                currentDate = rdDate.getText().toString();
                datePicker();

                break;


        }
    }

    private void datePicker() {
        CustomCenterDialog.showDialogDatePicker(rdDate,new CallbackString() {
            @Override
            public void Result(String s) {
                loadAllBill(s, true);
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
                currentDate = rdMonth.getText().toString();
                loadAllBill(paramDate(currentDate), true);
            }
        });
        datePickerDialogFragment.show(getSupportFragmentManager(), null);
    }

    private void loadAllBill(String param, Boolean showLoading){
        CustomerConnect.ListBill(param, new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                try {
                    listBill = new ArrayList<Bill>();
                    for (int i=0; i<result.length(); i++){
                        JSONObject objectBill = result.getJSONObject(i);
                        JSONObject objectUser = objectBill.getJSONObject("user");

                        Bill bill = new Bill(objectBill);
                        if (bill.getJsonObject("distributor").getInt("id") == Distributor.getId()){
                            if (User.getRole().equals(Constants.ROLE_ADMIN)){
                                listBill.add(bill);
                            }else {
                                if (User.getId() == objectUser.getInt("id")){
                                    listBill.add(bill);
                                }
                            }

                        }
                    }
                    Util.dashboardFragment.reloadData(listBill);
                    Util.billsFragment.reloadData(listBill);
                    Util.productFragment.reloadData(listBill);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
            }
        }, showLoading);
    }

    private String paramDate(String datemonth){
        String result ="&billingFrom=%s&billingTo=%s";

        if (datemonth.split("-").length ==2){
            String yearStart = datemonth.split("-")[1];
            String yearNext = datemonth.split("-")[1];
            String monthStart = datemonth.split("-")[0].length() ==1 ? "0"+datemonth.split("-")[0] : datemonth.split("-")[0];
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
            result = String.format(result, Util.TimeStamp1("01-"+ monthStart +"-" + yearStart) ,Util.TimeStamp1("01-"+ monthNext +"-" + yearNext));

        }else {
            long startDate =  Util.TimeStamp1(datemonth);
            long nextDate = Util.TimeStamp1(datemonth) + 86400000;
            result = String.format(result, startDate, nextDate);

        }


        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY){
            //reloadData();
        }


    }

}
