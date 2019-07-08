package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.connectapi.CustomDeleteMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatusConnect {

    public static void ListStatus(final CallbackCustomList listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.STATUS+ String.format(Api_link.DEFAULT_RANGE, 1,20);

        new CustomGetMethod(url, new CallbackCustom() {
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

    public static void CreateStatus(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.STATUS_NEW ;

//        new CustomPostMethod(url,params,false, new Callback() {
//            @Override
//            public void onResponse(JSONObject result) {
//                Util.getInstance().stopLoading(stopLoading);
//                try {
//                    if (result.getInt("status") == 200) {
//                        listener.onResponse(result.getJSONObject("data"));
//
//                    } else {
//                        listener.onError("Unknow error");
//                    }
//                } catch (JSONException e) {
//                    listener.onError(e.toString());
//                }
//            }
//
//
//            @Override
//            public void onError(String error) {
//                listener.onError(error);
//                Util.getInstance().stopLoading(stopLoading);
//            }
//        }).execute();
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
