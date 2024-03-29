package wolve.dms.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Calendar;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ItemAdapter;
import wolve.dms.adapter.MapWaitingListAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackLong;
import wolve.dms.callback.CallbackObject;
import wolve.dms.libraries.calendarpicker.SimpleDatePickerDelegate;
import wolve.dms.models.BaseModel;

/**
 * Created by macos on 9/28/17.
 */

public class CustomBottomDialog {

    public interface TwoMethodListener {
        void Method1(Boolean one);

        void Method2(Boolean two);
    }

    public interface ThreeMethodListener {
        void Method1(Boolean one);

        void Method2(Boolean two);

        void Method3(Boolean three);
    }

    public interface FourMethodListener {
        void Method1(Boolean one);

        void Method2(Boolean two);

        void Method3(Boolean three);

        void Method4(Boolean four);
    }

    public interface StringListener {
        void onResponse(String content);
    }

    public interface PositionListener {
        void onResponse(int pos);
    }

    public static DialogPlus choiceTwoOption(String icon1, String text1, String icon2, String text2, final TwoMethodListener mListener) {
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_2method))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.bg_corner5_white)
                .setMargin(10, 10, 10, 10)
//                .setPadding(20,30,20,20)
                .setInAnimation(R.anim.slide_in_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        LinearLayout lnCash = (LinearLayout) dialog.findViewById(R.id.choice_2method_parent1);
        LinearLayout lnDebt = (LinearLayout) dialog.findViewById(R.id.choice_2method_parent2);
        TextView tvIcon1 = (TextView) dialog.findViewById(R.id.choice_2method_icon1);
        TextView tvIcon2 = (TextView) dialog.findViewById(R.id.choice_2method_icon2);
        TextView tvText1 = (TextView) dialog.findViewById(R.id.choice_2method_text1);
        TextView tvText2 = (TextView) dialog.findViewById(R.id.choice_2method_text2);

        tvIcon1.setText(icon1 != null ? icon1 : "");
        tvIcon2.setText(icon2 != null ? icon2 : "");
        tvText1.setText(text1);
        tvText2.setText(text2);

        lnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.Method1(true);

            }
        });

        tvText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.Method1(true);
            }
        });

        lnDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.Method2(true);
            }
        });

        tvText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.Method2(true);
            }
        });

        dialog.show();

        return dialog;
    }

    public static void choiceThreeOption(String icon1, String text1, String icon2, String text2, String icon3, String text3, final ThreeMethodListener mListener) {
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_3method))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.bg_corner5_white)
                .setMargin(10, 10, 10, 10)
                .setInAnimation(R.anim.slide_in_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        LinearLayout lnOne = (LinearLayout) dialog.findViewById(R.id.choice_3method_parent1);
        LinearLayout lnTwo = (LinearLayout) dialog.findViewById(R.id.choice_3method_parent2);
        LinearLayout lnThree = (LinearLayout) dialog.findViewById(R.id.choice_3method_parent3);
        TextView tvIcon1 = (TextView) dialog.findViewById(R.id.choice_3method_icon1);
        TextView tvIcon2 = (TextView) dialog.findViewById(R.id.choice_3method_icon2);
        TextView tvIcon3 = (TextView) dialog.findViewById(R.id.choice_3method_icon3);
        TextView tvText1 = (TextView) dialog.findViewById(R.id.choice_3method_text1);
        TextView tvText2 = (TextView) dialog.findViewById(R.id.choice_3method_text2);
        TextView tvText3 = (TextView) dialog.findViewById(R.id.choice_3method_text3);

        tvIcon1.setText(icon1);
        tvIcon2.setText(icon2);
        tvIcon3.setText(icon3);
        tvText1.setText(text1);
        tvText2.setText(text2);
        tvText3.setText(text3);

        lnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.Method1(true);

            }
        });

        tvText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.Method1(true);
            }
        });

        lnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.Method2(true);

            }
        });

        tvText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.Method2(true);
            }
        });

        lnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.Method3(true);

            }
        });

        tvText3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.Method3(true);
            }
        });

        dialog.show();
    }

    public static void choiceListObject(String title, final List<BaseModel> list, String key, final CallbackObject mListener, CallbackBoolean dismiss) {
        int heigh = list.size() > 6 ? Util.convertSdpToInt(R.dimen._350sdp) :
                (list.size() + 1) * Util.convertSdpToInt(R.dimen._40sdp) + Util.convertSdpToInt(R.dimen._5sdp); //+Util.convertSdpToInt(R.dimen._30sdp);
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_listmethod))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.bg_corner5_white)
                .setMargin(20, 20, 20, 20)
                .setContentHeight(heigh)
                .setInAnimation(R.anim.slide_in_up)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        if (dismiss != null) {
                            dismiss.onRespone(true);

                        }
                    }
                })
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        RecyclerView rvList = (RecyclerView) dialog.findViewById(R.id.view_list_method_rv);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.view_listmethod_title);

        if (title == null) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }


        ItemAdapter adapter = new ItemAdapter(list, key, new PositionListener() {
            @Override
            public void onResponse(int pos) {
                dialog.dismiss();
                mListener.onResponse(list.get(pos));
//                if (list.get(pos).hasKey("invisible") && list.get(pos).getBoolean("invisible")){
//                    Util.showToast("Không thể chọn !");
//                }else {
//                    mListener.onResponse(list.get(pos));
//                }

            }

        });
        Util.createLinearRV(rvList, adapter);

        dialog.show();
    }

    public static void selectDate(String title , long currentDay, CallbackLong listener) {
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_date))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.bg_corner5_white)
                .setMargin(10, 10, 10, 10)
                .setInAnimation(R.anim.slide_in_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();
        TextView tvTitle = (TextView) dialog.findViewById(R.id.view_choicedate_title);
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.view_choicedate_submit);

        tvTitle.setText(title);

        SimpleDatePickerDelegate monthPickerView = new SimpleDatePickerDelegate(dialog.getHolderView());
        monthPickerView.init(Util.Year(currentDay),
                            Util.Month(currentDay),
//                Calendar.getInstance().get(Calendar.YEAR),
//                Calendar.getInstance().get(Calendar.MONTH),
                new SimpleDatePickerDelegate.OnDateChangedListener() {
            @Override
            public void onDateChanged(int year, int monthOfYear){

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = monthPickerView.getMonth() +1 ;
                int year = monthPickerView.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthPickerView.getMonth());
                int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                String value  =  String.format("%s-%s-%s 23:59:59",
                        day <10 ? "0"+String.valueOf(day) : String.valueOf(day),
                        month <10 ? "0"+String.valueOf(month) : String.valueOf(month),
                        String.valueOf(year));

                dialog.dismiss();
                long result = Util.TimeStamp2(value);
                listener.onResponse(result);


            }
        });




        dialog.show();
    }
    public static void customerWaiting(CallbackInt posListener, CallbackListObject callbacklist) {
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_dialog_map_waitinglist))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.color.colorTransparent)
                .setMargin(0, 0, 0, 0)
                .setInAnimation(R.anim.slide_in_up)
                .setOverlayBackgroundResource(R.color.colorTransparent)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                        posListener.onResponse(0);
                    }
                }).create();

        RelativeLayout mParent = (RelativeLayout) dialog.findViewById(R.id.waitinglist_parent);
        ImageView btnBack = (ImageView) dialog.findViewById(R.id.icon_back);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.waitinglist_title);
        RecyclerView rvCustomerWaiting = (RecyclerView) dialog.findViewById(R.id.waitinglist_rvCustomer);
        TextView tvClose = (TextView) dialog.findViewById(R.id.waitinglist_select_close);
        TextView tvCount = (TextView) dialog.findViewById(R.id.waitinglist_select_count);
        RelativeLayout mCheckGroup = (RelativeLayout) dialog.findViewById(R.id.waitinglist_select_group);
        Button btnSubmit = (Button) dialog.findViewById(R.id.waitinglist_submit);
        RelativeLayout mBottom = (RelativeLayout) dialog.findViewById(R.id.waitinglist_submit_parent);
        CheckBox rdCheckAll = (CheckBox) dialog.findViewById(R.id.waitinglist_checkall);
        LinearLayout lnSort = (LinearLayout) dialog.findViewById(R.id.waitinglist_sort);
        TextView tvSortTitle = (TextView) dialog.findViewById(R.id.waitinglist_sort_title);
        View view1 = dialog.findViewById(R.id.bottom_view1);
        View view2 = dialog.findViewById(R.id.bottom_view2);
        View view3 = dialog.findViewById(R.id.bottom_view3);
        View view4 = dialog.findViewById(R.id.bottom_view4);


        List<BaseModel> listCustomerWaiting = CustomSQL.getListObject(Constants.CUSTOMERS_WAITING);
        MapWaitingListAdapter adapter = new MapWaitingListAdapter(listCustomerWaiting,
                    new CallbackInt() {
                @Override
                public void onResponse(int value) {
                    tvCount.setText(String.format("%s Khách hàng",value > 0 ? String.valueOf(value) : ""));
                    if (value > 0) {
                        mCheckGroup.setVisibility(View.VISIBLE);
                        mBottom.setVisibility(View.VISIBLE);
                        if (value == listCustomerWaiting.size()) {
                            rdCheckAll.setChecked(true);
                        }else {
                            rdCheckAll.setChecked(false);
                        }

                    } else {
                        mCheckGroup.setVisibility(View.GONE);
                        mBottom.setVisibility(View.GONE);

                    }
                }
            });
            Util.createLinearRV(rvCustomerWaiting, adapter);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                posListener.onResponse(0);
            }
        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.unCheckAll();
                mCheckGroup.setVisibility(View.GONE);
                rdCheckAll.setChecked(false);

            }
        });
        rdCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rdCheckAll.isChecked()){
                    adapter.CheckAll();
                }else {
                    adapter.unCheckAll();
                }
            }
        });
        lnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDropdow.createListDropdown(lnSort, Constants.listSortItem(), 0, false, new CallbackClickAdapter() {
                    @Override
                    public void onRespone(String data, int position) {
                        tvSortTitle.setText(data);
                        adapter.sort(position);

                    }
                });
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbacklist.onResponse(adapter.getAllChecked());
                dialog.dismiss();
                posListener.onResponse(0);
            }
        });
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                posListener.onResponse(0);
            }
        });
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                posListener.onResponse(1);
            }
        });
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                posListener.onResponse(2);
            }
        });
//        view4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                posListener.onResponse(3);
//            }
//        });

        dialog.show();
    }


}
