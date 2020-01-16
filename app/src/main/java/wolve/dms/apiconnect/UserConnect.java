package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.libraries.connectapi.CustomPutMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class UserConnect {

    public static void Login(String params, final CallbackCustom listener, final Boolean showloading, final Boolean stopLoading){
        Util.getInstance().showLoading(showloading);

        new CustomPostMethod(DataUtil.postLoginParam(params),  new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(stopLoading);
                Constants.throwError(error);
                listener.onError(error);

            }
        }).execute();
    }

//    public static void Logout(String params, final CallbackJSONObject listener, final Boolean stopLoading){
//        Util.getInstance().showLoading();
//        new CustomGetMethod(Api_link.LOGOUT,  new Callback() {
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
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(stopLoading);
//                listener.onError(error);
//            }
//        }).execute();
//    }

    public static void UpdateUser(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.USER + User.getUserId();

        new CustomPutMethod(url,params,false, new Callback() {
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

    public static void CreateUser(String params,final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        new CustomPostMethod(DataUtil.createNewUserParam(params),new CallbackCustom() {
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

    public static void doLogin(final String username, final String pass, final CallbackBoolean mListener) {
        String params = String.format(Api_link.LOGIN_PARAM, username, pass);

        UserConnect.Login(params, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel object) {
                CustomSQL.setString(Constants.DISTRIBUTOR, object.getString("distributor"));
                CustomSQL.setString(Constants.DISTRICT_LIST, object.getString("district"));
                CustomSQL.setString(Constants.USER_USERNAME, username);
                CustomSQL.setString(Constants.USER_PASSWORD, pass);
                object.removeKey("district");
                saveUser(object);

                object.removeKey("distributor");

                CustomSQL.setBaseModel(Constants.USER, object);
                if (object.getInt("role")== Constants.ROLE_ADMIN) {
                    CustomSQL.setBoolean(Constants.IS_ADMIN, true);
                } else {
                    CustomSQL.setBoolean(Constants.IS_ADMIN, false);
                }


                mListener.onRespone(true);

            }

            @Override
            public void onError(String error) {
                mListener.onRespone(false);

            }

        }, true, true);
    }

    public static void doChangePass(final String pass, final CallbackBoolean mListener){
        String params = String.format(Api_link.USER_PARAM, pass, Distributor.getId());

        UserConnect.UpdateUser(params, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                mListener.onRespone(true);
            }

            @Override
            public void onError(String error) {
                mListener.onRespone(false);
            }
        }, true);
    }

    private static void saveUser(BaseModel user){
        List<BaseModel> listUser = CustomSQL.getListObject(Constants.USER_LIST);

        if (!DataUtil.checkDuplicate(listUser, "id", user)){
            listUser.add(user);
        }

        CustomSQL.setListBaseModel(Constants.USER_LIST, listUser);

    }

    public static void ListUser(final CallbackCustomList listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.USERS;

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


}

