package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.CustomDeleteMethod;
import wolve.dms.libraries.CustomGetMethod;
import wolve.dms.libraries.CustomPostMethod;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatusConnect {

    public static void ListStatus(final CallbackJSONArray listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.STATUS+ String.format(Constants.STATUS_PARAM, 1,20);

        new CustomGetMethod(url, new Callback() {
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

    public static void CreateStatus(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.STATUS_NEW ;

        new CustomPostMethod(url,params, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONObject("data"));

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

    public static void DeleteStatus(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.STATUS_DELETE + params;

        new CustomDeleteMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(null);

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
