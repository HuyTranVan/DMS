package wolve.dms.activity;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.StatisticalViewpagerAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.controls.CTextIcon;
import wolve.dms.libraries.MySwipeRefreshLayout;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialog;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDialogFragment;
import wolve.dms.models.Bill;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalActivity extends BaseActivity implements  View.OnClickListener , SwipeRefreshLayout.OnRefreshListener{
    private ImageView btnBack;
    private TextView tvTitle, tvDate;
    private CTextIcon tvIcon;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MySwipeRefreshLayout swipeRefreshLayout;

    private StatisticalViewpagerAdapter pageAdapter;
    List<Bill> listBill = new ArrayList<>();
    private int[] icons = new int[]{R.string.icon_chart,
            R.string.icon_bill,
            R.string.icon_product_group};


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
        tvDate = (TextView) findViewById(R.id.statistical_date_text);
        tvIcon = (CTextIcon) findViewById(R.id.statistical_date_icon);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.statistical_swipelayout);


    }

    @Override
    public void initialData() {

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvIcon.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            returnPreviousScreen();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvDate.setText(Util.CurrentMonthYear());
        loadAllCustomer(tvDate.getText().toString(), true);
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
                returnPreviousScreen();

                break;

            case R.id.statistical_date_icon:
                monthPicker();

                break;


        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2196f3"));
        loadAllCustomer(tvDate.getText().toString(), false);
    }

    private void returnPreviousScreen(){
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();


    }

    private void monthPicker() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        datePickerDialogFragment.setOnDateSetListener(new SimpleDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                tvDate.setText(monthOfYear+1 +"-" + year);
                loadAllCustomer(tvDate.getText().toString(), true);
            }
        });
        datePickerDialogFragment.show(getSupportFragmentManager(), null);
    }

    private void loadAllCustomer(String month, Boolean showLoading){
        CustomerConnect.ListBill(paramDate(month), new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    listBill = new ArrayList<Bill>();
                    for (int i=0; i<result.length(); i++){
                        Bill bill = new Bill(result.getJSONObject(i));
                        listBill.add(bill);
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
                swipeRefreshLayout.setRefreshing(false);
            }
        }, showLoading);
    }

    private String paramDate(String month){
        String yearNo = month.split("-")[1];
        String monthStart = month.split("-")[0].length() ==1 ? "0"+month.split("-")[0] : month.split("-")[0];
        String monthNext = null;
        int next = Integer.parseInt(monthStart) + 1;
        if (next == 13){
            monthNext = "01";
            yearNo = String.valueOf(Integer.parseInt(yearNo)+1);
        }else if (String.valueOf(next).length() ==1){
            monthNext = "0" + String.valueOf(next);
        }else {
            monthNext = String.valueOf(next);
        }

        return "&billingFrom="+ Util.TimeStamp1("01-"+ monthStart +"-" + yearNo) + "&billingTo="+ Util.TimeStamp1("01-"+ monthNext +"-" + yearNo);

    }

}
