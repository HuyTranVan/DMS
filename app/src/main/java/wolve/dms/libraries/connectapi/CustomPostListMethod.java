package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackList;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomPostListMethod extends AsyncTask<List<String>, Void, List<String>> {
    private CallbackList mListener = null;
    private String baseUrl;
    private List<String> mParams;

    private Boolean isJsonType= false;

    public CustomPostListMethod(String url, List<String> params, Boolean isJsonType, CallbackList listener) {
        mListener = listener;
        this.baseUrl = url;
        this.mParams = params;
        this.isJsonType = isJsonType;

    }

    @Override
    protected List<String> doInBackground(List<String>... lists) {
        Log.d("url: ", baseUrl);
        Log.d("params: ", mParams.toString());

        URL obj = null;
        StringBuffer response = null;
        List<String> listResult = new ArrayList<>();
        try {
            obj = new URL(baseUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Content-Type",isJsonType? "application/json":"application/x-www-form-urlencoded");
            con.setRequestProperty("x-wolver-accesstoken", User.getToken());
            con.setRequestProperty("x-wolver-accessid", User.getUserId());
            con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

            DataOutputStream wr = null;
            BufferedReader in = null;

            for (int i=0; i<mParams.size(); i++){
                con.setDoOutput(true);
                wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(mParams.get(i));
                wr.flush();

                int responseCode = con.getResponseCode();
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                listResult.add(String.valueOf(response));
            }

            in.close();
            wr.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            //return "errorCode: 01";
        } catch (ProtocolException e) {
            e.printStackTrace();
            //return "errorCode: 02";
        } catch (IOException e) {
            e.printStackTrace();
            //return "errorCode: 03";
        }
        System.out.println("output: "+String.valueOf(response));


        return listResult;
    }

    @Override protected void onPostExecute(List<String> response) {
        if (response.contains("errorCode")){
            //mListener.onError(response);
            if (response.contains("errorCode: 01")){
                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
            }else if (response.contains("errorCode: 02")){
                Util.getInstance().showSnackbar("Lỗi đường truyền ", null, null);
            }else if (response.contains("errorCode: 03")){
                Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);
            }
        }else {
            List<JSONObject> listResult = new ArrayList<>();
            try {
                for (int i=0; i<response.size(); i++){
                    JSONObject object = new JSONObject(response.get(i));
                    listResult.add(object);
                }
                mListener.onResponse(listResult);

            } catch (JSONException e) {
                mListener.onError("Data error");
                Util.getInstance().showSnackbar("Lỗi dữ liệu", null, null);
            }
        }

    }



}


