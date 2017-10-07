package wolve.dms.apiconnect;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.CustomPostMethod;
import wolve.dms.utils.ApiHelper;
import wolve.dms.utils.CustomSharedPrefer;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class UserConnect {

    public static void Login(String params, final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();
        new CustomPostMethod(Api_link.LOGIN, params,false, new Callback() {
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
                Util.getInstance().stopLoading(stopLoading);
                listener.onError(error);
            }
        }).execute();
    }
}

