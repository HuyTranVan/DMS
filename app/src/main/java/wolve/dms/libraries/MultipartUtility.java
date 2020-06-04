package wolve.dms.libraries;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import wolve.dms.models.User;

public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    String charset = "UTF-8";
    private OutputStream outputStream;
    private PrintWriter writer;

    public MultipartUtility(String requestURL) throws IOException {
//        User currentUser = User.getCurrentUser();
//        if (currentUser != null && currentUser.getToken() != null) {
//            token = currentUser.getToken();
//            id_user = currentUser.getId_user();
//        }

        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        Log.e("URL", "URL : " + requestURL.toString());
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("x-wolver-accesstoken", User.getToken());
        httpConn.setRequestProperty("x-wolver-accessid", User.getUserId());

        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

//    public void addFilePart(String fieldName, final File uploadFile, final CallbackProgress progressListener) throws IOException {
//        String fileName = uploadFile.getName();
//        writer.append("--" + boundary).append(LINE_FEED);
//        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
//        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
//        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
//        writer.append(LINE_FEED);
//        writer.flush();
//
//        int countByte = 0;
//        FileInputStream inputStream = new FileInputStream(uploadFile);
//        byte[] buffer = new byte[4096];
//        int bytesRead = -1;
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, bytesRead);
//
//            if(progressListener != null) {
//                countByte += bytesRead;
//                int progress = (int)((countByte / (float) uploadFile.length()) * 100);
//                progressListener.progress(progress);
//            }
//        }
//        outputStream.flush();
//        inputStream.close();
//
//        writer.append(LINE_FEED);
//        writer.flush();
//
//        if(progressListener != null) {
//            progressListener.progress(100);
//        }
//    }

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