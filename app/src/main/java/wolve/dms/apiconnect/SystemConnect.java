package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackListBaseModel;
import wolve.dms.libraries.connectapi.CustomGetListMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomGetPostListMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 1/28/18.
 */

public class SystemConnect {

    public static void loadList(List<BaseModel> listparam, CallbackListBaseModel listener){
        Util.getInstance().showLoading();

        new CustomGetPostListMethod(listparam, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                List<BaseModel> results = new ArrayList<>();
                for (int i=0; i<result.size(); i++){
                    //results.add(Constants.getResponeApiJson((JSONObject) result.get(i)));
                }
                listener.onResponse(results);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError();
            }
        });

    }


    public static void getAllData(final CallbackListCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String[] url =new String[]{
                Api_link.STATUS+ String.format(Api_link.DEFAULT_RANGE, 1,20),
                Api_link.DISTRICTS + Distributor.getLocationId(),
                Api_link.PRODUCT_GROUPS+ String.format(Api_link.DEFAULT_RANGE, 1,10),
                Api_link.PRODUCTS+ String.format(Api_link.DEFAULT_RANGE, 1,500),
                Api_link.PROVINCES};

        new CustomGetListMethod(url, new CallbackListCustom() {
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

    public static void getDistrict(int provinceId, final CallbackCustomList listener){
        //Util.getInstance().showLoading();

        String url = Api_link.DISTRICTS+provinceId;
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                //Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                //Util.getInstance().stopLoading(stopLoading);
                Constants.throwError(error);
                listener.onError(error);

            }


//            @Override
//            public void onResponse(JSONObject result) {
//                Util.getInstance().stopLoading(true);
//                try {
//                    if (result.getInt("status") == 200) {
//                        listener.onResponse(result.getJSONArray("data"));
//
//                    } else {
//                        listener.onError("Unknow error");
//                    }
//                } catch (JSONException e) {
//                    listener.onError(e.toString());
//                    Util.getInstance().stopLoading(true);
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//                listener.onError(error);
//                Util.getInstance().stopLoading(true);
//            }
        }).execute();

    }

    public static void loadListObject(List<BaseModel> listParam, CallbackListCustom listener, final Boolean stopLoading){

        new CustomGetPostListMethod(listParam, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                Util.getInstance().stopLoading(stopLoading);

//                for (int i=0; i<result.size(); i++){
//                    if (Constants.responeIsSuccess((BaseModel) result.get(i))){
//                        listener.onResponse(Constants.getResponeArraySuccess(result));
//
//                    }else {
//                        Constants.throwError(result.getString("message"));
//                        listener.onError(result.getString("message"));
//
//                    }
//                }


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
