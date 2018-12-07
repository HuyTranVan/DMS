package wolve.dms.apiconnect;

import java.util.List;

import wolve.dms.callback.CallbackList;
import wolve.dms.libraries.connectapi.CustomGetListMethod;
import wolve.dms.models.Distributor;
import wolve.dms.utils.Util;

/**
 * Created by macos on 1/28/18.
 */

public class SystemConnect {

    public static void getAllData(final CallbackList listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String[] url =new String[]{
                Api_link.STATUS+ String.format(Api_link.DEFAULT_RANGE, 1,20),
                Api_link.DISTRICTS + Distributor.getLocationId(),
                Api_link.PRODUCT_GROUPS+ String.format(Api_link.DEFAULT_RANGE, 1,10),
                Api_link.PRODUCTS+ String.format(Api_link.DEFAULT_RANGE, 1,500)};

        new CustomGetListMethod(url, new CallbackList() {
            @Override
            public void onResponse(List result) {
                Util.getInstance().stopLoading(stopLoading);
                listener.onResponse(result);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }


}
