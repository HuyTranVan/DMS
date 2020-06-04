package wolve.dms.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;

/**
 * Created by macos on 9/28/17.
 */

public class CustomTopDialog {
    public static DialogPlus dialog;

    public static void showTextNotify(String title, String text, CallbackBoolean dissmis) {
        Util.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {
                dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                        .setContentHolder(new ViewHolder(R.layout.view_top_notify))
                        .setGravity(Gravity.TOP)
                        .setBackgroundColorResId(R.drawable.bg_corner5_white)
                        .setMargin(Util.convertSdpToInt(R.dimen._5sdp),//left
                                Util.convertSdpToInt(R.dimen._5sdp), //top
                                Util.convertSdpToInt(R.dimen._5sdp), //right
                                Util.convertSdpToInt(R.dimen._5sdp)) //bottom
                        .setOverlayBackgroundResource(R.color.black_text_color_hint)
                        .setInAnimation(R.anim.slide_down)
                        .setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogPlus dialog) {
                                dissmis.onRespone(true);
                            }
                        }).setOnBackPressListener(new OnBackPressListener() {
                            @Override
                            public void onBackPressed(DialogPlus dialogPlus) {
                                dialogPlus.dismiss();
                            }
                        }).create();

                LinearLayout lnParent = (LinearLayout) dialog.findViewById(R.id.top_notify_parent);
                TextView tvTitle = (TextView) dialog.findViewById(R.id.top_notify_title);
                TextView tvText = (TextView) dialog.findViewById(R.id.top_notify_text);

                tvTitle.setText(title);
                tvText.setText(text);

                lnParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }


        });

    }


}
