//package wolve.dms.libraries.connectapi;
//
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class CustomPostMultiPart extends AsyncTask<Object, String, String> {
//    String file_name = "";
//    private String baseUrl;
//    private Uri uri;
//
//    public CustomPostMultiPart(String url, Uri urifile) {
//        //this.mListener = listener;
//        this.baseUrl = url;
//        this.uri = urifile;
//
//    }
//
//    @Override
//    protected String doInBackground(Object[] params) {
//        try {
//            String lineEnd = "\r\n";
//            String twoHyphens = "--";
//            String boundary = "*****";
//            int bytesRead, bytesAvailable, bufferSize;
//            byte[] buffer;
//            int maxBufferSize = 1024 * 1024;
//            //todo change URL as per client ( MOST IMPORTANT )
////            URL url = new URL("http://webkul.com/upload");
//            URL url = new URL(baseUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            // Allow Inputs &amp; Outputs.
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setUseCaches(false);
//
//            // Set HTTP method to POST.
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Connection", "Keep-Alive");
//            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            FileInputStream fileInputStream;
//            DataOutputStream outputStream;
//            outputStream = new DataOutputStream(connection.getOutputStream());
//            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//
//            outputStream.writeBytes("Content-Disposition: form-data; name=\"reference\"" + lineEnd);
//            outputStream.writeBytes(lineEnd);
//            outputStream.writeBytes("my_refrence_text");
//            outputStream.writeBytes(lineEnd);
//            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//
//            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadFile\";filename=\"" + uri.getLastPathSegment() + "\"" + lineEnd);
//            outputStream.writeBytes(lineEnd);
//
//            fileInputStream = new FileInputStream(uri.getPath());
//            bytesAvailable = fileInputStream.available();
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            buffer = new byte[bufferSize];
//
//            // Read file
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0) {
//                outputStream.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            }
//            outputStream.writeBytes(lineEnd);
//            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            // Responses from the server (code and message)
//            int serverResponseCode = connection.getResponseCode();
//            String result = null;
//            if (serverResponseCode == 200) {
//                StringBuilder s_buffer = new StringBuilder();
//                InputStream is = new BufferedInputStream(connection.getInputStream());
//                BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                String inputLine;
//                while ((inputLine = br.readLine()) != null) {
//                    s_buffer.append(inputLine);
//                }
//                result = s_buffer.toString();
//            }
//            fileInputStream.close();
//            outputStream.flush();
//            outputStream.close();
//            if (result != null) {
//                Log.d("result_for upload", result);
//                //file_name = getDataFromInputStream(result, "file_name");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return file_name;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//    }
//}
///**
// * Created by tranhuy on 7/22/16.
// */
////public class CustomPostMultiPart extends AsyncTask<String, Void, String> {
////    private Callback mListener = null;
////
////    private static final String LINE_FEED = "\r\n";
////    private HttpURLConnection httpConn;
////    private String charset ="UTF-8";
////    private OutputStream outputStream;
////    private PrintWriter writer;
////    private String baseUrl;
////    private String boundary;
////    private JSONObject param;
////    private String response = "";
////
////    public CustomPostMultiPart(String url, JSONObject params, Callback listener) {
////        this.mListener = listener;
////        this.baseUrl = url;
////        this.param = params;
////
//////        User currentUser = User.getCurrentUser();
//////        if (currentUser != null && currentUser.getToken() != null) {
//////            token = currentUser.getToken();
//////            id_user = currentUser.getId_user();
//////        }
////    }
////
////    @Override protected String doInBackground(String... params) {
////        Log.d("url: ", baseUrl);
////        Log.d("params: ", param.toString());
////        //Open connection
////
////        boundary = "===" + System.currentTimeMillis() + "===";
////        try {
////            URL url = new URL(baseUrl);
////            httpConn = (HttpURLConnection) url.openConnection();
////            httpConn.setUseCaches(false);
////            httpConn.setDoOutput(true); // indicates POST method
////            httpConn.setDoInput(true);
////            httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
////            httpConn.setRequestProperty("x-wolver-accesstoken", User.getToken());
////            httpConn.setRequestProperty("x-wolver-accessid", User.getUserId());
////
////            outputStream = httpConn.getOutputStream();
////            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
////
////            File file = new File(param.getString("image"));
////            addFilePart("image", file);
////
////            addFormField("promotion", param.getString("promotion"));
////            addFormField("unitPrice", param.getString("unitPrice"));
////            addFormField("purchasePrice", param.getString("purchasePrice"));
////            addFormField("volume", param.getString("volume"));
////            addFormField("productGroup.id", param.getString("productGroup.id"));
////
////            response = finish();
////
////
////
////        } catch (MalformedURLException e) {
////            e.printStackTrace();
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////
////        return response;
////    }
////
////    @Override protected void onPostExecute(String response) {
////        if (response.contains("errorCode")){
////            mListener.onError(response);
////            if (response.contains("errorCode: 01")){
////                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
////            }else if (response.contains("errorCode: 02")){
////                Util.getInstance().showSnackbar("Lỗi đường truyền ", null, null);
////            }else if (response.contains("errorCode: 03")){
////                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
////            }
////        }else {
////            try {
////                mListener.onResponse(new JSONObject(response));
////
////            } catch (JSONException e) {
////                mListener.onError("Data error");
////                Util.getInstance().showSnackbar("Lỗi dữ liệu", null, null);
////            }
////        }
////
////    }
////
////    public void addFormField(String name, String value) {
////        writer.append("--" + boundary).append(LINE_FEED);
////        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
////        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
////        writer.append(LINE_FEED);
////        writer.append(value).append(LINE_FEED);
////        writer.flush();
////    }
////
////    public void addFilePart(String fieldName, final File uploadFile) throws IOException {
////        String fileName = uploadFile.getName();
////        writer.append("--" + boundary).append(LINE_FEED);
////        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
////        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
////        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
////        writer.append(LINE_FEED);
////        writer.flush();
////
////        int countByte = 0;
////        FileInputStream inputStream = new FileInputStream(uploadFile);
////        byte[] buffer = new byte[4096];
////        int bytesRead = -1;
////        while ((bytesRead = inputStream.read(buffer)) != -1) {
////            outputStream.write(buffer, 0, bytesRead);
////
//////            if(progressListener != null) {
//////                countByte += bytesRead;
//////                int progress = (int)((countByte / (float) uploadFile.length()) * 100);
//////                progressListener.progress(progress);
//////            }
////        }
////        outputStream.flush();
////        inputStream.close();
////
////        writer.append(LINE_FEED);
////        writer.flush();
////
//////        if(progressListener != null) {
//////            progressListener.progress(100);
//////        }
////    }
////
////
////    public void addHeaderField(String name, String value) {
////        writer.append(name + ": " + value).append(LINE_FEED);
////        writer.flush();
////    }
////
////    public String finish() throws IOException {
////        StringBuffer response = new StringBuffer();
////
////        writer.append(LINE_FEED).flush();
////        writer.append("--" + boundary + "--").append(LINE_FEED);
////        writer.close();
////
////        // checks server's status code first
////        int status = httpConn.getResponseCode();
////        if (status == HttpURLConnection.HTTP_OK) {
////            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
////            String line = null;
////            while ((line = reader.readLine()) != null) {
////                response.append(line);
////            }
////            reader.close();
////            httpConn.disconnect();
////        } else {
////            throw new IOException("Server returned non-OK status: " + status);
////        }
////
////        return response.toString();
////    }
////
////
////}
//
//
