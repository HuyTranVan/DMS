package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackList;
import wolve.dms.libraries.connectapi.CustomGetListMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
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
                Api_link.PRODUCTS+ String.format(Api_link.DEFAULT_RANGE, 1,500),
                Api_link.PROVINCES};

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

    public static void getDistrict(int provinceId, final CallbackJSONArray listener){
        Util.getInstance().showLoading();

        String url = Api_link.DISTRICTS+provinceId;
        new CustomGetMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(true);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                    Util.getInstance().stopLoading(true);
                }
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(true);
            }
        }).execute();

    }



}
