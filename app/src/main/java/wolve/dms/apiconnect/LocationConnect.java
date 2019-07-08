package wolve.dms.apiconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/18/17.
 */

public class LocationConnect {
    public static void getAddressFromLocation(double lat, double lng, final CallbackCustom listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        String address = String.format(Locale.ENGLISH, Api_link.MAP_GET_ADDRESS, lat, lng);
        new CustomGetMethod(address, new CallbackCustom() {
//            @Override
//            public void onResponse(JSONObject result) {
//                Util.getInstance().stopLoading(stopLoading);
//
//
//                try {
//                    JSONArray results = (JSONArray) result.get("results");
//                    if(results.length() > 0) {
//                        JSONObject firstAddress = (JSONObject) results.get(0);
//                        callback.onResponse(firstAddress);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }

            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    JSONArray results = result.getJSONArray("results");
                    if(results.length() > 0) {
                        BaseModel firstAddress = new BaseModel((String) results.get(0));
                        listener.onResponse(firstAddress);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }
        }).execute();
    }

    public static void getAllProvinces(final CallbackCustomList listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();
        new CustomGetMethod(Api_link.PROVINCES, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }
        }).execute();
    }

    public static void getAllDistrict(String param, final CallbackCustomList listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();
        String address = Api_link.DISTRICTS + param;
        new CustomGetMethod(address, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }
        }).execute();
    }
}
