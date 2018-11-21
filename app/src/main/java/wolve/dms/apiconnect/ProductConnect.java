package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.libraries.connectapi.CustomDeleteMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ProductConnect {

    public static void ListProductGroup(Boolean loading, final CallbackJSONArray listener, final Boolean stopLoading){
        if (loading)
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

    public static void CreateProduct(final String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();
        final String url = Api_link.PRODUCT_NEW ;
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

//            new AsyncTask() {
//                @Override
//                protected Object doInBackground(Object[] objects) {
//                    String id_user = null, token = null;
//
////                    User currentUser = User.getCurrentUser();
////                    if (currentUser != null && currentUser.getToken() != null) {
////                        token = currentUser.getToken();
////                        id_user = currentUser.getId_user();
////                    }
//                    try {
//                        File file = new File(params.getString("image"));
//                        String s = multipartRequest(url, params, file.getAbsolutePath(), "image", "jpeg", token, id_user);
//                        System.out.print(s);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }
//            }.execute();
//        new CustomPostMethodMulti(url,params, new Callback() {
//            @Override
//            public void onResponse(JSONObject result) {
//                Util.getInstance().dismissDialog(dismissDialog);
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
//                Util.getInstance().dismissDialog(dismissDialog);
//            }
//        }).execute();
    }

//    public static void CreateProductMultipart(JSONObject params, final CallbackJSONObject listener, final Boolean dismissDialog){
//        Util.getInstance().showLoading();
//        String url = Api_link.PRODUCT_NEW ;
//
//        new CustomPostMultiPart(url,params, new Callback() {
//            @Override
//            public void onResponse(JSONObject result) {
//                Util.getInstance().dismissDialog(dismissDialog);
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
//                Util.getInstance().dismissDialog(dismissDialog);
//            }
//        }).execute();
//    }

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


    public static String multipartRequest(String urlTo, JSONObject parmas, String filepath, String filefield, String fileMimeType, String token, String id) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("x-wolver-accesstoken", token);
            connection.setRequestProperty("x-wolver-accessid", id);

            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = parmas.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.getString(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                System.out.print("aaaaa" + connection.getResponseCode());
            }

            inputStream = connection.getInputStream();

            result = convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
