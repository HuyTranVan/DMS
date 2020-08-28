package wolve.dms.libraries.calendarpicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

public class CalendarUtil {

    public static Dialog showCustomDialog(int resId) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Util.getInstance().getCurrentActivity());
        final Dialog d = adb.setView(new View(Util.getInstance().getCurrentActivity())).create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.bg_corner5_white);

        d.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        d.getWindow().setAttributes(lp);

        d.show();
        d.setContentView(resId);


        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        return d;
    }

    private static long getStartDay(String month, String year) {
        long date = 0;
        if (month != null) {
            if (month.split("-").length == 2) {
                String yearStart = month.split("-")[1];
                String yearNext = month.split("-")[1];
                String monthStart = month.split("-")[0].length() == 1 ? "0" + month.split("-")[0] : month.split("-")[0];
                String monthNext = null;
                int next = Integer.parseInt(monthStart) + 1;

                if (next == 13) {
                    monthNext = "01";
                    yearNext = String.valueOf(Integer.parseInt(yearNext) + 1);

                } else if (String.valueOf(next).length() == 1) {
                    monthNext = "0" + String.valueOf(next);
                } else {
                    monthNext = String.valueOf(next);
                }

                date = Util.TimeStamp1(String.format("01-%s-%s", monthStart, yearStart));


            } else {
                date = Util.TimeStamp1(month);

            }


        } else if (year != null) {
            date = Util.TimeStamp1(String.format("01-01-%s", year));

        }


        return date;
    }

    private static long getEndDay(String month, String year) {
        long date = 0;

        if (month != null) {
            if (month.split("-").length == 2) {
                String yearStart = month.split("-")[1];
                String yearNext = month.split("-")[1];
                String monthStart = month.split("-")[0].length() == 1 ? "0" + month.split("-")[0] : month.split("-")[0];
                String monthNext = null;
                int next = Integer.parseInt(monthStart) + 1;

                if (next == 13) {
                    monthNext = "01";
                    yearNext = String.valueOf(Integer.parseInt(yearNext) + 1);

                } else if (String.valueOf(next).length() == 1) {
                    monthNext = "0" + String.valueOf(next);
                } else {
                    monthNext = String.valueOf(next);
                }

                date = Util.TimeStamp1(String.format("01-%s-%s", monthNext, yearNext));


            } else {
                date = Util.TimeStamp1(month) + 86400000;

            }

        } else if (year != null) {

            date = Util.TimeStamp1(String.format("01-01-%d", Integer.parseInt(year) + 1));

        }


        return date;
    }


    public static void datePicker(final CustomCenterDialog.CallbackRangeTime mListener, final CallbackString mString) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_select_datepicker);
        dialogResult.setCancelable(true);
        dialogResult.setCanceledOnTouchOutside(true);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.title);
        Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final CalendarPickerView calendarView = dialogResult.findViewById(R.id.calendar_view);

        tvTitle.setText("Chọn thời gian");
        btnSubmit.setText("Chọn");
        btnCancel.setText("Hủy");

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 10);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -10);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);

        calendarView.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM, YYYY", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)

                .withSelectedDate(new Date());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                calendarView.scrollToDate(new Date());
            }
        }, 200);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startDate = new Timestamp(calendarView.getSelectedDates().get(0).getTime()).getTime();
                long lastDate = new Timestamp(calendarView.getSelectedDates().get(calendarView.getSelectedDates().size() - 1).getTime()).getTime();

                if (calendarView.getSelectedDates().size() == 1) {
                    //rdButton.setText(Util.DateString(startDate));
                    mListener.onSelected(startDate, startDate + 86400000);
                    mString.Result(Util.DateString(startDate));
                } else {
                    //rdButton.setText(String.format("%s\n%s", Util.DateString(startDate) ,Util.DateString(lastDate)));
                    mListener.onSelected(startDate, lastDate + 86400000);
                    mString.Result(String.format("%s đến %s", Util.DateString(startDate), Util.DateString(lastDate)));

                }

                dialogResult.dismiss();

            }
        });

    }

    public static SimpleDatePickerDialogFragment monthPicker(CustomCenterDialog.CallbackRangeTime mListener, CallbackString mString) {
        int mMonth = Calendar.getInstance().get(Calendar.MONTH);
        int mYear = Calendar.getInstance().get(Calendar.YEAR);

        SimpleDatePickerDialogFragment datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(mYear, mMonth);
        datePickerDialogFragment.setOnDateSetListener(new SimpleDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                mString.Result(monthOfYear + 1 + "-" + year);
                mListener.onSelected(getStartDay(monthOfYear + 1 + "-" + year, null),
                        getEndDay(monthOfYear + 1 + "-" + year, null));

            }
        });
        datePickerDialogFragment.setOnDismissListener(new SimpleDatePickerDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean result) {

            }
        });

        return datePickerDialogFragment;
    }

//    public static void showDialogYearPicker(String currentYear, CustomCenterDialog.CallbackRangeTime mListener, final CallbackString mString) {
//        final Dialog dialogResult = YearPicker.showCustomDialog(R.layout.view_dialog_select_yearpicker);
//        dialogResult.setCancelable(true);
//        dialogResult.setCanceledOnTouchOutside(true);
//        Button ok = dialogResult.findViewById(R.id.btn_submit);
//        Button cancel = dialogResult.findViewById(R.id.btn_cancel);
//        final NumberPicker numberPicker = dialogResult.findViewById(R.id.year);
//
//        int year = Calendar.getInstance().get(Calendar.YEAR);
//        numberPicker.setMaxValue(year + 50);
//        numberPicker.setMinValue(year - 50);
//        numberPicker.setWrapSelectorWheel(false);
//        numberPicker.setValue(currentYear != null ? Util.YearInt(currentYear) : year);
//        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mString.Result(String.valueOf(numberPicker.getValue()));
//                mListener.onSelected(getStartDay(null, String.valueOf(numberPicker.getValue())),
//                        getEndDay(null, String.valueOf(numberPicker.getValue())));
//                dialogResult.dismiss();
//
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogResult.dismiss();
//
//            }
//        });
//
//
//    }


}
