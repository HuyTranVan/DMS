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

public class ProductConnect {

    public static void ListProductGroup(Boolean loading, final CallbackCustomList listener, final Boolean stopLoading){
        if (loading)
            Util.getInstance().showLoading();

        String url = Api_link.PRODUCT_GROUPS+ String.format(Api_link.DEFAULT_RANGE, 1,10);

        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));
                    Util.getInstance().stopLoading(true);
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

    public static void CreateProductGroup(String params,final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        new CustomPostMethod(DataUtil.createNewProductGroupParam(params),new CallbackCustom() {
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

    public static void DeleteProductGroup(String params,final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCT_GROUP_DELETE + params;

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

    public static void ListProduct(final CallbackCustomList listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCTS+ String.format(Api_link.DEFAULT_RANGE, 1,500);

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

    public static void CreateProduct(final String params, CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        new CustomPostMethod(DataUtil.createNewProductParam(params),new CallbackCustom() {
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

    public static void DeleteProduct(String params,final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.PRODUCT_DELETE + params;

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
