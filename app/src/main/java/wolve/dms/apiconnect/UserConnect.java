package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.connectapi.CustomDeleteMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.libraries.connectapi.CustomPutMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class UserConnect {

    public static void Login(String params, final CallbackJSONObject listener,final Boolean showloading, final Boolean stopLoading){
        if (showloading)
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

    public static void Logout(String params, final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();
        new CustomGetMethod(Api_link.LOGOUT,  new Callback() {
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

    public static void doLogin(final String username, final String pass, final CallbackBoolean mListener){
        String params = String.format(Api_link.LOGIN_PARAM,username, pass);

        UserConnect.Login(params, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                try {
                    User user = new User(result);
                    Distributor distributor = new Distributor(result.getJSONObject("distributor"));

                    CustomSQL.setObject(Constants.USER, user);
                    CustomSQL.setObject(Constants.DISTRIBUTOR, distributor);
                    CustomSQL.setString(Constants.USER_USERNAME, username);
                    CustomSQL.setString(Constants.USER_PASSWORD, pass);

                    if (User.getRole().equals(Constants.ROLE_ADMIN)){
                        CustomSQL.setBoolean(Constants.IS_ADMIN , true);
                    }else {
                        CustomSQL.setBoolean(Constants.IS_ADMIN , false);
                    }

                    saveUser(new BaseModel(result));

                    mListener.onRespone(true);
//                    Util.showToast("Đăng nhập thành công");
//                    Transaction.gotoHomeActivity(true);


                } catch (JSONException e) {
//                    e.printStackTrace();
                    mListener.onRespone(false);
                }
            }

            @Override
            public void onError(String error) {

            }
        }, true,true);
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

        boolean dup = false;
        for (int i=0; i< listUser.size(); i++){
            if (listUser.get(i).getString("id").equals(user.getString("id"))){
                dup = true;
                break;
            }
        }

        if (!dup){
            listUser.add(user);
        }

        CustomSQL.setListBaseModel(Constants.USER_LIST, listUser);

    }


}

