package wolve.dms.libraries;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import wolve.dms.callback.Callback;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomPostMultiPart extends AsyncTask<String, Void, String> {
    private Callback mListener = null;

    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset ="UTF-8";
    private OutputStream outputStream;
    private PrintWriter writer;
    private String baseUrl,token, id_user, fieldName;
    private File fileUpload;

    public CustomPostMultiPart(String url, String fieldName, File uploadFile, Callback listener) {
        this.mListener = listener;
        this.baseUrl = url;
        this.fieldName = fieldName;
        this.fileUpload = uploadFile;

        User currentUser = User.getCurrentUser();
        if (currentUser != null && currentUser.getToken() != null) {
            token = currentUser.getToken();
            id_user = currentUser.getId_user();
        }
    }

    @Override protected String doInBackground(String... params) {
        Log.d("url: ", baseUrl);
        Log.d("token: ", token);
        Log.d("id_nv: ", id_user);
        Log.d("params: ", fieldName);
        String boundary = "===" + System.currentTimeMillis() + "===";
        StringBuffer response = null;
        try {
            URL url = new URL(baseUrl);
            Log.e("URL", "URL : " + baseUrl.toString());
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setReadTimeout(5000);
            httpConn.setConnectTimeout(5000);
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "===" + System.currentTimeMillis() + "===");
            httpConn.setRequestProperty("x-wolver-accesstoken", token);
            httpConn.setRequestProperty("x-wolver-accessid", id_user);

            httpConn.setRequestProperty(token, id_user);


            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

            String fileName = fileUpload.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            int countByte = 0;
            FileInputStream inputStream = new FileInputStream(fileUpload);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);

            }
            outputStream.flush();
            inputStream.close();

//            writer.append(LINE_FEED);
//            writer.flush();


            response = new StringBuffer();

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();


            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "errorCode: 01";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "errorCode: 02";
        } catch (IOException e) {
            e.printStackTrace();
            return "errorCode: 03";
        }

        Log.d("output: ", String.valueOf(response));
        return String.valueOf(response);
    }

    @Override protected void onPostExecute(String response) {
        if (response.contains("errorCode")){
            mListener.onError(response);
            if (response.contains("errorCode: 01")){
                Util.getInstance().quickMessage("Lỗi kết nối server ", null, null);
            }else if (response.contains("errorCode: 02")){
                Util.getInstance().quickMessage("Lỗi đường truyền ", null, null);
            }else if (response.contains("errorCode: 03")){
                Util.getInstance().quickMessage("Lỗi kết nối server ", null, null);
            }
        }else {
            try {
                mListener.onResponse(new JSONObject(response));

            } catch (JSONException e) {
                mListener.onError("Data error");
                Util.getInstance().quickMessage("Lỗi dữ liệu", null, null);
            }
        }

    }

}


