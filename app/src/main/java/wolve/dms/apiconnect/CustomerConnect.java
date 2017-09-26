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
 * Created by macos on 9/19/17.
 */

public class CustomerConnect {

    public static void CreateCustomer(String params, final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMER_NEW ;

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

    public static void ListCustomer(String param, final CallbackJSONArray listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMERS+ String.format(Constants.CUSTOMER_PARAM, 1,1000);

        new CustomPostMethod(url,param, new Callback() {
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

    public static void ListCustomerLocation(String lat, String lng, final CallbackJSONArray listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMERS_NEAREST+ String.format(Constants.CUSTOMER_NEAREST_PARAM, 1,1000, lat, lng);

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

    public static void DeleteCustomer(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMER_DELETE + params;

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

    public static void GetCustomerDetail(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMER_GETDETAIL + params;

        new CustomGetMethod(url, new Callback() {
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

    public static void PostCheckin(String params, final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CHECKIN_NEW ;

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

}
