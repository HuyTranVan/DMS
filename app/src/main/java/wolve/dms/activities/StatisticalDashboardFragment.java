package wolve.dms.activities;

import static wolve.dms.activities.BaseActivity.createGetParam;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;
import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CHorizontalView;
import wolve.dms.customviews.CVerticalView;
import wolve.dms.models.BaseModel;
import wolve.dms.models.District;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalDashboardFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ColumnChartView chartIncome;
    private ColumnChartView chartNetGroup;
    private PieChartView chartDistrict;
    private CHorizontalView vCash, vProfit, vPaidNet, vPaidBase, vRevenue, vDebt, vDebtNet, vBillNet,vBillSalesNet,
            vBillBase, vInventoryBase, vInventoryNet, vInventoryEmployee, vBaseProfit  ;

    private StatisticalActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_dashboard, container, false);
        Util.dashboardFragment = this;
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {


    }

    private void addEvent() {
        chartIncome.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                Log.e("chart", columnIndex + " % " + subcolumnIndex + " & " + value.toString());
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        chartIncome = (ColumnChartView) view.findViewById(R.id.statistical_dashboard_income);
        chartDistrict = (PieChartView) view.findViewById(R.id.statistical_dashboard_district);
        chartNetGroup = view.findViewById(R.id.statistical_dashboard_netsale);
        vRevenue = view.findViewById(R.id.statistical_dashboard_revenue);
        vCash = view.findViewById(R.id.statistical_dashboard_cash);
        vDebt = view.findViewById(R.id.statistical_dashboard_debt);
        vDebtNet = view.findViewById(R.id.statistical_dashboard_debt_net);
        vProfit = view.findViewById(R.id.statistical_dashboard_profit);
        vInventoryBase = view.findViewById(R.id.statistical_dashboard_inventory_base);
        vInventoryNet = view.findViewById(R.id.statistical_dashboard_inventory_net);
        vInventoryEmployee = view.findViewById(R.id.statistical_dashboard_inventory_employee);
        vBaseProfit = view.findViewById(R.id.statistical_dashboard_base_profit);
        vPaidNet = view.findViewById(R.id.statistical_dashboard_paidnet);
        vPaidBase = view.findViewById(R.id.statistical_dashboard_paidbase);
        vBillNet = view.findViewById(R.id.statistical_dashboard_billnet);
        vBillSalesNet = view.findViewById(R.id.statistical_dashboard_billsalesnet);
        vBillBase = view.findViewById(R.id.statistical_dashboard_billbase);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void reloadData(String username,
                           List<BaseModel> list,
                           List<BaseModel> listDetail,
                           double total,
                           double paid, int countpayment,
                           double debt,
                           double debt_net, int countdebt,
                           double profit,
                           double base_profit,
                           double net_total,
                           double sales_net_total,
                           double base_total,
                           BaseModel temptWarehouse,
                           int warehouse_id) {
        List<BaseModel> mList = new ArrayList<>();
        List<BaseModel> mListDetail = new ArrayList<>();
        if (username.equals(Constants.ALL_FILTER)) {
            mList = list;
            mListDetail = listDetail;

        } else {
            mList = new ArrayList<>();
            mListDetail = new ArrayList<>();

            for (BaseModel row1 : list) {
                if (row1.getBaseModel("user").getString("displayName").equals(username)) {
                    mList.add(row1);
                }
            }

            for (BaseModel row2 : listDetail) {
                if (row2.getBaseModel("user").getString("displayName").equals(username)) {
                    mListDetail.add(row2);
                }
            }

            loadEmployeeInventory(warehouse_id);
        }
        setupIncomeChart(mListDetail, total, paid, debt, profit);
        setupNetGroupChart(mListDetail);
        setupDistrictChart(mList);
        updateOverView( username,
                        mList,
                        paid,
                        countpayment,
                        total,
                        debt,
                        debt_net,
                        countdebt,
                        profit,
                        base_profit,
                        net_total,
                        sales_net_total,
                        base_total,
                        temptWarehouse);

    }

    private void updateOverView(String username,
                                List<BaseModel> bills,
                                double paid, int countpayment,
                                double total,
                                double debt,
                                double debt_net, int countdebt,
                                double profit,
                                double baseprofit,
                                double nettotal,
                                double salesnettotal,
                                double basetotal,
                                BaseModel warehouse){

        vRevenue.setTitleText(String.format("Tổng bán hàng (%d)", bills.size()));
        vRevenue.setText(Util.FormatMoney(total));

        vBillNet.setTitleText("Tổng bán hàng theo giá NET");
        vBillNet.setText(Util.FormatMoney(nettotal));

        vBillSalesNet.setText(Util.FormatMoney(salesnettotal));

        vBillBase.setText(Util.FormatMoney(basetotal));

        vCash.setTitleText(String.format("Tổng tiền thu (%d)", countpayment));
        vCash.setText(Util.FormatMoney(paid));

        vPaidNet.setTitleText("Tổng tiền thu theo giá NET");
        vPaidNet.setText(Util.FormatMoney(paid - profit));

        vPaidBase.setTitleText("Tổng tiền thu theo giá NPP");
        vPaidBase.setText(Util.FormatMoney(paid - baseprofit));

        vProfit.setTitleText("Chiết khấu bán hàng");
        vProfit.setText(Util.FormatMoney(profit));

        vBaseProfit.setTitleText("Chênh lệch giá NPP");
        vBaseProfit.setText(Util.FormatMoney(baseprofit));

        vDebt.setTitleText(String.format("Công nợ (%d)", countdebt));
        vDebt.setText(Util.FormatMoney(debt));
        vDebtNet.setText(Util.FormatMoney(debt_net));


        vInventoryBase.setTitleText(String.format("Tồn kho NPP(%d)", warehouse.getInt("quantity")));
        vInventoryBase.setText(Util.FormatMoney(warehouse.getDouble("total_base")));

        vInventoryNet.setText(Util.FormatMoney(warehouse.getDouble("total_net")));



        //vPaidNet.setVisibility(username.equals(Constants.ALL_FILTER) ? View.GONE : View.VISIBLE);
        vInventoryEmployee.setVisibility(username.equals(Constants.ALL_FILTER) ? View.GONE : View.VISIBLE);
        vInventoryBase.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        vInventoryNet.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        vBillBase.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        vPaidBase.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);

        vBaseProfit.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
    }

    private void setupDistrictChart(List<BaseModel> list) {
        //repair data for Chart
        ArrayList<JSONObject> listData = new ArrayList<>();
        List<BaseModel> listDistrict = District.getDistricts();
        try {
            for (int i = 0; i < listDistrict.size(); i++) {
                Double total = 0.0;
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getBaseModel("customer").getString("district").equals(listDistrict.get(i).getString("text"))) {
                        total += list.get(j).getDouble("total");
                    }

                }


                JSONObject district = new JSONObject();
                district.put("name", listDistrict.get(i).getString("text"));
                district.put("total", total);
                listData.add(district);


            }

            PieChartData data;

            List<SliceValue> values = new ArrayList<SliceValue>();
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getDouble("total") > 0.0) {
                    SliceValue sliceValue = new SliceValue((float) listData.get(i).getDouble("total"), ChartUtils.nextColor());
                    sliceValue.setLabel(listData.get(i).getString("name") + " - " + Util.FormatMoney(listData.get(i).getDouble("total")));
                    values.add(sliceValue);

                }
            }


            data = new PieChartData(values);
            data.setHasLabels(true);
//            data.setHasLabelsOnlyForSelected(true);
            data.setHasLabelsOutside(true);

//            data.setHasCenterCircle(true);


            chartDistrict.setPieChartData(data);
            chartDistrict.setCircleFillRatio(0.7f);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setupIncomeChart(List<BaseModel> listDetail, double total, double paid, double debt, double profit) {
        //float total = Util.getTotalMoney(list).floatValue();
        //float debt =Util.getSumDebt(list).floatValue();
        //float income = Util.getTotalMoney(list).floatValue() - debt;
        double bdf = 0.0f;
        bdf = DataUtil.defineBDFValue(listDetail);

//        setupBDFChart(total, bdf);

//        float profit = 0.0f;
//        if (User.getCurrentRoleId().equals(Constants.ROLE_ADMIN)){
//            profit =Util.getTotalProfit(list).floatValue();
//        }
//        float[] inputData = new float[]{total, income , debt , profit};
        double[] inputDataDouble = new double[]{total, paid, profit, debt, bdf};
        float[] inputData = new float[]{(float) total, (float) paid, (float) profit, (float) debt, (float) bdf};

        List<Column> columns = new ArrayList<Column>();

        for (int i = 0; i < inputData.length; ++i) {
            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
            SubcolumnValue subcolumnValue = new SubcolumnValue(inputData[i], ChartUtils.nextColor());
            subcolumnValue.setLabel(Util.FormatMoney(inputDataDouble[i]));

            values.add(subcolumnValue);

            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);

        }

        ColumnChartData data = new ColumnChartData(columns);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        axisValues.add(new AxisValue(0, "Tổng bán hàng".toCharArray()));

        axisValues.add(new AxisValue(1, "Tiền đã thu".toCharArray()));

        String profitString = new DecimalFormat("#.##").format(profit * 100 / paid) + " %";
        axisValues.add(new AxisValue(2, String.format("### (%s)", profitString).toCharArray()));

        axisValues.add(new AxisValue(3, "Công nợ".toCharArray()));

        String bdfString = new DecimalFormat("#.##").format(bdf * 100 / total) + " %";
        axisValues.add(new AxisValue(4, String.format("BDF (%s)", bdfString).toCharArray()));
        Axis axisX = new Axis(axisValues);

        data.setAxisXBottom(axisX);


//        data.setAxisYLeft(axisX);


//        final Viewport v = new Viewport(chartIncome.getMaximumViewport());
//        v.top = v.top + 30; //example max value
//        chartIncome.setMaximumViewport(v);
//        chartIncome.setCurrentViewport(v);
//Optional step: disable viewport recalculations, thanks to this animations will not change viewport automatically.
        chartIncome.setViewportCalculationEnabled(true);

        chartIncome.setColumnChartData(data);

        chartIncome.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                switch (i) {
                    case 0:
                        mActivity.viewPager.setCurrentItem(1, true);
                        break;

                    case 1:
                        mActivity.viewPager.setCurrentItem(3, true);

                        break;

                    case 2:
                        mActivity.viewPager.setCurrentItem(4, true);
                        break;

                    case 3:

                        break;


                }


                //Log.e("tag", "vị trí " + i + "  ... " + i1);
            }

            @Override
            public void onValueDeselected() {

            }
        });

    }

    private void setupNetGroupChart(List<BaseModel> details) {
        List<BaseModel> listGroup = ProductGroup.getProductGroupList();
        List<BaseModel> mValues = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        DataUtil.sortbyStringKey("name", listGroup, true);

        for (int i = 0; i < listGroup.size(); i++) {
            BaseModel group = new BaseModel();
            group.put("id", listGroup.get(i).getInt("id"));
            group.put("name", listGroup.get(i).getString("name").
                    substring(0, listGroup.get(i).getString("name").length()>7 ? 6 : listGroup.get(i).getString("name").length()));

            double total = 0.0;
            for (int ii=0; ii<details.size(); ii++){
                if (details.get(ii).getInt("productGroup_id") == listGroup.get(i).getInt("id")){
                    total += details.get(ii).getDouble("purchasePrice") * details.get(ii).getInt("quantity");

                }
            }
            group.put("total", total);

            mValues.add(group);

            axisValues.add(new AxisValue(i, group.getString("name").toCharArray()));
        }



        List<Column> columns = new ArrayList<Column>();
        for (int a = 0; a < mValues.size(); a++){
            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
            SubcolumnValue subcolumnValue = new SubcolumnValue(mValues.get(a).getFloat("total"), ChartUtils.nextColor());
            subcolumnValue.setLabel(Util.FormatMoney(mValues.get(a).getDouble("total")));

            values.add(subcolumnValue);

            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);

        }
        Axis axisX = new Axis(axisValues);
        ColumnChartData data = new ColumnChartData(columns);
        data.setAxisXBottom(axisX);

        chartNetGroup.setViewportCalculationEnabled(true);

        chartNetGroup.setColumnChartData(data);





//            double[] inputDataDouble = new double[]{total, paid, profit, debt, bdf};
//        float[] inputData = new float[]{(float) total, (float) paid, (float) profit, (float) debt, (float) bdf};
//
//
//
//        for (int i = 0; i < inputData.length; ++i) {
//            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
//            SubcolumnValue subcolumnValue = new SubcolumnValue(inputData[i], ChartUtils.nextColor());
//            subcolumnValue.setLabel(Util.FormatMoney(inputDataDouble[i]));
//
//            values.add(subcolumnValue);
//
//            Column column = new Column(values);
//            column.setHasLabels(true);
//            columns.add(column);
//
//        }

        //ColumnChartData data = new ColumnChartData(columns);

        //List<AxisValue> axisValues = new ArrayList<AxisValue>();
//        axisValues.add(new AxisValue(0, "Tổng bán hàng".toCharArray()));
//
//        axisValues.add(new AxisValue(1, "Tiền đã thu".toCharArray()));
//
//        String profitString = new DecimalFormat("#.##").format(profit * 100 / paid) + " %";
//        axisValues.add(new AxisValue(2, String.format("### (%s)", profitString).toCharArray()));
//
//        axisValues.add(new AxisValue(3, "Công nợ".toCharArray()));
//
//        String bdfString = new DecimalFormat("#.##").format(bdf * 100 / total) + " %";
//        axisValues.add(new AxisValue(4, String.format("BDF (%s)", bdfString).toCharArray()));



//        data.setAxisYLeft(axisX);


//        final Viewport v = new Viewport(chartIncome.getMaximumViewport());
//        v.top = v.top + 30; //example max value
//        chartIncome.setMaximumViewport(v);
//        chartIncome.setCurrentViewport(v);
//Optional step: disable viewport recalculations, thanks to this animations will not change viewport automatically.


//        chartIncome.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
//            @Override
//            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
//                switch (i) {
//                    case 0:
//                        mActivity.viewPager.setCurrentItem(1, true);
//                        break;
//
//                    case 1:
//                        mActivity.viewPager.setCurrentItem(3, true);
//
//                        break;
//
//                    case 2:
//                        mActivity.viewPager.setCurrentItem(4, true);
//                        break;
//
//                    case 3:
//
//                        break;
//
//
//                }
//
//
//                //Log.e("tag", "vị trí " + i + "  ... " + i1);
//            }
//
//            @Override
//            public void onValueDeselected() {
//
//            }
//        });

    }

    private void loadEmployeeInventory(int warehouse_id){
        BaseModel param = createGetParam(String.format(ApiUtil.INVENTORIES_BY_WAREHOUSE(), warehouse_id), false);

        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                vInventoryEmployee.setTitleText(String.format("Tồn kho nhân viên giá NET(%d)", result.getInt("quantity")));
                vInventoryEmployee.setText(Util.FormatMoney(result.getDouble("total_net")));
            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

}
