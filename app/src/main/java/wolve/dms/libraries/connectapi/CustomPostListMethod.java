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

import wolve.dms.callback.CallbackListCustom;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomPostListMethod extends AsyncTask<String, Void, List<String>> {
    private CallbackListCustom mListener = null;
    private String baseUrl;
    private List<String> mParams;

    private Boolean isJsonType= false;

    public CustomPostListMethod(String url, List<String> listParams, Boolean isJsonType, CallbackListCustom listener) {
        mListener = listener;
        this.baseUrl = url;
        this.mParams = listParams;
        this.isJsonType = isJsonType;

    }

    @Override
    protected List<String> doInBackground(String... s) {
        Log.d("url: ", baseUrl);
        Log.d("params: ", mParams.toString());

        List<String> listResult = new ArrayList<>();
        for (int i=0; i<mParams.size(); i++) {

            try {
                URL obj = new URL(baseUrl);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Content-Type", isJsonType ? "application/json" : "application/x-www-form-urlencoded");
                con.setRequestProperty("x-wolver-accesstoken", User.getToken());
                con.setRequestProperty("x-wolver-accessid", User.getUserId());
                con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

                DataOutputStream wr = null;
                BufferedReader in = null;
                con.setDoOutput(true);


                wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(mParams.get(i));
                wr.flush();

                int responseCode = con.getResponseCode();
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                listResult.add(String.valueOf(response));


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
        }
        //System.out.println("output: "+String.valueOf(response));


        return listResult;
    }

    @Override protected void onPostExecute(List<String> responses) {
        List<String> listResult = new ArrayList<>();
        if (responses == null){
            mListener.onError("Lỗi kết nối server ");
            Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);

        }else {
            for (int i=0; i<responses.size(); i++) {
                try {
                    JSONObject object = new JSONObject(responses.get(i));
                    if (object.getInt("status") == 200) {
                        listResult.add(object.getString("data"));

                    } else {
                        Util.getInstance().showSnackbar("Lỗi dữ liệu", null, null);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            mListener.onResponse(listResult);
        }

    }



}


