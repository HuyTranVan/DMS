package wolve.dms.utils;

import android.content.DialogInterface;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import wolve.dms.callback.Callback;
import wolve.dms.libraries.RestClientHelper;

import static wolve.dms.apiconnect.Api_link.BASE_PHOTO_URL;
import static wolve.dms.apiconnect.Api_link.BASE_URL;


/**
 * Created by Engine on 12/26/2016.
 */

public class ApiHelper {

    public static String TokenLink() {
        //User currentUser = User.getCurrentUser();
//        if (currentUser != null && currentUser.getToken() != null) {
//            return "?access_token=" + currentUser.getToken();
//        } else {
//            return "?access_token=0108ae52892cf1970c7e1813e227ce2161b9653a734558fffef5fe8ee61ec2f5";
//        }

        return "";
    }

    public static void AfterNoConnection() {
//        Activity act = Util.getInstance().getCurrentActivity();
//        if (act.getClass() == MainActivity.class) {
//            ((MainActivity) act).NoConnection();
//        }
    }

    public static void DisplayError(String error) {
        Util.getInstance().stopLoading(true);
        if (!Util.isInternetAvailable()) {
            Util.showSnackbar("Không có kết nối.", null, null);
            AfterNoConnection();
            return;
        }
        new AlertDialog.Builder(Util.getInstance().getCurrentActivity())
                .setTitle("Có lỗi xảy ra!!!")
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void GetJson(String url, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }

        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + TokenLink());
        RestClientHelper.getInstance().get(BASE_URL + url + TokenLink(), new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                DisplayError(error);
            }
        });
    }

    public static void GetJsonFullUrl(String url, final Callback callback) {
        Log.d(Constants.DMS_LOGS, "Called: " + url);
        RestClientHelper.getInstance().get( url , new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                DisplayError(error);
            }
        });
    }

    public static void GetJsonNotShowLoading(String url, final Callback callback) {
        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + TokenLink());
        RestClientHelper.getInstance().get(BASE_URL + url + TokenLink(), new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
            }

            @Override
            public void onError(String error) {
                DisplayError(error);
            }
        });
    }

//    public static void GetJsonReturnErr(String url, final Callback callback) {
//        if (callback.isShowLoading()) {
//            Util.getInstance().showLoading();
//        }
//
//        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + TokenLink());
//        RestClientHelper.getInstance().get(BASE_URL + url + TokenLink(), new RestClientHelper.RestClientListener() {
//            @Override
//            public void onSuccess(String response) {
//                JSONObject jsonObj = null;
//                try {
//                    jsonObj = new JSONObject(response);
//                } catch (JSONException e) {
//                    Util.alert(Constants.RESULT_ERROR_TITLE, response, "Đóng");
//                    e.printStackTrace();
//                }
//                try {
//                    if (jsonObj.getBoolean(Constants.RESULT) == Constants.RESULT_FALSE) {
//                        //Util.alert(Constants.RESULT_ERROR_TITLE, jsonObj.getString(Constants.RESULT_MESSAGE), "Đóng");
//                        callback.onError();
//                        Util.getInstance().stopLoading();
//
//                        return;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                callback.onResponse(jsonObj);
//                Util.getInstance().stopLoading();
//            }
//
//            @Override
//            public void onError(String error) {
//                DisplayError(error);
//                //callback.onError();
//            }
//        });
//    }
//
//    public static void GetJsonReturnErrLostPass(String url, final Callback callback) {
//        if (callback.isShowLoading()) {
//            Util.getInstance().showLoading();
//        }
//
//        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + TokenLink());
//        RestClientHelper.getInstance().get(BASE_URL + url + TokenLink(), new RestClientHelper.RestClientListener() {
//            @Override
//            public void onSuccess(String response) {
//                JSONObject jsonObj = null;
//                try {
//                    jsonObj = new JSONObject(response);
//                } catch (JSONException e) {
//                    Util.alert(Constants.RESULT_ERROR_TITLE, response, "Đóng");
//                    e.printStackTrace();
//                }
//                try {
//                    if (jsonObj.getBoolean(Constants.RESULT) == Constants.RESULT_FALSE) {
//                        Util.alert(Constants.RESULT_ERROR_TITLE, jsonObj.getString(Constants.RESULT_MESSAGE), "Đóng");
//                        //callback.onError();
//                        Util.getInstance().stopLoading();
//
//                        return;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                callback.onResponse(jsonObj);
//                Util.getInstance().stopLoading();
//            }
//
//            @Override
//            public void onError(String error) {
//                DisplayError(error);
//                //callback.onError();
//            }
//        });
//    }

    public static void PostJson(String url, ArrayMap<String, Object> params, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }
        Log.d("datapost", new Gson().toJson(params));
        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + TokenLink());
        RestClientHelper.getInstance().post(BASE_URL + url + TokenLink(), params, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, response, "Đóng");
                    e.printStackTrace();
                }
                try {
                    if (jsonObj.getBoolean(Constants.RESULT) == Constants.RESULT_FALSE) {
                        CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, jsonObj.getString(Constants.RESULT_MESSAGE), "Đóng");
                        Util.getInstance().stopLoading(true);
                        callback.onError("");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                DisplayError(error);
            }
        });
    }

    public static void PostJsonString(String url, ArrayMap<String, Object> params, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }
        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + TokenLink());
        RestClientHelper.getInstance().postString(BASE_URL + url + TokenLink(), params, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, response, "Đóng");
                    e.printStackTrace();
                }
                try {
                    if (jsonObj.getBoolean(Constants.RESULT) == Constants.RESULT_FALSE) {
                        CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, jsonObj.getString(Constants.RESULT_MESSAGE), "Đóng");
                        Util.getInstance().stopLoading(true);
                        callback.onError("");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                DisplayError(error);
            }
        });
    }


    public static void PostJson(String url, String noToken, ArrayMap<String, Object> params, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }
        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + noToken);
        RestClientHelper.getInstance().post(BASE_URL + url + TokenLink(), params, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, response, "Đóng");
                    e.printStackTrace();
                }
                try {
                    if (jsonObj.getBoolean(Constants.RESULT) == Constants.RESULT_FALSE) {
                        CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, jsonObj.getString(Constants.RESULT_MESSAGE), "Đóng");
                        Util.getInstance().stopLoading(true);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                DisplayError(error);
            }
        });
    }

    public static void PutJson(String url, ArrayMap<String, Object> params, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }
        Log.d(Constants.DMS_LOGS, "Called: " + BASE_URL + url + TokenLink());
        RestClientHelper.getInstance().put(BASE_URL + url + TokenLink(), params, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, response, "Đóng");
                    e.printStackTrace();
                }
                try {
                    if (jsonObj.getBoolean(Constants.RESULT) == Constants.RESULT_FALSE) {
                        CustomCenterDialog.alert(Constants.RESULT_ERROR_TITLE, jsonObj.getString(Constants.RESULT_MESSAGE), "Đóng");
                        Util.getInstance().stopLoading(true);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {

                DisplayError(error);

            }
        });
    }

    public static void DeleteJson(String url, ArrayMap<String, Object> params, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }
        RestClientHelper.getInstance().delete(BASE_URL + url + TokenLink(), params, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                DisplayError(error);
            }
        });
    }

    public static void UploadFile(JSONObject obj, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("result", true);
            jsonObj.put("data", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callback.onResponse(jsonObj);
        Util.getInstance().stopLoading(true);
    }
    public static void UploadFile(String url, final JSONObject photoObj, ArrayMap<String, Object> params, ArrayMap<String, File> files, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }
        RestClientHelper.getInstance().postMultipart(BASE_PHOTO_URL + url + TokenLink(), params, files, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                    photoObj.put("link", jsonObj.getJSONObject("data").getString("link"));

                    jsonObj.put("result", true);
                    jsonObj.put("data", photoObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                callback.onError("");
                DisplayError(error);
            }
        });
    }

    public static void UploadImage(String url, ArrayMap<String, Object> params, ArrayMap<String, File> files, final Callback callback) {
        if (callback.isShowLoading()) {
            Util.getInstance().showLoading();
        }
        Log.d(Constants.DMS_LOGS, "Called: " + BASE_PHOTO_URL + url + TokenLink());
        RestClientHelper.getInstance().postMultipart(BASE_PHOTO_URL + url + TokenLink(), params, files, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callback.onResponse(jsonObj);
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                callback.onError("");
                DisplayError(error);
            }
        });
    }

}
