package wolve.dms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.adapter.Statistical_ViewpagerAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CustomTabLayout;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomTopDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalActivity extends BaseActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    protected TextView tvTitle, tvEmployeeName, btnReport, btnReload, tvCalendar;
    protected ViewPager viewPager;
    private CustomTabLayout tabLayout;
    private LinearLayout btnEmployeeFilter;
    private Fragment mFragment;
    private CircleImageView employeeImage;

    private Statistical_ViewpagerAdapter pageAdapter;

    protected BaseModel temptWarehouse;
    protected List<BaseModel> listInitialBill = new ArrayList<>();
    protected List<BaseModel> listInitialBillDetail = new ArrayList<>();
    protected List<BaseModel> listUser;
    protected List<BaseModel> listInitialPayment;
    protected List<BaseModel> listInitialDebt;
    protected List<BaseModel> listInitialCustomerByUser;
    //private List<Object> listSheetID = new ArrayList<>();
    private int[] icons = new int[]{
            R.string.icon_chartline,
            R.string.icon_bill,
            R.string.icon_product_group,
            R.string.icon_money,
            R.string.icon_district,
            R.string.icon_bomb};
   //private long start, end;
    private BaseModel currentRangeTime;
    private int currentTabPosition;
    private BaseModel currentUser;

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
        btnEmployeeFilter = findViewById(R.id.statistical_filter_by_employee);
        btnReport = findViewById(R.id.statistical_export);
        btnReload = findViewById(R.id.statistical_reload);
        tvEmployeeName = findViewById(R.id.statistical_filter_by_employee_name);
        tvCalendar = findViewById(R.id.statistical_calendar);
        employeeImage = findViewById(R.id.statistical_filter_by_employee_image);

    }

    @Override
    public void initialData() {
        listUser = new ArrayList<>();
        listUser.add(0, getAllFilterUser());
        currentUser = new BaseModel(getIntent().getStringExtra(Constants.USER));

        if (!currentUser.hasKey("id")){
            tvEmployeeName.setText( Constants.ALL_FILTER);
            Glide.with(this).load(R.drawable.ic_user).fitCenter().into(employeeImage);

        }else {
            tvEmployeeName.setText(currentUser.getString("displayName"));
            Glide.with(this).load(User.getImage()).placeholder(R.drawable.ic_user).fitCenter().into(employeeImage);

        }
        loadInitialData(currentMonth(), 1);

        setupViewPager(viewPager);
        setupTabLayout(tabLayout);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnEmployeeFilter.setOnClickListener(this);
        btnReport.setOnClickListener(this);
        btnReload.setOnClickListener(this);
        tvCalendar.setOnClickListener(this);

    }

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new Statistical_ViewpagerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDashboardFragment.class.getName()), getResources().getString(icons[0]), "Chung");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalPaymentFragment.class.getName()), getResources().getString(icons[3]), "Tiền thu");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalBillsFragment.class.getName()), getResources().getString(icons[1]), "Hóa đơn");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalProductFragment.class.getName()), getResources().getString(icons[2]), "S.Phẩm");
        pageAdapter.addFragment(Fragment.instantiate(this, StatisticalDebtFragment.class.getName()), getResources().getString(icons[5]), "Nợ");

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
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();

                break;

            case R.id.statistical_filter_by_employee:
                if (Util.isAdmin()) {
                    CustomTopDialog.choiceListObject("Chọn nhân viên", listUser, "displayName", new CallbackObject() {
                        @Override
                        public void onResponse(BaseModel object) {
                            currentUser = object;
                            tvEmployeeName.setText(currentUser.getString("displayName"));
                            Glide.with(StatisticalActivity.this).load(currentUser.getString("image")).placeholder(R.drawable.ic_user).fitCenter().into(employeeImage);
                            updateAllFragmentData(currentUser.getInt("id"), currentUser.getInt("warehouse_id"));

                        }

                    }, null);
                }


                break;

            case R.id.statistical_export:
                if (Util.isAdmin()){
                    openSummaryFragment();
                }else {
                    Util.showToast("Không thể thực hiện!");
                }

                break;

            case R.id.statistical_reload:
                loadInitialData(currentRangeTime, currentTabPosition);
                break;

            case R.id.statistical_calendar:
                Bundle bundle = new Bundle();
                bundle.putInt("position", currentTabPosition);
                bundle.putLong("start_time", currentRangeTime.getLong("start"));
                changeFragment(new DatePickerFragment(), bundle, true);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.statistical_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (mFragment != null && mFragment instanceof StatisticalOrderedFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (mFragment != null && mFragment instanceof DatePickerFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (mFragment != null && mFragment instanceof SummaryFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }

    }

    private void loadInitialData(BaseModel range, int tabPosition) {
        tvCalendar.setText(Util.getIconString(R.string.icon_calendar, "   ", range.getString("text")));
        currentRangeTime = range;
        currentTabPosition = tabPosition;
        BaseModel param = createGetParam(String.format(ApiUtil.STATISTICALS(), range.getLong("start"), range.getLong("end")), false);

        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                updatePaymentList(DataUtil.array2ListObject(result.getString(Constants.PAYMENTS)));
                updateDebtList(result.getBaseModel(Constants.DEBTS));
                updateBillsList(DataUtil.array2ListObject(result.getString(Constants.BILLS)));
                updateBillDetailList(listInitialBill);
                temptWarehouse = result.getBaseModel("inventory");

                updateAllFragmentData(currentUser.getInt("id"), currentUser.getInt("warehouse_id"));
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }

    private void updateBillsList(List<BaseModel> list) {
        listInitialBill = new ArrayList<>(list);
        updateListUser(listInitialBill);

    }

    private void updateBillDetailList(List<BaseModel> listbill) {
        listInitialBillDetail = new ArrayList<>();
        for (BaseModel row : listbill) {
            List<BaseModel> listTemp = DataUtil.array2ListBaseModel(row.getJSONArray("billDetails"));
            for (int i = 0; i < listTemp.size(); i++) {
                listTemp.get(i).put("user", row.getJsonObject("user"));
                listInitialBillDetail.add(listTemp.get(i));
            }

        }
    }

    private void updatePaymentList(List<BaseModel> list) {
        listInitialPayment = new ArrayList<>(list);
        updateListUser(listInitialPayment);

    }

    private void updateDebtList(BaseModel debt) {
        listInitialDebt = DataUtil.array2ListObject(debt.getString("debtList"));
        listInitialCustomerByUser = DataUtil.array2ListObject(debt.getString("ordered"));
        updateListUser(listInitialDebt);

    }

    private void updateAllFragmentData(int user_id, int warehouse_id) {
        String username = tvEmployeeName.getText().toString().trim();
        Util.paymentFragment.reloadData(username, listInitialPayment);
        Util.debtFragment.reloadData(username, listInitialDebt);
        Util.debtFragment.updateCustomerNumber(user_id, listInitialCustomerByUser);
        Util.billsFragment.reloadData(username, listInitialBill);
        Util.productFragment.reloadData(username, listInitialBillDetail);
        Util.dashboardFragment.reloadData(username, listInitialBill,
                listInitialBillDetail,
                Util.billsFragment.getSumBillTotal(),
                Util.paymentFragment.getSumPayment(),
                Util.paymentFragment.getCoutList(),
                Util.debtFragment.getSumDebt(),
                Util.debtFragment.getSumNetDebt(),
                Util.debtFragment.CoutList(),
                Util.paymentFragment.getSumProfit(),
                Util.paymentFragment.getSumBaseProfit(),
                Util.billsFragment.getSumBillNetSale(),
                Util.billsFragment.getSumBillSalesNet(),
                Util.billsFragment.getSumBillBasePrice(),
                temptWarehouse,
                warehouse_id);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        if (data.hasExtra(Constants.RELOAD_DATA) && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY) {
            if (data.getBooleanExtra(Constants.RELOAD_DATA, false)) {
                loadInitialData(currentRangeTime, currentTabPosition);
            }

        }

    }

    private void updateListUser(List<BaseModel> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getBaseModel("user").getInt("active") == 1){
                if (!DataUtil.checkDuplicate(listUser, "displayName", list.get(i).getBaseModel("user"))) {
                    listUser.add(list.get(i).getBaseModel("user"));
                }

            }

        }
    }

    private BaseModel getAllFilterUser() {
        BaseModel model = new BaseModel();
        model.put("image","");
        model.put("displayName", Constants.ALL_FILTER);

        return model;
    }

    protected int getCurrentUserId() {
        int user_id = 0;
        if (!tvEmployeeName.getText().toString().trim().equals(Constants.ALL_FILTER)) {
            for (BaseModel model : listUser) {
                if (model.getString("displayName").equals(tvEmployeeName.getText().toString().trim())) {
                    user_id = model.getInt("id");
                    break;
                }
            }
        }
        return user_id;
    }

    private BaseModel currentMonth(){
        BaseModel result = new BaseModel();
        result.put("start", Util.TimeStamp1(Util.Current01MonthYear()));
        result.put("end", Util.TimeStamp1(Util.Next01MonthYear()));
        result.put("text", Util.CurrentMonthYear());
        result.put("type", Constants.MONTH);

        return result;

    }

    @Override
    public void onResponse(BaseModel object){
        tvCalendar.setText(Util.getIconString(R.string.icon_calendar, "   ", object.getString("text")));
        loadInitialData(object, object.getInt("position"));

        btnReport.setVisibility(object.getString("type").equals(Constants.MONTH) ? View.VISIBLE : View.GONE);

    }

    private void openSummaryFragment(){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RANGE_TIME, currentRangeTime.BaseModelstoString());
        changeFragment(new SummaryFragment(), bundle, true);

    }
}