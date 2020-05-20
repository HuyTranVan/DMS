//package wolve.dms.libraries.connectapi;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.ProtocolException;
//import java.net.URL;
//
//import wolve.dms.callback.Callback;
//import wolve.dms.models.Distributor;
//import wolve.dms.models.User;
//import wolve.dms.utils.Util;
//
///**
// * Created by tranhuy on 7/22/16.
// */
//public class CustomPutMethod extends AsyncTask<String, Void, String> {
//    private Callback mListener = null;
//    private String baseUrl, mParams;
//    private Boolean isJsonType= false;
//
//    public CustomPutMethod(String url,String params,Boolean isJsonType, Callback listener) {
//        mListener = listener;
//        this.baseUrl = url;
//        this.mParams = params;
//        this.isJsonType = isJsonType;
//
//    }
//
//    @Override protected String doInBackground(String... params) {
//        Log.d("url: ", baseUrl);
//        Log.d("params: ", mParams);
//
//        String url = baseUrl;
//        URL obj = null;
//        StringBuffer response = null;
//        try {
//            obj = new URL(baseUrl);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//            con.setReadTimeout(5000);
//            con.setConnectTimeout(5000);
//            con.setRequestMethod("PUT");
//            con.setRequestProperty("User-Agent", "Mozilla/5.0");
//            con.setRequestProperty("Content-Type",isJsonType? "application/json":"application/x-www-form-urlencoded");
//            con.setRequestProperty("x-wolver-accesstoken", User.getToken());
//            con.setRequestProperty("x-wolver-accessid", User.getUserId());
////            con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());
//
//            String token = User.getToken() + User.getUserId();
//
//            String urlParameters = mParams;
//
//            // Send post request
//            con.setDoOutput(true);
//            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            wr.writeBytes(urlParameters);
//            wr.flush();
//
//
//            int responseCode = con.getResponseCode();
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//            wr.close();
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return "errorCode: 01";
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//            return "errorCode: 02";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "errorCode: 03";
//        }
//        System.out.println("output: "+String.valueOf(response));
//
//
//        return String.valueOf(response);
//    }
//
//    @Override protected void onPostExecute(String response) {
//        if (response.contains("errorCode")){
//            mListener.onError(response);
//            if (response.contains("errorCode: 01")){
//                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
//            }else if (response.contains("errorCode: 02")){
//                Util.getInstance().showSnackbar("Lỗi đường truyền ", null, null);
//            }else if (response.contains("errorCode: 03")){
//                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
//            }
//        }else {
//            try {
//                mListener.onResponse(new JSONObject(response));
//
//            } catch (JSONException e) {
//                mListener.onError("Data error");
//                Util.getInstance().showSnackbar("Lỗi dữ liệu", null, null);
//            }
//        }
//
//    }
//}
//
//
