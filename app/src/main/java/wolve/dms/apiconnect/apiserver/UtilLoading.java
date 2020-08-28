package wolve.dms.apiconnect.apiserver;

import android.graphics.Color;
import android.os.Handler;

import com.kaopiz.kprogresshud.KProgressHUD;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
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

//    public void showLoading() {
//        createLoading("Đang xử lý...");
//    }

    public void showLoading(String text) {
        int times = CustomSQL.getInt(Constants.LOADING_TIMES) + 1;

        if (times >0){
            CustomSQL.setInt(Constants.LOADING_TIMES, times);
            createLoading(text);
        }


    }

    public void showLoading(boolean show) {
        if (show) {
            int times = CustomSQL.getInt(Constants.LOADING_TIMES) + 1;

            if (times >0){
                CustomSQL.setInt(Constants.LOADING_TIMES, times);
                createLoading("Đang xử lý...");
            }


        }else {
            stopLoading();
        }

    }

    public void createLoading(final String message) {
        if (!isLoading()){
            cDialog = KProgressHUD.create(Util.getInstance().getCurrentActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setDetailsLabel(message)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setBackgroundColor(Color.parseColor("#40000000"))
                    .setDimAmount(0.5f)
                    .show();
        }


//        if (isLoading()){
//            mHandlerLoading.removeCallbacks(delayForLoading);
//
//        }else {
//            cDialog = KProgressHUD.create(Util.getInstance().getCurrentActivity())
//                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                    .setDetailsLabel(message)
//                    .setCancellable(false)
//                    .setAnimationSpeed(2)
//                    .setBackgroundColor(Color.parseColor("#40000000"))
//                    .setDimAmount(0.5f)
//                    .show();
//
//        }

    }

    public void stopLoading() {
        int times = CustomSQL.getInt(Constants.LOADING_TIMES);
        if (times >0){
            CustomSQL.setInt(Constants.LOADING_TIMES, times-1);
            if (cDialog != null && cDialog.isShowing() || cDialog != null) {
                cDialog.dismiss();
                cDialog = null;
            }

        }else {
            if (cDialog != null && cDialog.isShowing() || cDialog != null) {
                cDialog.dismiss();
                cDialog = null;
            }

        }
        //mHandlerLoading.postDelayed(delayForLoading, 500);

    }



}
