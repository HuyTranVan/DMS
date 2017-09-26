package wolve.dms.apiconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.CustomGetMethod;
import wolve.dms.libraries.CustomPostMethod;
import wolve.dms.libraries.RestClientHelper;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/18/17.
 */

public class LocationConnect {
    public static void getAddressFromLocation(double lat, double lng, final CallbackJSONObject callback, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        String address = String.format(Locale.ENGLISH, Api_link.MAP_GET_ADDRESS, lat, lng);
        new CustomGetMethod(address, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    JSONArray results = (JSONArray) result.get("results");
                    if(results.length() > 0) {
                        JSONObject firstAddress = (JSONObject) results.get(0);
                        callback.onResponse(firstAddress);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void getAllProvinces(final CallbackJSONArray listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();
        new CustomGetMethod(Api_link.PROVINCES, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }

            }

            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void getAllDistrict(String param, final CallbackJSONArray listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();
        String address = Api_link.DISTRICTS + param;
        new CustomGetMethod(address, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }

            }

            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }
}
