package wolve.dms.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;
import wolve.dms.R;
import wolve.dms.models.Bill;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalDashboardFragment extends Fragment implements View.OnClickListener {
    private View view;
    private BarChart chartIncome;
    private PieChartView chartDistrict;

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

    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        chartIncome = (BarChart) view.findViewById(R.id.statistical_dashboard_income);
        chartDistrict = (PieChartView) view.findViewById(R.id.statistical_dashboard_district);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public void reloadData(List<Bill> list){
        setupIncomeChart(list);
        setupDistrictChart(list);
    }

    private void setupDistrictChart(List<Bill> list) {
        //repair data for Chart
        ArrayList<JSONObject> listData = new ArrayList<>();
        try {
            for (int i=0; i<Util.mListDistricts.size(); i++){
                Double total =0.0;
                for (int j=0; j<list.size(); j++){
                    JSONObject objectCustomer = new JSONObject(list.get(j).getString("customer"));
                    if (objectCustomer.getString("district").equals(Util.mListDistricts.get(i))){
                        total +=list.get(j).getDouble("total");
                    }

                }

                JSONObject district = new JSONObject();
                district.put("name",Util.mListDistricts.get(i) );
                district.put("total", total);
                listData.add(district);
            }

            PieChartData data;

            List<SliceValue> values = new ArrayList<SliceValue>();
            for (int i=0; i<listData.size(); i++){
                if (listData.get(i).getDouble("total") > 0){
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


        }catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setupIncomeChart(List<Bill> list){
        chartIncome.animateY(1000, Easing.EasingOption.Linear);
        float income =0.0f;
        float debt =0.0f;
        float profit =0.0f;

        for (int i=0; i<list.size(); i++){
            income += list.get(i).getDouble("total");
            debt += list.get(i).getDouble("debt");
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0,income));
            entries.add(new BarEntry(1,debt));
            entries.add(new BarEntry(2,profit));


        BarDataSet dataset = new BarDataSet(entries,"Income");

        ArrayList<String> labels = new ArrayList<>();
            labels.add("Doanh thu");
            labels.add("Công nợ");
            labels.add("Lợi nhuận");

        BarData data = new BarData(labels, dataset);
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);
        chartIncome.invalidate();
        chartIncome.setData(data);
        chartIncome.setDescription(null);
        chartIncome.getAxisLeft().setDrawGridLines(false);
        chartIncome.getXAxis().setDrawGridLines(false);

        chartIncome.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        XAxis xAxis = chartIncome.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

    }


}
