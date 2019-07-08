package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
import wolve.dms.models.BaseModel;
import wolve.dms.models.District;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalDashboardFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ColumnChartView chartIncome;
    private PieChartView chartDistrict, chartBDF;

    private StatisticalActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_dashboard,container,false);
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
                Log.e("chart", columnIndex +" % " + subcolumnIndex +" & " + value.toString());
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
        chartBDF = view.findViewById(R.id.statistical_dashboard_bdf);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public void reloadData(List<BaseModel> list, List<BaseModel> listDetail, double paid, double debt){
        setupIncomeChart(list, listDetail, paid, debt);
        setupDistrictChart(list);

    }

    private void setupBDFChart(float total, float bdf) {
        PieChartData data;
        double percent =0.0;

        percent = bdf *100 /total;

        List<SliceValue> values = new ArrayList<SliceValue>();

        SliceValue sliceValue1 = new SliceValue((float) total, ChartUtils.nextColor());
        sliceValue1.setLabel("Tổng");
        values.add(sliceValue1);

        SliceValue sliceValue2 = new SliceValue((float) bdf, ChartUtils.nextColor());
        sliceValue2.setLabel("BDF" + " - " + new DecimalFormat("#.##").format(percent) + "%");
        values.add(sliceValue2);

        data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasLabelsOutside(true);

        chartBDF.setPieChartData(data);
        chartBDF.setCircleFillRatio(0.95f);

    }

    private void setupDistrictChart(List<BaseModel> list) {
        //repair data for Chart
        ArrayList<JSONObject> listData = new ArrayList<>();
        List<String> listDistrict = District.getDistrictList();
        try {
            for (int i = 0; i< listDistrict.size(); i++){
                Double total =0.0;
                for (int j=0; j<list.size(); j++){
                    JSONObject objectCustomer = new JSONObject(list.get(j).getString("customer"));
                    if (objectCustomer.getString("district").equals(listDistrict.get(i))){
                        total +=list.get(j).getDouble("total");
                    }

                }


                JSONObject district = new JSONObject();
                district.put("name",listDistrict.get(i) );
                district.put("total", total);
                listData.add(district);


            }

            PieChartData data;

            List<SliceValue> values = new ArrayList<SliceValue>();
            for (int i=0; i<listData.size(); i++){
                if (listData.get(i).getDouble("total") > 0.0){
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


        }catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setupIncomeChart(List<BaseModel> list, List<BaseModel> listDetail , double paid, double debt){
        float total = Util.getTotalMoney(list).floatValue();
        //float debt =Util.getTotalDebt(list).floatValue();
        //float income = Util.getTotalMoney(list).floatValue() - debt;
        float bdf = 0.0f;
        bdf = (float) DataUtil.defineBDFValue(listDetail);

        setupBDFChart(total, bdf);

//        float profit = 0.0f;
//        if (User.getRole().equals(Constants.ROLE_ADMIN)){
//            profit =Util.getTotalProfit(list).floatValue();
//        }
//        float[] inputData = new float[]{total, income , debt , profit};

        float[] inputData = new float[]{total, (float) paid, (float) debt, bdf};

        List<Column> columns = new ArrayList<Column>();

        for (int i = 0; i < 4; ++i) {
            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
            SubcolumnValue subcolumnValue = new SubcolumnValue(inputData[i], ChartUtils.nextColor());
            subcolumnValue.setLabel(Util.FormatMoney((double) inputData[i]));

            values.add(subcolumnValue);

            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);

        }

        ColumnChartData data = new ColumnChartData(columns);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        axisValues.add(new AxisValue(0, "Tổng bán hàng".toCharArray()));
        axisValues.add(new AxisValue(1, "Tiền đã thu".toCharArray()));
        axisValues.add(new AxisValue(2, "Công nợ".toCharArray()));
        axisValues.add(new AxisValue(3, "BDF".toCharArray()));
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

    }





}
