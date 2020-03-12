package wolve.dms.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import wolve.dms.R;
import wolve.dms.activities.HomeActivity;
import wolve.dms.adapter.ItemAdapter;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;

/**
 * Created by macos on 9/28/17.
 */

public class CustomTopDialog {
    public static DialogPlus dialog;

    public static void showTextNotify(String title, String text){
        Util.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {
                dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                        .setContentHolder(new ViewHolder(R.layout.view_top_notify))
                        .setGravity(Gravity.TOP)
                        .setBackgroundColorResId(R.drawable.bg_corner5_white)
                        .setMargin(Util.convertSdpToInt(R.dimen._10sdp),//left
                                Util.convertSdpToInt(R.dimen._5sdp), //top
                                Util.convertSdpToInt(R.dimen._10sdp), //right
                                Util.convertSdpToInt(R.dimen._5sdp)) //bottom
                        .setOverlayBackgroundResource(R.color.black_text_color_hint)
                        .setInAnimation(R.anim.slide_down)
                        .setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogPlus dialog) {
                                Util.showToast("xong");
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
