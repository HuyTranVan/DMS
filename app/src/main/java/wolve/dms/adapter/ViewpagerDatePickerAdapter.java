package wolve.dms.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.viewpager.widget.PagerAdapter;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wolve.dms.R;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDelegate;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ViewpagerDatePickerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private View view;
    private long startTime;
    private String[] mTitles;
    private CalendarPickerView datePickerView;
    private SimpleDatePickerDelegate monthPickerView;
    private NumberPicker yearPickerView;
    private int mPosition;

    public ViewpagerDatePickerAdapter(long currenttime, int position, String[] titles) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.startTime = currenttime;
        this.mTitles = titles;
        this.mPosition = position;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (position) {
            case 0:
                view = inflater.inflate(R.layout.view_tab_item_datepicker, container, false);
                dateSetup(view, mPosition == position? new Date(startTime) : new Date());
                break;

            case 1:
                view = inflater.inflate(R.layout.view_tab_item_monthpicker, container, false);
                monthSetup(view,
                        mPosition == position? Util.Month(startTime) : Calendar.getInstance().get(Calendar.MONTH),
                        mPosition == position? Util.Year(startTime) : Calendar.getInstance().get(Calendar.YEAR));

                break;

            case 2:
                view = inflater.inflate(R.layout.view_tab_item_yearpicker, container, false);
                yearSetup(view,
                        mPosition == position? Util.Year(startTime) : Calendar.getInstance().get(Calendar.YEAR));
                break;
        }

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        container.refreshDrawableState();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return mTitles[position];
//    }

    private void yearSetup(View view, int year) {
        yearPickerView = view.findViewById(R.id.year);

        yearPickerView.setMaxValue(year + 50);
        yearPickerView.setMinValue(year - 50);
        yearPickerView.setWrapSelectorWheel(false);
        yearPickerView.setValue(year);
        yearPickerView.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

    }

    private void dateSetup(View view, Date date) {
        datePickerView = view.findViewById(R.id.calendar_view);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 10);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -10);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);

        datePickerView.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MM, YYYY", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(date);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                datePickerView.scrollToDate(date);
            }
        }, 200);


    }

    private void monthSetup(View view, int month, int year) {
        monthPickerView = new SimpleDatePickerDelegate(view);
        monthPickerView.init(year, month, new SimpleDatePickerDelegate.OnDateChangedListener() {
            @Override
            public void onDateChanged(int year, int monthOfYear){

            }
        });


    }

    public BaseModel getDateSelected() {
        long startDate = new Timestamp(datePickerView.getSelectedDates().get(0).getTime()).getTime();
        long lastDate = new Timestamp(datePickerView.getSelectedDates().get(datePickerView.getSelectedDates().size() - 1).getTime()).getTime();
        long end = 0;
        String text = "";
        if (datePickerView.getSelectedDates().size() == 1) {
            end = startDate + 86400000;
            text = Util.DateString(startDate);

        } else {
            end = lastDate + 86400000;
            text = String.format("%s >> %s", Util.DateString(startDate), Util.DateString(lastDate));

        }

        BaseModel result = new BaseModel();
        result.put("start", startDate);
        result.put("end", end);
        result.put("text", text);
        result.put("type", Constants.DATE);
        result.put("position", 0);

        return result;
    }

    public BaseModel getMonthSelected() {
        int year = monthPickerView.getYear();
        int month = monthPickerView.getMonth() + 1;
        BaseModel result = new BaseModel();

        String startMonth = month < 10 ? "0" + month : String.valueOf(month);
        String endMonth = "";
        int endYear = 0;
        if (month == 12) {
            endMonth = "01";
            endYear = year + 1;
        } else {
            endMonth = month + 1 < 10 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
            endYear = year;
        }
        result.put("start", Util.TimeStamp2(String.format("01-%s-%d 00:00:00", startMonth, year)));
        result.put("end", Util.TimeStamp2(String.format("01-%s-%d 00:00:00", endMonth, endYear)));
        result.put("text", month + "-" + year);
        result.put("type", Constants.MONTH);
        result.put("position", 1);

        return result;
    }

    public BaseModel getYearSelected() {
        int value = yearPickerView.getValue();
        BaseModel result = new BaseModel();
        result.put("start", Util.TimeStamp2(String.format("01-01-%d 00:00:00", value)));
        result.put("end", Util.TimeStamp2(String.format("01-01-%d 00:00:00", value + 1)));
        result.put("text", String.valueOf(value));
        result.put("type", Constants.YEAR);
        result.put("position", 2);

        return result;

    }


}