package wolve.dms.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.ViewHolder;

import wolve.dms.R;
import wolve.dms.controls.CTextIcon;

/**
 * Created by macos on 9/28/17.
 */

public class CustomBottomDialog {

    public interface TwoMethodListener {
        void Method1(Boolean one);
        void Method2(Boolean two);
    }

    public static void choiceTwoOption(String icon1, String text1, String icon2, String text2, final TwoMethodListener mListener){

        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_choice_2method))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10,10,10,10)
                .setPadding(20,30,20,20)
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

        tvIcon1.setText(icon1);
        tvIcon2.setText(icon2);
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
    }


}
