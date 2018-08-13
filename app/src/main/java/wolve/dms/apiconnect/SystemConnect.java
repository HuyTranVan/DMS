package wolve.dms.apiconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackList;
import wolve.dms.libraries.connectapi.CustomGetListMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.utils.Util;

/**
 * Created by macos on 1/28/18.
 */

public class SystemConnect {

    public static void getAllData(final CallbackList listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String[] url =new String[]{
                Api_link.STATUS+ String.format(Api_link.STATUS_PARAM, 1,20),
                Api_link.DISTRICTS + "79",
                Api_link.PRODUCT_GROUPS+ String.format(Api_link.PRODUCTGROUPS_PARAM, 1,10),
                Api_link.PRODUCTS+ String.format(Api_link.PRODUCTS_PARAM, 1,500)};

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
