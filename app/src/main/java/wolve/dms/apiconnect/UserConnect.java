package wolve.dms.apiconnect;

import java.util.List;

import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomFixSQL;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class UserConnect {

    public static void Login(String params, final CallbackCustom listener, final Boolean showloading, final Boolean stopLoading) {
        Util.getInstance().showLoading(showloading);

        new CustomPostMethod(DataUtil.postLoginParam(params), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)) {
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                } else {
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

//    public static void Logout(final CallbackCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//        new CustomGetMethod(Api_link.LOGOUT, new CallbackCustom(){
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(stopLoading);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//        }).execute();
//    }

    public static void CreateUser(String params, final CallbackCustom listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        new CustomPostMethod(DataUtil.createNewUserParam(params), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)) {
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                } else {
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

    public static void doLogin(final String username, final String pass, String fcm_token, final CallbackBoolean mListener) {
        String params = String.format(Api_link.LOGIN_PARAM, username, pass, fcm_token);

        UserConnect.Login(params, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel object) {
                CustomSQL.setString(Constants.DISTRIBUTOR, object.getString("distributor"));
                CustomSQL.setString(Constants.DISTRICT_LIST, object.getString("district"));
                CustomSQL.setString(Constants.USER_USERNAME, username);
                CustomSQL.setString(Constants.USER_PASSWORD, pass);
                CustomSQL.setInt(Constants.VERSION_CODE, CustomSQL.getInt(Constants.VERSION_CODE));
                object.removeKey("district");
                saveUser(object);

                object.removeKey("distributor");

                CustomSQL.setBaseModel(Constants.USER, object);
                if (object.getInt("role") == Constants.ROLE_ADMIN) {
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

    public static void doChangePass(String current_pass, final String new_pass, final CallbackCustom listener, boolean showloading) {
        Util.getInstance().showLoading(showloading);

        String params = String.format(Api_link.USER_CHANGE_PASS_PARAM, User.getId(), current_pass, new_pass);
        new CustomPostMethod(DataUtil.createUserChangePassParam(params), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)) {
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                } else {
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

    public static void setDefaultPassword(int user_id, final CallbackCustom listener, boolean showloading) {
        Util.getInstance().showLoading(showloading);

        String params = String.format(Api_link.USER_DEFAULT_PASS_PARAM, user_id);
        new CustomPostMethod(DataUtil.createUserDefaultPassParam(params), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)) {
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                } else {
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

    private static void saveUser(BaseModel user) {
        List<BaseModel> listUser = CustomFixSQL.getListObject(Constants.USER_LIST);

        if (!DataUtil.checkDuplicate(listUser, "id", user)) {
            listUser.add(user);
        }

        CustomFixSQL.setListBaseModel(Constants.USER_LIST, listUser);

    }

    public static void ListUser(final CallbackCustomList listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        String url = Api_link.USERS;

        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)) {
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                } else {
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

