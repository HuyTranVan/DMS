package wolve.dms.activities;

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
import wolve.dms.models.BaseModel;
import wolve.dms.models.District;
import wolve.dms.utils.Constants;
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

    public void reloadData(String username, List<BaseModel> list, List<BaseModel> listDetail,double total, double paid, double debt, double profit){
        List<BaseModel> mList = new ArrayList<>();
        List<BaseModel> mListDetail= new ArrayList<>();
        if (username.equals(Constants.ALL_FILTER)){
            mList = list;
            mListDetail = listDetail;

        }else {
            mList = new ArrayList<>();
            mListDetail = new ArrayList<>();

            for (BaseModel row1 : list){
                if (row1.getBaseModel("user").getString("displayName").equals(username)){
                    mList.add(row1);
                }
            }

            for (BaseModel row2 : listDetail){
                if (row2.getBaseModel("user").getString("displayName").equals(username)){
                    mListDetail.add(row2);
                }
            }
        }
        setupIncomeChart( mListDetail,total,  paid, debt, profit);
        setupDistrictChart(mList);

    }

//    private void setupBDFChart(double total, double bdf) {
//        PieChartData data;
//        double percent =0.0;
//
//        percent = bdf *100 /total;
//
//        List<SliceValue> values = new ArrayList<SliceValue>();
//
//        SliceValue sliceValue1 = new SliceValue((float) total, ChartUtils.nextColor());
//        sliceValue1.setLabel("Tổng");
//        values.add(sliceValue1);
//
//        SliceValue sliceValue2 = new SliceValue((float) bdf, ChartUtils.nextColor());
//        sliceValue2.setLabel("BDF" + " - " + new DecimalFormat("#.##").format(percent) + "%");
//        values.add(sliceValue2);
//
//        data = new PieChartData(values);
//        data.setHasLabels(true);
//        data.setHasLabelsOutside(true);
//
//        chartBDF.setPieChartData(data);
//        chartBDF.setCircleFillRatio(0.95f);
//
//    }

    private void setupDistrictChart(List<BaseModel> list) {
        //repair data for Chart
        ArrayList<JSONObject> listData = new ArrayList<>();
        List<BaseModel> listDistrict = District.getDistricts();
        try {
            for (int i = 0; i< listDistrict.size(); i++){
                Double total =0.0;
                for (int j=0; j<list.size(); j++){
                    if (list.get(j).getBaseModel("customer").getString("district").equals(listDistrict.get(i).getString("text"))){
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

    private void setupIncomeChart( List<BaseModel> listDetail , double total, double paid, double debt, double profit){
        //float total = Util.getTotalMoney(list).floatValue();
        //float debt =Util.getSumDebt(list).floatValue();
        //float income = Util.getTotalMoney(list).floatValue() - debt;
        double bdf = 0.0f;
        bdf = DataUtil.defineBDFValue(listDetail);

//        setupBDFChart(total, bdf);

//        float profit = 0.0f;
//        if (User.getRole().equals(Constants.ROLE_ADMIN)){
//            profit =Util.getTotalProfit(list).floatValue();
//        }
//        float[] inputData = new float[]{total, income , debt , profit};
        double[] inputDataDouble = new double[]{total,  paid, profit,  debt, bdf};
        float[] inputData = new float[]{(float) total, (float) paid, (float) profit, (float) debt,(float)  bdf};

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

        String profitString = new DecimalFormat("#.##").format(profit *100 /paid) + " %";
        axisValues.add(new AxisValue(2, String.format("### (%s)", profitString).toCharArray()));

        axisValues.add(new AxisValue(3, "Công nợ".toCharArray()));

        String bdfString = new DecimalFormat("#.##").format(bdf *100 /total) + " %";
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
                switch (i){
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





}
