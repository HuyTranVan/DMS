package wolve.dms.libraries.calendarpicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

public class YearPicker {

    public static Dialog showCustomDialog(int resId) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Util.getInstance().getCurrentActivity());
        final Dialog d = adb.setView(new View(Util.getInstance().getCurrentActivity())).create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.colorwhite_corner_large);

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

    public static void showDialogYearPicker(String currentYear, final CallbackString mListener, final CallbackBoolean clickListener){
        final Dialog dialogResult = YearPicker.showCustomDialog(R.layout.view_dialog_select_yearpicker);
        dialogResult.setCancelable(true);
        dialogResult.setCanceledOnTouchOutside(true);
        Button ok = dialogResult.findViewById(R.id.btn_submit);
        Button cancel = dialogResult.findViewById(R.id.btn_cancel);
        final NumberPicker numberPicker = dialogResult.findViewById(R.id.year);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        numberPicker.setMaxValue(year+50);
        numberPicker.setMinValue(year-50);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(currentYear != null? Util.YearInt(currentYear) : year);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        dialogResult.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                clickListener.onRespone(false);
            }
        });



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.Result(String.valueOf(numberPicker.getValue()));
                dialogResult.dismiss();
                clickListener.onRespone(true);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
                clickListener.onRespone(false);
            }
        });


    }

    public static void showDialogDatePicker(final RadioButton rdButton, final CustomCenterDialog.CallbackRangeTime mListener, final CallbackBoolean clickListener){
        final String result ="&billingFrom=%d&billingTo=%d";

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

//        calendarView.deactivateDates(list);
//        ArrayList<Date> arrayList = new ArrayList<>();
//        try {
//            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
//            String strdate = "22-2-2018";
//            String strdate2 = "26-2-2018";
//            Date newdate = dateformat.parse(strdate);
//            Date newdate2 = dateformat.parse(strdate2);
//            arrayList.add(newdate);
//            arrayList.add(newdate2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        calendarView.init(lastYear.getTime(), nextYear.getTime(),new SimpleDateFormat("MMMM, YYYY", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date());
//                .withDeactivateDates(list)
//                .withHighlightedDates(arrayList);

        dialogResult.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                clickListener.onRespone(false);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
                clickListener.onRespone(false);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startDate = new Timestamp(calendarView.getSelectedDates().get(0).getTime()).getTime();
                long lastDate = new Timestamp(calendarView.getSelectedDates().get(calendarView.getSelectedDates().size()-1).getTime()).getTime() ;

                if (calendarView.getSelectedDates().size() ==1){
                    rdButton.setText(Util.DateString(startDate));
                    mListener.onSelected(startDate, startDate+86400000);
                }else {
                    rdButton.setText(String.format("%s\n%s", Util.DateString(startDate) ,Util.DateString(lastDate)));
                    mListener.onSelected(startDate,lastDate +86400000);
                }

//                mListener.Result(String.format(result, startDate, lastDate+ 86400000));
                dialogResult.dismiss();
                clickListener.onRespone(true);

            }
        });

    }


}
