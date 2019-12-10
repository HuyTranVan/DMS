package wolve.dms.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ItemAdapter;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.customviews.CTextIcon;
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

    public interface StringListener{
        void onResponse(String content);
    }

    public interface PositionListener{
        void onResponse(int pos);
    }

    public static DialogPlus choiceTwoOption(String icon1, String text1, String icon2, String text2, final TwoMethodListener mListener){
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_2method))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10,10,10,10)
//                .setPadding(20,30,20,20)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        LinearLayout lnCash = (LinearLayout) dialog.findViewById(R.id.choice_2method_parent1);
        LinearLayout lnDebt = (LinearLayout) dialog.findViewById(R.id.choice_2method_parent2);
        CTextIcon tvIcon1 = (CTextIcon) dialog.findViewById(R.id.choice_2method_icon1);
        CTextIcon tvIcon2 = (CTextIcon) dialog.findViewById(R.id.choice_2method_icon2);
        TextView tvText1 = (TextView) dialog.findViewById(R.id.choice_2method_text1);
        TextView tvText2 = (TextView) dialog.findViewById(R.id.choice_2method_text2);

        tvIcon1.setText(icon1!= null? icon1:"");
        tvIcon2.setText(icon2!= null? icon2:"");
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

    public static void choiceThreeOption(String icon1, String text1, String icon2, String text2,String icon3, String text3, final ThreeMethodListener mListener){
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_3method))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10,10,10,10)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        LinearLayout lnOne = (LinearLayout) dialog.findViewById(R.id.choice_3method_parent1);
        LinearLayout lnTwo = (LinearLayout) dialog.findViewById(R.id.choice_3method_parent2);
        LinearLayout lnThree = (LinearLayout) dialog.findViewById(R.id.choice_3method_parent3);
        CTextIcon tvIcon1 = (CTextIcon) dialog.findViewById(R.id.choice_3method_icon1);
        CTextIcon tvIcon2 = (CTextIcon) dialog.findViewById(R.id.choice_3method_icon2);
        CTextIcon tvIcon3 = (CTextIcon) dialog.findViewById(R.id.choice_3method_icon3);
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

    public static void choiceFourOption(String icon1, String text1, String icon2, String text2,String icon3, String text3, String icon4, String text4, final FourMethodListener mListener){
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_4method))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10,10,10,10)
//                .setPadding(20,30,20,20)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        LinearLayout lnOne = (LinearLayout) dialog.findViewById(R.id.choice_4method_parent1);
        LinearLayout lnTwo = (LinearLayout) dialog.findViewById(R.id.choice_4method_parent2);
        LinearLayout lnThree = (LinearLayout) dialog.findViewById(R.id.choice_4method_parent3);
        LinearLayout lnFour = (LinearLayout) dialog.findViewById(R.id.choice_4method_parent4);
        CTextIcon tvIcon1 = (CTextIcon) dialog.findViewById(R.id.choice_4method_icon1);
        CTextIcon tvIcon2 = (CTextIcon) dialog.findViewById(R.id.choice_4method_icon2);
        CTextIcon tvIcon3 = (CTextIcon) dialog.findViewById(R.id.choice_4method_icon3);
        CTextIcon tvIcon4 = (CTextIcon) dialog.findViewById(R.id.choice_4method_icon4);
        TextView tvText1 = (TextView) dialog.findViewById(R.id.choice_4method_text1);
        TextView tvText2 = (TextView) dialog.findViewById(R.id.choice_4method_text2);
        TextView tvText3 = (TextView) dialog.findViewById(R.id.choice_4method_text3);
        TextView tvText4 = (TextView) dialog.findViewById(R.id.choice_4method_text4);

        tvIcon1.setText(icon1);
        tvIcon2.setText(icon2);
        tvIcon3.setText(icon3);
        tvIcon4.setText(icon4);
        tvText1.setText(text1);
        tvText2.setText(text2);
        tvText3.setText(text3);
        tvText4.setText(text4);

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

        lnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.Method4(true);

            }
        });

        tvText4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.Method4(true);
            }
        });


        dialog.show();
    }

//    public static void choiceList(String title, final List<String> list, final StringListener mListener){
//        int heigh = list.size()>5 ? 400: list.size()*50 +65;
//
//        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
//                .setContentHolder(new ViewHolder(R.layout.view_choice_listmethod))
//                .setGravity(Gravity.BOTTOM)
//                .setBackgroundColorResId(R.drawable.colorwhite_corner)
//                .setMargin(20,20,20,20)
//                .setContentHeight((int) Util.convertDp2Px(heigh))
//                .setInAnimation(R.anim.slide_up)
//                .setOnBackPressListener(new OnBackPressListener() {
//                    @Override
//                    public void onBackPressed(DialogPlus dialogPlus) {
//                        dialogPlus.dismiss();
//                    }
//                }).create();
//
//        RecyclerView rvList = (RecyclerView) dialog.findViewById(R.id.view_list_method_rv);
//        TextView tvTitle = (TextView) dialog.findViewById(R.id.view_listmethod_title);
//
//        if (title == null){
//            tvTitle.setVisibility(View.GONE);
//        }else {
//            tvTitle.setVisibility(View.VISIBLE);
//            tvTitle.setText(title);
//        }
//
//        ItemAdapter adapter = new ItemAdapter(list, new PositionListener() {
//            @Override
//            public void onResponse(int pos) {
//                dialog.dismiss();
//                mListener.onResponse(list.get(pos));
//            }
//        });
//        Util.createLinearRV(rvList, adapter);
//
//        dialog.show();
//    }

    public static void choiceListObject(String title, final List<BaseModel> list, final CallbackBaseModel mListener){
        int heigh = list.size()>5 ? Util.convertSdpToInt(R.dimen._300sdp):
                (list.size()+1)*Util.convertSdpToInt(R.dimen._35sdp); //+Util.convertSdpToInt(R.dimen._30sdp);
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_listmethod))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(20,20,20,20)
                .setContentHeight(heigh)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        RecyclerView rvList = (RecyclerView) dialog.findViewById(R.id.view_list_method_rv);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.view_listmethod_title);

        if (title == null){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }


        ItemAdapter adapter = new ItemAdapter(list, new PositionListener() {
            @Override
            public void onResponse(int pos) {
                dialog.dismiss();
                mListener.onResponse(list.get(pos));
            }

        });
        Util.createLinearRV(rvList, adapter);

        dialog.show();
    }
}
