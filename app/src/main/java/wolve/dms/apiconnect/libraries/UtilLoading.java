package wolve.dms.apiconnect.libraries;

import android.graphics.Color;
import android.os.Handler;

import com.kaopiz.kprogresshud.KProgressHUD;

import wolve.dms.utils.Util;

public class UtilLoading {
    private static UtilLoading util;
    private KProgressHUD cDialog;
    private Handler mHandlerLoading = new Handler();

    private Runnable delayForLoading = new Runnable() {
        @Override
        public void run() {
            if (cDialog != null && cDialog.isShowing() || cDialog != null) {
                cDialog.dismiss();
                cDialog = null;
            }
        }
    };

    public static synchronized UtilLoading getInstance() {
        if (util == null)
            util = new UtilLoading();

        return util;
    }

    public boolean isLoading() {
        if (cDialog != null && cDialog.isShowing())
            return true;
        return false;
    }

    public void showLoading() {
        createLoading("Đang xử lý...");
    }

    public void showLoading(String text) {
        createLoading(text);
    }

    public void showLoading(boolean show) {
        if (show) {
            createLoading("Đang xử lý...");

        }else {
            stopLoading();
        }

    }

    public void createLoading(final String message) {
        if (isLoading()){
            mHandlerLoading.removeCallbacks(delayForLoading);

        }else {
            cDialog = KProgressHUD.create(Util.getInstance().getCurrentActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setDetailsLabel(message)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setBackgroundColor(Color.parseColor("#40000000"))
                    .setDimAmount(0.5f)
                    .show();

        }

    }

    public void stopLoading() {
        mHandlerLoading.postDelayed(delayForLoading, 500);

    }



}
