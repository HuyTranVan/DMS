package wolve.dms.apiconnect;

import android.support.v4.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.CustomDeleteMethod;
import wolve.dms.libraries.CustomGetMethod;
import wolve.dms.libraries.CustomPostMethod;
import wolve.dms.libraries.CustomPostMultiPart;
import wolve.dms.libraries.MultipartUtility;
import wolve.dms.libraries.RestClientHelper;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ProductConnect {

    public static void ListProductGroup(final CallbackJSONArray listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCT_GROUPS+ String.format(Api_link.PRODUCTGROUPS_PARAM, 1,10);

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

    public static void CreateProductGroup(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCT_GROUP_NEW ;

        new CustomPostMethod(url,params, false,new Callback() {
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

    public static void DeleteProductGroup(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCT_GROUP_DELETE + params;

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

    public static void ListProduct(final CallbackJSONArray listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCTS+ String.format(Api_link.PRODUCTS_PARAM, 1,100);

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

//    public static void CreateProduct(String params,final CallbackJSONObject listener, final Boolean stopLoading){
//        Util.getInstance().showLoading();
//
//        String url = Api_link.PRODUCT_NEW ;
//
//        new CustomPostMethodMulti(url,params, new Callback() {
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
//    }

    public static void CreateProductMultipart(JSONObject params, final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();
        String url = Api_link.PRODUCT_NEW ;

        new CustomPostMultiPart(url,params, new Callback() {
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

    public static void DeleteProduct(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCT_DELETE + params;

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
