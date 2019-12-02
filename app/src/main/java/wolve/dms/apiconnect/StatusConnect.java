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
import wolve.dms.utils.DataUtil;
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

    public static void CreateStatus(String params,final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.STATUS_NEW ;

        new CustomPostMethod(DataUtil.createNewStatusParam(params), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Util.getInstance().stopLoading(true);
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

    public static void DeleteStatus(String params,final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.STATUS_DELETE + params;

        new CustomDeleteMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Util.getInstance().stopLoading(true);
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
