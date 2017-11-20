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
    private String baseUrl;
    private String boundary;
    private JSONObject param;
    private String response = "";

    public CustomPostMultiPart(String url, JSONObject params, Callback listener) {
        this.mListener = listener;
        this.baseUrl = url;
        this.param = params;

//        User currentUser = User.getCurrentUser();
//        if (currentUser != null && currentUser.getToken() != null) {
//            token = currentUser.getToken();
//            id_user = currentUser.getId_user();
//        }
    }

    @Override protected String doInBackground(String... params) {
        Log.d("url: ", baseUrl);
        Log.d("params: ", param.toString());
        //Open connection

        boundary = "===" + System.currentTimeMillis() + "===";
        try {
            URL url = new URL(baseUrl);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpConn.setRequestProperty("x-wolver-accesstoken", User.getToken());
            httpConn.setRequestProperty("x-wolver-accessid", User.getUserId());

            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

            File file = new File(param.getString("image"));
            addFilePart("image", file);

            addFormField("promotion", param.getString("promotion"));
            addFormField("unitPrice", param.getString("unitPrice"));
            addFormField("purchasePrice", param.getString("purchasePrice"));
            addFormField("volume", param.getString("volume"));
            addFormField("productGroup.id", param.getString("productGroup.id"));

            response = finish();



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override protected void onPostExecute(String response) {
        if (response.contains("errorCode")){
            mListener.onError(response);
            if (response.contains("errorCode: 01")){
                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
            }else if (response.contains("errorCode: 02")){
                Util.getInstance().showSnackbar("Lỗi đường truyền ", null, null);
            }else if (response.contains("errorCode: 03")){
                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
            }
        }else {
            try {
                mListener.onResponse(new JSONObject(response));

            } catch (JSONException e) {
                mListener.onError("Data error");
                Util.getInstance().showSnackbar("Lỗi dữ liệu", null, null);
            }
        }

    }

    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addFilePart(String fieldName, final File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        int countByte = 0;
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);

//            if(progressListener != null) {
//                countByte += bytesRead;
//                int progress = (int)((countByte / (float) uploadFile.length()) * 100);
//                progressListener.progress(progress);
//            }
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();

//        if(progressListener != null) {
//            progressListener.progress(100);
//        }
    }


    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    public String finish() throws IOException {
        StringBuffer response = new StringBuffer();

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        // checks server's status code first
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

        return response.toString();
    }


}


