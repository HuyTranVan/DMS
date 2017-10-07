package wolve.dms.libraries;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import wolve.dms.apiconnect.Api_link;
import wolve.dms.callback.Callback;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomPostMultiPartMethod extends AsyncTask<String, Void, String> {
    private Callback mListener = null;
    private String baseUrl, mParams;
    private Uri uri;
    private String token ="";
    private String id_user ="";
    String result = "";


    public CustomPostMultiPartMethod(String url, String params, Uri uri, Callback listener) {
        mListener = listener;
        this.baseUrl = url;
        this.mParams = params;
        this.uri = uri;

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
        Log.d("params: ", mParams);

        try {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            //todo change URL as per client ( MOST IMPORTANT )
            URL url = new URL(Api_link.CUSTOMER_NEW);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("x-wolver-accesstoken", token);
            connection.setRequestProperty("x-wolver-accessid", id_user);

            FileInputStream fileInputStream;
            DataOutputStream outputStream;
            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            outputStream.writeBytes("Content-Disposition: form-data; name=\"reference\""+ lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("my_refrence_text");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadFile\";filename=\"" + uri.getLastPathSegment() +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            fileInputStream = new FileInputStream(uri.getPath());
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();

            if (serverResponseCode == 200) {
                StringBuilder s_buffer = new StringBuilder();
                InputStream is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    s_buffer.append(inputLine);
                }
                result = s_buffer.toString();
            }
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            if (result != null) {
                Log.e("result_for upload", result);
                //file_name = getDataFromInputStream(result, "file_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    return result;
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


